/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.tools;

import javax.servlet.http.HttpServletRequest;

public final class WebParams {
    public final static String DB_URL = "dbUrl";
    public final static String DB_POOL_URL = "QDBPOOL_URL";
    public final static String DB_POOL_NAME = "QDBPOOL_NAME";
    public final static String DB_JDBC_CLASS = "QDBPOOL_JDBC_CLASS";
    public final static String DEBUG = "debug";

    public static Boolean getBoolean(HttpServletRequest request, String param) {
        String value = getString(request, param);
        if (value == null) {
            return null;
        }
        try {
            return Boolean.getBoolean(value);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getString(HttpServletRequest request, String param) {
        return request.getServletContext().getInitParameter(param);
    }
}
