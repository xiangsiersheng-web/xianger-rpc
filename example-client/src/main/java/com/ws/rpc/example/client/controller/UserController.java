package com.ws.rpc.example.client.controller;

import com.ws.rpc.client.annotation.RpcReference;
import com.ws.rpc.example.api.UserService;
import com.ws.rpc.example.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-15 15:44
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @RpcReference(version = "1.0")
    private UserService userService;

    @GetMapping("/me")
    public User getMe() {
        return userService.getMe();
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return userService.getUser(id);
    }

    @GetMapping("/getAllUser")
    public List<User> getAllUser() {
        return userService.getUsers();
    }
}
