/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core;

import java.util.HashMap;
import java.util.Map;

public final class ApplicationContext {

    private static ApplicationContext applicationContext;
    private transient Map<String, Class<?>> handlers;

    private ApplicationContext() {
        handlers = new HashMap<>();
    }

    public synchronized static ApplicationContext getInstance() {
        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        }
        return applicationContext;
    }

    public synchronized Map<String, Class<?>> getHandlers() {
        return applicationContext.handlers;
    }
}
