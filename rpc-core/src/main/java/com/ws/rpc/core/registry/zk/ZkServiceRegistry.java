package com.ws.rpc.core.registry.zk;

import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

/**
 * @Reference https://github.com/viego1999/wxy-rpc/tree/master
 * @version 1.0
 * @date 2024-12-27 1:49
 */

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private static final int SESSION_TIMEOUT = 60 * 1000;
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int BASE_SLEEP_TIME = 3 * 1000;
    private static final int MAX_RETRY = 10;
    private static final String BASE_PATH = "/xianger-rpc";

    private CuratorFramework client;
    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    public ZkServiceRegistry(String registryAddress) {
        try {
            // 创建zk客户端示例
            client = CuratorFrameworkFactory.newClient(
                    registryAddress,
                    SESSION_TIMEOUT,
                    CONNECT_TIMEOUT,
                    new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRY)
            );
            // 开启客户端通信
            client.start();

            // 构建 ServiceDiscovery 服务注册中心
            serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(new JsonInstanceSerializer<>(ServiceInfo.class))
                    .basePath(BASE_PATH)
                    .build();

            serviceDiscovery.start();
            log.info("Zookeeper registry started successfully at address: {}", registryAddress);
        } catch (Exception e) {
            log.error("An error occurred while starting the zookeeper registry: ", e);
            throw new RpcException("Failed to start Zookeeper Service Registry", e);
        }
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = buildServiceInstance(serviceInfo);
        try {
            serviceDiscovery.registerService(serviceInstance);
            log.info("Successfully registered service: [{}] at address: {}:{}",
                    serviceInstance.getName(), serviceInstance.getAddress(), serviceInstance.getPort());
        } catch (Exception e) {
            String errorMsg = String.format("Error occurred when registering service [%s] at address [%s:%d]",
                    serviceInfo.getServiceKey(), serviceInfo.getHost(), serviceInfo.getPort());
            log.error(errorMsg, e);
            throw new RpcException(errorMsg, e);
        }
    }

    @Override
    public void unregister(ServiceInfo serviceInfo) throws Exception {
        ServiceInstance<ServiceInfo> serviceInstance = buildServiceInstance(serviceInfo);
        try {
            serviceDiscovery.unregisterService(serviceInstance);
            log.warn("Successfully unregistered service: [{}] from address: {}:{}",
                    serviceInstance.getName(), serviceInstance.getAddress(), serviceInstance.getPort());
        } catch (Exception e) {
            String errorMsg = String.format("Error occurred when unregistering service [%s] from address [%s:%d]",
                    serviceInfo.getServiceKey(), serviceInfo.getHost(), serviceInfo.getPort());
            log.error(errorMsg, e);
            throw new RpcException(errorMsg, e);
        }
    }

    @Override
    public void destroy() throws Exception {
        try {
            serviceDiscovery.close();
            client.close();
            log.info("Zookeeper registry destroyed successfully.");
        } catch (Exception e) {
            log.error("An error occurred while closing the Zookeeper registry.", e);
            throw new RpcException("Failed to destroy Zookeeper Service Registry", e);
        }
    }


    /**
     * 构建一个 ServiceInstance 实例
     *
     * @param serviceInfo 服务信息
     * @return ServiceInstance
     */
    private ServiceInstance<ServiceInfo> buildServiceInstance(ServiceInfo serviceInfo) throws Exception {
        return ServiceInstance.<ServiceInfo>builder()
                .name(serviceInfo.getServiceKey())
                .address(serviceInfo.getHost())
                .port(serviceInfo.getPort())
                .payload(serviceInfo)
                .build();
    }
}
