package com.ws.rpc.core.serialization.hessian;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-05 17:02
 */
public class HessianSerializationTest {
    @Test
    public void test() {
        Serialization serialization = SerializationFactory.getSerialization(SerializationType.HESSIAN);
        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();
        byte[] bytes = serialization.serialize(request);
        System.out.println(new String(bytes));
        System.out.println(bytes.length);
//        bytes = new byte[0];
        RpcRequest target = serialization.deserialize(bytes, RpcRequest.class);
        System.out.println(target);
        System.out.println(request);
        assertEquals(target, request);
    }
}