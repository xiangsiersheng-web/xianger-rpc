package com.ws.rpc.core.serialization.json;

import com.google.gson.*;
import com.ws.rpc.core.serialization.Serialization;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 15:42
 */
public class JsonSerialization implements Serialization {

    private final static Gson GSON = new GsonBuilder()
                        .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                        .create();

    private static class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                return Class.forName(jsonElement.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Class not found during deserialization", e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(aClass.getName());
        }
    }

    @Override
    public <T> byte[] doSerialize(T obj) {
        String json = GSON.toJson(obj);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) {
        String json = new String(data, StandardCharsets.UTF_8);
        return GSON.fromJson(json, clazz);
    }
}
