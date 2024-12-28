package com.ws.rpc.example.server.impl;

import com.ws.rpc.example.api.HelloService;
import com.ws.rpc.server.annotation.RpcService;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 22:22
 */
@RpcService(version = "1.0", interfaceClass = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
