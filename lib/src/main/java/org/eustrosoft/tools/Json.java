/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

    public static String fromObject(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public String toString() {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            object.add(entry.getKey(), gson.toJsonTree(entry.getValue()));
        }
        return object.toString();
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
