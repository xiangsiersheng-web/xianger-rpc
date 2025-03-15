package com.ws.rpc.example.client.controller;

import com.ws.rpc.client.annotation.RpcReference;
import com.ws.rpc.example.api.HelloService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 22:51
 */
@Component
@RestController
@RequestMapping("/hello")
public class HelloController {
    @RpcReference(version = "1.0", timeout = 1000, retry = 3)
    private HelloService helloService;

    @GetMapping("/test/ws")
    public void test() {
        System.out.println(helloService.sayHello("ws"));
    }

    @GetMapping("/test/{count}")
    public Map<String, Long> performTest(@PathVariable("count") Long count) {
        Map<String, Long> result = new HashMap<>();
        result.put("调用次数", count);
        long start = System.currentTimeMillis();
        for (long i = 0; i < count; i++) {
            helloService.sayHello(Long.toString(i));
        }
        result.put("耗时(ms)", System.currentTimeMillis() - start);
        return result;
    }

    @GetMapping("/{name}")
    public String hello(@PathVariable("name") String name) {
        return helloService.sayHello(name);
    }
}
