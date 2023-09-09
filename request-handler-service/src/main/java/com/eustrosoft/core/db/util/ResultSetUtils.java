/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.util;

import com.eustrosoft.cms.CMSType;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.*;

public final class ResultSetUtils {

    private ResultSetUtils() {
    }

    public static String getStrValueOrEmpty(ResultSet resultSet, String colName) {
        String val = "";
        try {
            val = resultSet.getObject(colName).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }

    public static Long getLongValueOrEmpty(ResultSet resultSet, String colName) {
        Long val = null;
        try {
            val = Long.parseLong(resultSet.getObject(colName).toString());
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

    public static Long getZsid(ResultSet resultSet) {
        Long zsid = getLongValueOrEmpty(resultSet, SID);
        if (zsid == null) {
            zsid = getLongValueOrEmpty(resultSet, ZSID);
        }
        return zsid;
    }

    public static Long getZoid(ResultSet resultSet) {
        Long zoid = getLongValueOrEmpty(resultSet, ZOID);
        if (zoid == null) {
            zoid = getLongValueOrEmpty(resultSet, ID);
        }
        return zoid;
    }

    public static Long getZrid(ResultSet resultSet) {
        Long zrid = getLongValueOrEmpty(resultSet, ZRID);
        if (zrid == null) {
            zrid = getLongValueOrEmpty(resultSet, ID);
        }
        return zrid;
    }

    public static String getFid(ResultSet resultSet) {
        return getStrValueOrEmpty(resultSet, "f_id");
    }
}
