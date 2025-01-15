package com.ws.rpc.example.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.gson.reflect.TypeToken;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import com.ws.rpc.core.serialization.json.GsonSerialization;
import com.ws.rpc.example.pojo.User;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
/**
 * 
 * @author ws
 * @version 1.0
 * @date 2025-01-15 16:03
 */
public class UserServiceImplTest {
    @Test
    public void serializeUser() {
        UserServiceImpl userService = new UserServiceImpl();
        User me = userService.getMe();


        Serialization serialization = SerializationFactory.getSerialization(SerializationType.JSON);
//        Serialization serialization = new GsonSerialization();
        byte[] bytes = serialization.serialize(me);
        User user = serialization.deserialize(bytes, User.class);

        System.out.println(user);

        RpcResponse response = RpcResponse.builder()
                .result(me)
                .build();
        byte[] bytes1 = serialization.serialize(response);
        RpcResponse rpcResponse = serialization.deserialize(bytes1, RpcResponse.class);
        JSONObject result = (JSONObject)rpcResponse.getResult();
        User user1 = result.toJavaObject(User.class);
        rpcResponse.setResult(user1);
        System.out.println(rpcResponse);
    }
}