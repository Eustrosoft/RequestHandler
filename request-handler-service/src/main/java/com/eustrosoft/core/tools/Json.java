/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Json {
    private Map<String, Object> jsonMap = new HashMap<>();
    private JsonBuilder jsonBuilder;

    public JsonBuilder builder() {
        this.jsonBuilder = new JsonBuilder();
        return this.jsonBuilder;
    }

    public void addKeyValue(String key, String val) {
        this.jsonMap.put(key, val);
    }

    public void addKeyObject(String key, Objects val) {
        this.jsonMap.put(key, val);
    }

    public static String tryGetQJsonParam(QJson qJson, String key) {
        String value = null;
        try {
            value = qJson.getItemString(key);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return value;
    }

    public static class JsonBuilder {
        private final Json json;

        public JsonBuilder() {
            json = new Json();
        }

        public JsonBuilder addKeyValue(String key, String val) {
            this.json.addKeyValue(key, val);
            return this;
        }

        public void addKeyObject(String key, Objects val) {
            this.json.addKeyObject(key, val);
        }

        public Json build() {
            return this.json;
        }
    }
}
