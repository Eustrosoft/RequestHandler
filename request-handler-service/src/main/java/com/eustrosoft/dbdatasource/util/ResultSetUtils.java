/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.dbdatasource.util;

import com.eustrosoft.datasource.sources.ranges.CMSType;

import java.sql.ResultSet;

import static com.eustrosoft.dbdatasource.constants.DBConstants.ID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.SID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.TYPE;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;
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

    public static CMSType getType(ResultSet resultSet, CMSType defaultValue) {
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

    public static CMSType getType(ResultSet resultSet) {
        return getType(resultSet, CMSType.UNKNOWN);
    }

    public static String getZsid(ResultSet resultSet) {
        String zsid = getValueOrEmpty(resultSet, SID);
        if (zsid == null || zsid.isEmpty()) {
            zsid = getValueOrEmpty(resultSet, ZSID);
        }
        return zsid;
    }

    public static String getZoid(ResultSet resultSet) {
        String zoid = getValueOrEmpty(resultSet, ZOID);
        if (zoid == null || zoid.isEmpty()) {
            zoid = getValueOrEmpty(resultSet, ID);
        }
        return zoid;
    }

    public static String getZrid(ResultSet resultSet) {
        String zrid = getValueOrEmpty(resultSet, ZRID);
        if (zrid == null || zrid.isEmpty()) {
            zrid = getValueOrEmpty(resultSet, ID);
        }
        return zrid;
    }

    public static String getFid(ResultSet resultSet) {
        return getValueOrEmpty(resultSet, "f_id");
    }
}
