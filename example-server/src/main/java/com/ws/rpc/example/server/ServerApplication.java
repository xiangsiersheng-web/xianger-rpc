package com.ws.rpc.example.server;

import com.ws.rpc.server.annotation.RpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 22:18
 */
@RpcScan(basePackages = {"com.ws.rpc.example.server"})
@ComponentScan("com.ws.rpc.server.config")
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
