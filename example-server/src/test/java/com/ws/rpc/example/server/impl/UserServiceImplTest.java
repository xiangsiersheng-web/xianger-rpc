package com.ws.rpc.example.server.impl;

import com.google.gson.reflect.TypeToken;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
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
        byte[] bytes = serialization.serialize(me);
        User user = serialization.deserialize(bytes, User.class);

        System.out.println(user);

        List<User> users = userService.getUsers();
        bytes = serialization.serialize(me);

//        // 使用 TypeToken 提供泛型类型信息
//        Type userListType = new TypeToken<List<User>>() {}.getType();
//        List<User> target = serialization.deserialize(bytes, userListType);
//
//        System.out.println(target);
    }
}