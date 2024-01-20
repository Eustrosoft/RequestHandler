/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public interface PropsContainer {
    Properties getProperties();

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

    void loadProps() throws Exception;

    default Map<String, String> getLoadedProps() {
        Map<String, String> props = new HashMap<>();
        Set<Map.Entry<Object, Object>> entries = getProperties().entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            props.put((String) entry.getKey(), (String) entry.getValue());
        }
        return props;
    }
}
