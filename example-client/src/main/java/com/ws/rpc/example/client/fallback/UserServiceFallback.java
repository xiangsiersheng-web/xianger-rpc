package com.ws.rpc.example.client.fallback;

import com.ws.rpc.example.api.UserService;
import com.ws.rpc.example.pojo.User;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 16:55
 */
public class UserServiceFallback implements UserService {
    @Override
    public User getMe() {
        return new User(-1, "fallback", "fallback", 0);
    }

    @Override
    public User getUser(Integer id) {
        return new User(-1, "fallback", "fallback", 0);
    }

    @Override
    public List<User> getUsers() {
        return List.of(new User(-1, "fallback", "fallback", 0));
    }
}
