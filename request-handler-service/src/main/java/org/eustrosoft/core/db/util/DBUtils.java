/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.db.util;

import org.eustrosoft.cms.CMSType;
import org.eustrosoft.core.constants.DBConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public final class DBUtils {

    private DBUtils() {
    }

    public static void setLongOrNull(PreparedStatement preparedStatement, int pos, Long num)
            throws SQLException {
        if (num == null) {
            preparedStatement.setNull(pos, Types.BIGINT);
        } else {
            preparedStatement.setLong(pos, num);
        }
    }

    public static void setShortOrNull(PreparedStatement preparedStatement, int pos, Short num)
            throws SQLException {
        if (num == null) {
            preparedStatement.setNull(pos, Types.SMALLINT);
        } else {
            preparedStatement.setShort(pos, num);
        }
    }

    public static void setIntOrNull(PreparedStatement preparedStatement, int pos, Integer num)
            throws SQLException {
        if (num == null) {
            preparedStatement.setNull(pos, Types.INTEGER);
        } else {
            preparedStatement.setInt(pos, num);
        }
    }

    public static void setStringOrNull(PreparedStatement preparedStatement, int pos, String str)
            throws SQLException {
        if (str == null || str.isEmpty()) {
            preparedStatement.setNull(pos, Types.VARCHAR);
        } else {
            preparedStatement.setString(pos, str);
        }
    }

    public static String getStrValueOrEmpty(ResultSet resultSet, String colName) {
        String val = "";
        try {
            val = resultSet.getObject(colName).toString();
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return val;
    }

    public static Long getLongValueOrEmpty(ResultSet resultSet, String colName) {
        Long val = null;
        try {
            val = Long.parseLong(resultSet.getObject(colName).toString());
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return val;
    }

    public static CMSType getType(ResultSet resultSet, CMSType defaultValue) {
        CMSType val = CMSType.UNKNOWN;
        try {
            String typeStr = resultSet.getObject(DBConstants.TYPE).toString();
            if (typeStr.equals("R") || typeStr.equals("D")) { // todo
                val = CMSType.DIRECTORY;
            }
            if (typeStr.equals("B")) { // todo
                val = CMSType.FILE;
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return val;
    }

    public static CMSType getType(ResultSet resultSet) {
        return getType(resultSet, CMSType.UNKNOWN);
    }

    public static Long getZsid(ResultSet resultSet) {
        Long zsid = getLongValueOrEmpty(resultSet, DBConstants.SID);
        if (zsid == null) {
            zsid = getLongValueOrEmpty(resultSet, DBConstants.ZSID);
        }
        return zsid;
    }

    public static Long getZoid(ResultSet resultSet) {
        Long zoid = getLongValueOrEmpty(resultSet, DBConstants.ZOID);
        if (zoid == null) {
            zoid = getLongValueOrEmpty(resultSet, DBConstants.ID);
        }
        return zoid;
    }

    public static Long getZrid(ResultSet resultSet) {
        Long zrid = getLongValueOrEmpty(resultSet, DBConstants.ZRID);
        if (zrid == null) {
            zrid = getLongValueOrEmpty(resultSet, DBConstants.ID);
        }
        return zrid;
    }

    public static String getFid(ResultSet resultSet) {
        return getStrValueOrEmpty(resultSet, "f_id");
    }
}
