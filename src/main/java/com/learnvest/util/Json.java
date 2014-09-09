package com.learnvest.util;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class Json {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser parser = new JsonParser();


    public static JsonElement loadFromFile(String path) {
        return fromJson(getJson(path));
    }

    public static <T> T loadFromFile(String path, TypeToken<T> typeToken) {
        return fromJson(getJson(path), typeToken);
    }
    
    public static <T> T loadFromFile(String path, Class<T> _class) {
        return fromJson(getJson(path), _class);
    }

    public static JsonElement fromJson(String json) {
        return parser.parse(json);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public static <T> T fromJson(String json, Class<T> _class) {
        return gson.fromJson(json, _class);
    }

    public static <T> String toJson(T t) {
        return gson.toJson(t);
    }


    private static String getJson(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load file: " + path, e);
        }
    }
}

