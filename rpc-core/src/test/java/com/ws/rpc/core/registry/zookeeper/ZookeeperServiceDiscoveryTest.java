package com.ws.rpc.core.registry.zookeeper;

import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.ws.rpc.core.registry.ServiceDiscovery;
import com.ws.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-07 1:02
 */
@Slf4j
public class ZookeeperServiceDiscoveryTest {

    @Test
    public void batchRegistry() throws Exception {
        ServiceRegistry serviceRegistry = new ZookeeperServiceRegistry("192.168.150.160:2181");
        for (int port = 9991; port < 9999; port++) {
            ServiceInfo serviceInfo = ServiceInfo.builder()
                    .address("192.168.150.101")
                    .port(port)
                    .serviceKey("com.ws.rpc.example.server.HelloService:1.0")
                    .version("1.0")
                    .weight(1)
                    .metadata("测试")
                    .build();
            serviceRegistry.register(serviceInfo);
        }
    }

    @Test
    public void testDiscovery() throws Exception {
        RandomLoadBalance loadBalance = new RandomLoadBalance();
        ServiceDiscovery serviceDiscovery = new ZookeeperServiceDiscovery("192.168.150.160:2181", loadBalance);
        ServiceRegistry serviceRegistry = new ZookeeperServiceRegistry("192.168.150.160:2181");
        List<ServiceInfo> serviceInfos = serviceDiscovery.getServices("com.ws.rpc.example.server.HelloService:1.0");
        System.out.println("删除前共"+ serviceInfos.size() +"个实例");
        for (ServiceInfo serviceInfo : serviceInfos) {
            System.out.println(serviceInfo);
        }

        // 删除节点后？
        System.out.println("=================删除节点后=================");
        ServiceInfo serviceInfo = ServiceInfo.builder()
                .address("192.168.150.101")
                .port(9991)
                .serviceKey("com.ws.rpc.example.server.HelloService:1.0")
                .version("1.0")
                .weight(1)
                .metadata("测试")
                .build();
        serviceRegistry.unregister(serviceInfo);
        Thread.sleep(500);
        serviceInfos = serviceDiscovery.getServices("com.ws.rpc.example.server.HelloService:1.0");
        System.out.println("删除后共"+ serviceInfos.size() +"个实例");
        for (ServiceInfo info : serviceInfos) {
            System.out.println(info);
        }
        System.in.read();
    }

}