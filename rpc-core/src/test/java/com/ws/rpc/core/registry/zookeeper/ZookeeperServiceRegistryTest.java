package com.ws.rpc.core.registry.zookeeper;

import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.registry.ServiceRegistry;
import org.junit.Test;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-06 23:40
 */
public class ZookeeperServiceRegistryTest {

    @Test
    public void testRegister() throws Exception {
        ServiceRegistry serviceRegistry = new ZookeeperServiceRegistry("192.168.150.160:2181");
        ServiceInfo serviceInfo = ServiceInfo.builder()
                .host("192.168.150.101")
                .port(9991)
                .serviceKey("com.ws.rpc.example.server.HelloService:1.0")
                .version("1.0")
                .weight(1)
                .metadata("测试")
                .build();
        serviceRegistry.register(serviceInfo);
//        serviceRegistry.unregister(serviceInfo);
//        serviceRegistry.destroy();
    }

}