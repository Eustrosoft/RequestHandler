package com.eustrosoft.core.model;

import com.eustrosoft.core.model.interfaces.IDBObject;
import com.eustrosoft.core.model.interfaces.JsonFormat;
import com.eustrosoft.core.model.interfaces.ResultSetConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static com.eustrosoft.core.constants.DBConstants.ZLVL;
import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;
import static com.eustrosoft.core.constants.DBConstants.ZVER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBObject implements IDBObject, ResultSetConverter<DBObject>, JsonFormat {
    private Long zoid;
    private Long zver;
    private Long zrid;
    private Long zlvl;
    private Date created;

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }

    public DBObject(Long zoid, Long zver, Long zrid) {
        this.zoid = zoid;
        this.zver = zver;
        this.zrid = zrid;
    }

    public DBObject(Long zoid, Long zver, Long zrid, Date created) {
        this.zoid = zoid;
        this.zver = zver;
        this.zrid = zrid;
        this.created = created;
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("Result set is null while processing DBObject from ResultSet.");
        }
        try {
            setZoid(resultSet.getLong(ZOID));
        } catch (Exception ex) {
            System.out.println("ZOID not found.");
        }
        try {
            setZver(resultSet.getLong(ZVER));
        } catch (Exception ex) {
            System.out.println("ZVER not found.");
        }
        try {
            setZrid(resultSet.getLong(ZRID));
        } catch (Exception ex) {
            System.out.println("ZRID not found.");
        }
        try {
            setZlvl(resultSet.getLong(ZLVL));
        } catch (Exception ex) {
            System.out.println("ZLVL not found.");
        }
    }
}
