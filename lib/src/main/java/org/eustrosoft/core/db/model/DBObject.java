package org.eustrosoft.core.db.model;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.date.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class DBObject implements IDBObject, ResultSetConverter<DBObject> {
    private Long ZOID;
    private Long ZVER;
    private Long ZRID;
    private Long ZSID;
    private Short ZLVL;
    private String created;

    public DBObject() {

    }

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }

    public DBObject(Long ZOID, Long ZVER, Long ZRID) {
        this.ZOID = ZOID;
        this.ZVER = ZVER;
        this.ZRID = ZRID;
    }

    public DBObject(Long ZOID, Long ZVER, Long ZRID, Long ZSID) {
        this.ZOID = ZOID;
        this.ZVER = ZVER;
        this.ZRID = ZRID;
        this.ZSID = ZSID;
    }

    public DBObject(Long ZOID, Long ZVER, Long ZRID, DateTimeZone created) {
        this.ZOID = ZOID;
        this.ZVER = ZVER;
        this.ZRID = ZRID;
        if (created != null) {
            this.created = created.toString();
        }
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("Result set is null while processing DBObject from ResultSet.");
        }
        trySet(this::setZOID, resultSet, DBConstants.ZOID);
        trySet(this::setZVER, resultSet, DBConstants.ZVER);
        trySet(this::setZRID, resultSet, DBConstants.ZRID);
        trySet(this::setZLVL, resultSet, DBConstants.ZLVL);
        trySet(this::setZSID, resultSet, DBConstants.ZSID);
    }

    protected <T> void trySet(Consumer<T> function, ResultSet resultSet, String name) {
        try {
            function.accept((T) resultSet.getObject(name));
        } catch (Exception ex) {
            function.accept(null);
        }
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public Long getZOID() {
        return ZOID;
    }

    public void setZOID(Long ZOID) {
        this.ZOID = ZOID;
    }

    @Override
    public Long getZVER() {
        return ZVER;
    }

    public void setZVER(Long ZVER) {
        this.ZVER = ZVER;
    }

    @Override
    public Long getZRID() {
        return ZRID;
    }

    public void setZRID(Long ZRID) {
        this.ZRID = ZRID;
    }

    @Override
    public Long getZSIC() {
        return ZSID;
    }

    public void setZSID(Long ZSID) {
        this.ZSID = ZSID;
    }

    @Override
    public Short getZLVL() {
        return ZLVL;
    }

    public void setZLVL(Short ZLVL) {
        this.ZLVL = ZLVL;
    }
}
