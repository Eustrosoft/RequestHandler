package com.eustrosoft.dbdatasource.util;

import com.eustrosoft.datasource.sources.model.CMSType;

import java.sql.ResultSet;

import static com.eustrosoft.dbdatasource.constants.DBConstants.ID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.SID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.TYPE;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZSID;

public final class ResultSetUtils {

    private ResultSetUtils() {
    }

    public static String getValueOrEmpty(ResultSet resultSet, String colName) {
        String val = "";
        try {
            val = resultSet.getObject(colName).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }

    public static CMSType getType(ResultSet resultSet) {
        CMSType val = CMSType.UNKNOWN;
        try {
            String typeStr = resultSet.getObject(TYPE).toString();
            if (typeStr.equals("R") || typeStr.equals("D")) { // todo
                val = CMSType.DIRECTORY;
            }
            if (typeStr.equals("B")) { // todo
                val = CMSType.FILE;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }

    public static String getZsid(ResultSet resultSet) {
        String sid = getValueOrEmpty(resultSet, SID);
        if (sid == null || sid.isEmpty()) {
            sid = getValueOrEmpty(resultSet, ZSID);
        }
        return sid;
    }

    public static String getZoid(ResultSet resultSet) {
        String sid = getValueOrEmpty(resultSet, ZOID);
        if (sid == null || sid.isEmpty()) {
            sid = getValueOrEmpty(resultSet, ID);
        }
        return sid;
    }

    public static String getFid(ResultSet resultSet) {
        return getValueOrEmpty(resultSet, "f_id");
    }
}
