package com.ws.rpc.core.serialization.jdk;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.protocol.RpcMessage;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 23:26
 */
public class JdkSerializationTest {
    @Test
    public void serialize() {
        JdkSerialization jdkSerialization = new JdkSerialization();
        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();
        byte[] bytes = jdkSerialization.serialize(request);
        RpcRequest target = jdkSerialization.deserialize(bytes, RpcRequest.class);
        System.out.println(target);
        System.out.println(request);
        assertEquals(target, request);
    }

}