package org.eustrosoft.dic.model;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.db.model.DBObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DIC extends DBObject {
    private String dic;
    private String code;
    private String value;
    private String descr;

    public DIC() {
    }

    public DIC(Long id, Long zver, Long zrid,
               String dic, String code, String value, String descr) {
        super(id, zver, zrid);
        this.dic = dic;
        this.code = code;
        this.value = value;
        this.descr = descr;
    }

    @Override
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        String dic = resultSet.getString(DBConstants.DIC);
        if (dic != null && !dic.isEmpty()) {
            setDic(dic.trim());
        }
        String code = resultSet.getString(DBConstants.CODE);
        if (code != null && !code.isEmpty()) {
            setCode(code.trim());
        }
        String value = resultSet.getString(DBConstants.VALUE);
        if (value != null && !value.isEmpty()) {
            setValue(value.trim());
        }
        String descr = resultSet.getString(DBConstants.DESCR);
        if (descr != null && !descr.isEmpty()) {
            setDescr(descr.trim());
        }
    }

    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
