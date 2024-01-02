package org.eustrosoft.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.model.interfaces.IDBObject;
import org.eustrosoft.core.model.interfaces.JsonFormat;
import org.eustrosoft.core.model.interfaces.ResultSetConverter;
import org.eustrosoft.core.tools.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBObject implements IDBObject, ResultSetConverter<DBObject>, JsonFormat {
    private Long zoid;
    private Long zver;
    private Long zrid;
    private Long zsid;
    private Short zlvl;
    private String created;

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }

    public DBObject(Long zoid, Long zver, Long zrid) {
        this.zoid = zoid;
        this.zver = zver;
        this.zrid = zrid;
    }

    public DBObject(Long zoid, Long zver, Long zrid, Long zsid) {
        this.zoid = zoid;
        this.zver = zver;
        this.zrid = zrid;
        this.zsid = zsid;
    }

    public DBObject(Long zoid, Long zver, Long zrid, DateTimeZone created) {
        this.zoid = zoid;
        this.zver = zver;
        this.zrid = zrid;
        if (created != null) {
            this.created = created.toString();
        }
    }

    protected <T> void trySet(Consumer<T> function, ResultSet resultSet, String name) {
        try {
            function.accept((T) resultSet.getObject(name));
        } catch (Exception ex) {
            function.accept(null);
        }
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("Result set is null while processing DBObject from ResultSet.");
        }
        trySet(this::setZoid, resultSet, DBConstants.ZOID);
        trySet(this::setZver, resultSet, DBConstants.ZVER);
        trySet(this::setZrid, resultSet, DBConstants.ZRID);
        trySet(this::setZlvl, resultSet, DBConstants.ZLVL);
        trySet(this::setZsid, resultSet, DBConstants.ZSID);
    }
}
