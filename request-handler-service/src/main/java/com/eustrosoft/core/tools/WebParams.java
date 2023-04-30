package com.eustrosoft.core.tools;

import javax.servlet.http.HttpServletRequest;

public final class WebParams {
    public final static String DB_URL = "dbUrl";
    public final static String DB_POOL_URL = "dbPoolUrl";
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
