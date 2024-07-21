/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.util;

import com.eustrosoft.cms.CMSType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static com.eustrosoft.core.constants.DBConstants.ID;
import static com.eustrosoft.core.constants.DBConstants.SID;
import static com.eustrosoft.core.constants.DBConstants.TYPE;
import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;
import static com.eustrosoft.core.constants.DBConstants.ZSID;

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

    public static void setCharOrNull(PreparedStatement preparedStatement, int pos, Character chr)
            throws SQLException {
        if (chr == null || chr.toString().isEmpty()) {
            preparedStatement.setNull(pos, Types.CHAR);
        } else {
            preparedStatement.setString(pos, chr.toString());
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

    public static void setNull(PreparedStatement preparedStatement, int pos, int type)
            throws SQLException {
        preparedStatement.setNull(pos, type);
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
            // ex.printStackTrace();
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

    public static String getFidOrZoid(ResultSet resultSet) {
        String fileId = getStrValueOrEmpty(resultSet, "f_id");
        if (fileId.isEmpty()) {
            return String.valueOf(getLongValueOrEmpty(resultSet, ZOID));
        }
        return fileId;
    }
}
