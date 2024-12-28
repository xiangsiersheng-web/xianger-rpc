package com.ws.rpc.example.client.controller;

import com.ws.rpc.client.annotation.RpcReference;
import com.ws.rpc.example.api.HelloService;
import org.springframework.stereotype.Component;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 22:51
 */
@Component
public class HelloController {
    @RpcReference(version = "1.0")
    private HelloService helloService;

    public void test() {
        System.out.println(helloService.hello("ws"));
    }
}
