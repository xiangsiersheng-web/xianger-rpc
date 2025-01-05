package com.ws.rpc.core.compression.gzip;

import com.ws.rpc.core.compression.Compression;
import com.ws.rpc.core.compression.CompressionFactory;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 18:27
 */
public class GzipCompressionTest {
    @Test
    public void test() {
        // 使用JSON序列化
        Serialization jsonSerialization = SerializationFactory.getSerialization(SerializationType.JSON);
        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();

        // 序列化RpcRequest对象
        byte[] bytes = jsonSerialization.serialize(request);
        System.out.println("before compress: " + bytes.length);

        // 使用GZIP压缩
        Compression compression = CompressionFactory.getCompression(CompressionType.GZIP);
        byte[] compress = compression.compress(bytes);
        System.out.println("after compress: " + compress.length);

        // 解压缩
        byte[] decompress = compression.decompress(compress);
        System.out.println("after decompress: " + decompress.length);

        // 验证解压缩后的数据是否与原始数据相同
        assertTrue(Arrays.equals(decompress, bytes));

        // 反序列化解压后的数据
        RpcRequest target = jsonSerialization.deserialize(decompress, RpcRequest.class);

        // 验证原始对象与解序列化后的对象是否相等
        assertEquals(request, target);
    }

}