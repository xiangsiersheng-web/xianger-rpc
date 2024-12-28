package com.ws.rpc.example.client;

import com.ws.rpc.example.client.controller.HelloController;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 22:46
 */
@ComponentScan({"com.ws.rpc.client.config", "com.ws.rpc.example.client"})
public class ClientApplication {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ClientApplication.class, args);

        HelloController helloController = applicationContext.getBean(HelloController.class);
        helloController.test();
        System.in.read();
    }
}
