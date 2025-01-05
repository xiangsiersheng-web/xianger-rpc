package com.ws.rpc.core.serialization.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;

import java.io.IOException;
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
        try {
            String json = GSON.toJson(obj);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("Json serialize failed.", e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) {
        try {
            String json = new String(data, StandardCharsets.UTF_8);
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new SerializationException("Json deserialize failed.", e);
        }
    }
}
