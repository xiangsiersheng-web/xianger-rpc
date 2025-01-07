package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.LoadBalance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-07 22:44
 */
public class RoundRobinLoadBalanceTest {
    public static void main(String[] args) {
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        for (int port = 9990; port <= 9999; port++) {
            ServiceInfo serviceInfo = ServiceInfo.builder()
                    .host("192.168.150.101")
                    .port(port)
                    .serviceKey("com.ws.rpc.example.server.HelloService:1.0")
                    .version("1.0")
                    .weight(1)
                    .metadata("测试")
                    .build();
            serviceInfos.add(serviceInfo);
        }
        System.out.println(serviceInfos.size());

        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();

        LoadBalance roundRobinLoadBalance = new RoundRobinLoadBalance();
        for (int i = 0; i < 20; i++) {
            System.out.println(roundRobinLoadBalance.select(serviceInfos, request));
        }
    }

}