package com.ws.rpc.example.server.impl;

import com.ws.rpc.example.api.HelloService;
import com.ws.rpc.example.api.UserService;
import com.ws.rpc.example.pojo.User;
import com.ws.rpc.server.annotation.RpcService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-15 15:38
 */
@RpcService(version = "1.0", interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    static Map<Integer, User> userMap = new HashMap<>();
    static {
        userMap.put(1, new User(1, "xianger", "123456", 24));
        userMap.put(2, new User(2, "xiaoming", "123456", 24));
        userMap.put(3, new User(3, "xiaohong", "123456", 24));
    }

    @Override
    public User getMe() {
        return userMap.get(1);
    }

    @Override
    public User getUser(Integer id) {
//        int i = 1 / 0;
        return userMap.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values().stream().toList());
    }
}
