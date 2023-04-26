package com.eustrosoft.core.context;

import org.eustrosoft.qdbp.QDBPool;

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
        }
        return pool;
    }
}
