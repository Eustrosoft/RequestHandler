package com.eustrosoft.core.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Json {
    private Map<String, Object> jsonMap = new HashMap<>();
    private JsonBuilder jsonBuilder;

    public Json() {

    }

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
