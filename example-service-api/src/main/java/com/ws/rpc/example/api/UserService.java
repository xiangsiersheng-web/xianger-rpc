package com.ws.rpc.example.api;

import com.ws.rpc.example.pojo.User;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-15 15:36
 */
public interface UserService {

    User getMe();

    User getUser(Integer id);

    List<User> getUsers();
}
