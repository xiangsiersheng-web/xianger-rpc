package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-07 22:52
 */
public class RandomLoadBalanceTest {
    public static void main(String[] args) {
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        for (int port = 9990; port <= 9992; port++) {
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

        // 1000次，记录每个服务的调用次数
        Map<ServiceInfo, Integer> cnts = new HashMap<>();
        for (int i = 0; i < 3000; i++) {
            ServiceInfo serviceInfo = new RandomLoadBalance().select(serviceInfos, request);
            System.out.println(serviceInfo.getPort());
            cnts.put(serviceInfo, cnts.getOrDefault(serviceInfo, 0) + 1);
        }
        for (Map.Entry<ServiceInfo, Integer> entry : cnts.entrySet()) {
            System.out.println(entry.getKey().getPort() + ": " + entry.getValue());
        }
    }

}