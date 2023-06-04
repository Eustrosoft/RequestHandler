package com.eustrosoft.core.context;

import com.eustrosoft.core.tools.WebParams;
import org.eustrosoft.qdbp.QDBPool;

import javax.servlet.http.HttpServletRequest;

public final class DBPoolContext {
    private static QDBPool pool;

    private DBPoolContext() {

    }

    public static QDBPool getInstance(String name, String url, String driver) {
        QDBPool cachedPool = QDBPool.get(name);
        if (cachedPool == null) {
            cachedPool = new QDBPool(name, url, driver);
            QDBPool.add(cachedPool);
        }
        return cachedPool;
    }

    public static String getUrl(HttpServletRequest request) {
        return WebParams.getString(request, WebParams.DB_POOL_URL);
    }

    public static String getDriverClass(HttpServletRequest request) {
        return WebParams.getString(request, WebParams.DB_JDBC_CLASS);
    }

    public static String getDbPoolName(HttpServletRequest request) {
        return WebParams.getString(request, WebParams.DB_POOL_NAME);
    }
}
