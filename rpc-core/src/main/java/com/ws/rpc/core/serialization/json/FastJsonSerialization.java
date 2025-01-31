package com.ws.rpc.core.serialization.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ws.rpc.core.exception.SerializationException;
import com.ws.rpc.core.serialization.Serialization;

import java.nio.charset.StandardCharsets;

/**
 * 使用 Fastjson 实现 JSON 序列化
 * @author ws
 * @version 1.0
 * @date 2025-01-15 16:29
 */
public class FastJsonSerialization implements Serialization {

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        ParserConfig.getGlobalInstance().addAccept("com.ws.rpc.");
    }

    @Override
    public <T> byte[] doSerialize(T obj) {
        try {
            // 序列化对象为 JSON 字符串
            String json = JSON.toJSONString(obj, SerializerFeature.WriteClassName);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializationException("Json serialize failed.", e);
        }
    }

    @Override
    public <T> T doDeserialize(byte[] data, Class<T> clazz) {
        try {
            // 将 JSON 字符串反序列化为目标对象
            String json = new String(data, StandardCharsets.UTF_8);
            return JSON.parseObject(json, clazz, Feature.SupportAutoType);
        } catch (Exception e) {
            throw new SerializationException("Json deserialize failed.", e);
        }
    }
}
