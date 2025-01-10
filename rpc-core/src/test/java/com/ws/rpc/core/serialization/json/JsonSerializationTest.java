package com.ws.rpc.core.serialization.json;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 16:29
 */
public class JsonSerializationTest {
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
        System.out.println(new String(bytes));
        System.out.println(bytes.length);
//        bytes = new byte[0];
        RpcRequest target = jsonSerialization.deserialize(bytes, RpcRequest.class);
        System.out.println(target);
        System.out.println(request);
        assertEquals(target, request);

//        RpcResponse response = RpcResponse.builder()
//                .result("hello")
//                .build();
//
//        byte[] bytes = jsonSerialization.serialize(response);
//        System.out.println(new String(bytes));
//        System.out.println(bytes.length);
//        RpcResponse target = jsonSerialization.deserialize(bytes, RpcResponse.class);
//        System.out.println(target);
//
//        assertEquals(target, response);
    }
}