package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.LoadBalance;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-07 22:56
 */
public class ConsistentHashLoadBalanceTest {

    @Test
    public void testConsistentHashLoadBalanceWithSameRequest() {
        // 1. 创建服务列表
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

        // 2. 创建负载均衡器
        LoadBalance loadBalance = new ConsistentHashLoadBalance();

        // 3. 创建相同的请求
        RpcRequest request1 = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();

        RpcRequest request2 = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();

        // 4. 使用相同的请求进行负载均衡选择
        ServiceInfo selectedService1 = loadBalance.select(serviceInfos, request1);
        ServiceInfo selectedService2 = loadBalance.select(serviceInfos, request2);

        // 5. 验证：对于相同的请求，负载均衡器应返回相同的服务
        Assert.assertEquals(selectedService1, selectedService2);
        System.out.println(selectedService1);
        System.out.println(selectedService2);
    }


    @Test
    public void testConsistentHashLoadBalanceWithDifferentRequest() {
        // 1. 创建服务列表
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

        // 2. 创建负载均衡器
        LoadBalance loadBalance = new ConsistentHashLoadBalance();

        // 3. 创建不同的请求
        RpcRequest request1 = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws1"})
                .parameterTypes(new Class[]{String.class})
                .build();

        RpcRequest request2 = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"test1"})
                .parameterTypes(new Class[]{String.class})
                .build();

        // 4. 使用不同的请求进行负载均衡选择
        ServiceInfo selectedService1 = loadBalance.select(serviceInfos, request1);
        ServiceInfo selectedService2 = loadBalance.select(serviceInfos, request2);

        System.out.println(selectedService1);
        System.out.println(selectedService2);

        // 5. 验证：对于不同的请求，期望负载均衡器返回不同的服务
        Assert.assertNotEquals(selectedService1, selectedService2);
    }

    @Test
    public void testConsistentHashLoadBalanceWithEmptyServiceList() {
        // 1. 创建空的服务列表
        List<ServiceInfo> serviceInfos = new ArrayList<>();

        // 2. 创建负载均衡器
        LoadBalance loadBalance = new ConsistentHashLoadBalance();

        // 3. 创建请求
        RpcRequest request = RpcRequest.builder()
                .serviceKey("com.ws.rpc.core.service.HelloService:1.0")
                .methodName("hello")
                .parameters(new Object[]{"ws"})
                .parameterTypes(new Class[]{String.class})
                .build();

        // 4. 使用请求进行负载均衡选择
        ServiceInfo selectedService = loadBalance.select(serviceInfos, request);

        // 5. 验证：服务列表为空时，负载均衡器应返回null
        Assert.assertNull(selectedService);
    }
}