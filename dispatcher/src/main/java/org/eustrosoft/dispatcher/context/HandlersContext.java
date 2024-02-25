package org.eustrosoft.dispatcher.context;

import org.eustrosoft.core.annotations.Handler;

import java.util.HashMap;
import java.util.Map;

public final class HandlersContext {
    private static HandlersContext context;
    public Map<String, Class<?>> handlersMap;
    private HandlersConfig handlersConfig;

    private HandlersContext() {

    }

    public static HandlersContext getInstance() {
        if (context == null) {
            context = new HandlersContext();
        }
        return context;
    }

    public static HandlersContext getInstance(HandlersConfig handlerConfiguration) {
        if (context == null) {
            context = new HandlersContext();
        }
        context.setHandlersConfig(handlerConfiguration);
        return context;
    }

    public Map<String, Class<?>> getHandlersMap() {
        if (handlersMap == null) {
            initLazy();
        }
        return this.handlersMap;
    }

    public void initLazy() {
        handlersMap = new HashMap<>();
        Map<String, String> allowedHandlers = this.handlersConfig.getAllowedHandlers();
        for (Map.Entry<String, String> entry : allowedHandlers.entrySet()) {
            try {
                Class<?> clazz = Class.forName(entry.getValue());
                Handler[] declaredAnnotationsByType = clazz.getDeclaredAnnotationsByType(Handler.class);
                String value = declaredAnnotationsByType[0].value();
                if (allowedHandlers.containsKey(value)) {
                    handlersMap.put(value, clazz);
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // err while search classes
            }
        }

    }

    private void setHandlersConfig(HandlersConfig config) {
        this.handlersConfig = config;
    }
}
