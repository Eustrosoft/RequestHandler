package com.eustrosoft.core.context;

import com.eustrosoft.core.tools.WebParams;
import org.eustrosoft.qdbp.QDBPool;

import javax.servlet.http.HttpServletRequest;

public final class DBPoolContext {
    private static QDBPool pool;

    private DBPoolContext() {

    }

    public static QDBPool getInstance(String name, String url, String driver) {
        if (pool == null) {
            pool = QDBPool.get(name);
            if (pool == null) {
                pool = new QDBPool(name, url, driver);
            }
        } else {
            QDBPool cachedPool = QDBPool.get(name);
            if (cachedPool == null) {
                cachedPool = new QDBPool(name, url, driver);
                pool.add(cachedPool);
            }
        }
        return pool;
    }

    public static String getUrl(HttpServletRequest request) {
        return WebParams.getString(request, WebParams.DB_URL);
    }
}
