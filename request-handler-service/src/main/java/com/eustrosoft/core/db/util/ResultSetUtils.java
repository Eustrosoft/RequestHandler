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

    public static String getZsid(ResultSet resultSet) {
        String zsid = getStrValueOrEmpty(resultSet, SID);
        if (zsid == null || zsid.isEmpty()) {
            zsid = getStrValueOrEmpty(resultSet, ZSID);
        }
        return zsid;
    }

    public static String getZoid(ResultSet resultSet) {
        String zoid = getStrValueOrEmpty(resultSet, ZOID);
        if (zoid == null || zoid.isEmpty()) {
            zoid = getStrValueOrEmpty(resultSet, ID);
        }
        return zoid;
    }

    public static String getZrid(ResultSet resultSet) {
        String zrid = getStrValueOrEmpty(resultSet, ZRID);
        if (zrid == null || zrid.isEmpty()) {
            zrid = getStrValueOrEmpty(resultSet, ID);
        }
        return zrid;
    }

    public static String getFid(ResultSet resultSet) {
        return getStrValueOrEmpty(resultSet, "f_id");
    }
}
