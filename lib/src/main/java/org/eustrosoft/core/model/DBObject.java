package org.eustrosoft.core.model;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.date.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class DBObject implements IDBObject, ResultSetConverter<DBObject> {
    private Long zoid;
    private Long zver;
    private Long zrid;
    private Long zsid;
    private Short zlvl;
    private String created;

    public DBObject() {

    }

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
    public Long getZoid() {
        return zoid;
    }

    public void setZoid(Long zoid) {
        this.zoid = zoid;
    }

    @Override
    public Long getZver() {
        return zver;
    }

    public void setZver(Long zver) {
        this.zver = zver;
    }

    @Override
    public Long getZrid() {
        return zrid;
    }

    public void setZrid(Long zrid) {
        this.zrid = zrid;
    }

    @Override
    public Long getZsid() {
        return zsid;
    }

    public void setZsid(Long zsid) {
        this.zsid = zsid;
    }

    @Override
    public Short getZlvl() {
        return zlvl;
    }

    public void setZlvl(Short zlvl) {
        this.zlvl = zlvl;
    }
}
