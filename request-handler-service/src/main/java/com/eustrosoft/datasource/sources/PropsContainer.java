package com.eustrosoft.datasource.sources;

import java.util.Map;

public interface PropsContainer {
    default void reloadProps(boolean force) throws Exception {
        if (getLoadedProps() == null || getLoadedProps().isEmpty()) {
            loadProps();
            return;
        }
        if (force) {
            loadProps();
        } else {
            throw new Exception("There are some loaded properties already." +
                    "\nIf you want to reload them, use 'force' = true key for reload method.");
        }
    }

    ;

    void loadProps() throws Exception;

    Map<String, String> getLoadedProps();
}
