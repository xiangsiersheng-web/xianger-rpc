package com.ws.rpc.core.compression.uncompress;

import com.ws.rpc.core.compression.Compression;
import com.ws.rpc.core.compression.CompressionFactory;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 18:19
 */
public class UnCompressionTest {
    @Test
    public void test() {
        Serialization jsonSerialization = SerializationFactory.getSerialization(SerializationType.JSON);
        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();
        byte[] bytes = jsonSerialization.serialize(request);
        System.out.println("before compress: " + bytes.length);

        Compression compression = CompressionFactory.getCompression(CompressionType.UN_COMPRESSION);
        byte[] compress = compression.compress(bytes);
        System.out.println("after compress: " + compress.length);
    }

}