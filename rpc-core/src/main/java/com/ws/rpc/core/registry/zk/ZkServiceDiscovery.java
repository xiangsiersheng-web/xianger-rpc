package com.ws.rpc.core.registry.zk;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.loadbalance.LoadBalance;
import com.ws.rpc.core.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Reference https://github.com/viego1999/wxy-rpc/tree/master
 * @version 1.0
 * @date 2024-12-27 2:00
 */

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 60 * 1000;
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int BASE_SLEEP_TIME = 3 * 1000;
    private static final int MAX_RETRY = 10;
    private static final String BASE_PATH = "/xianger-rpc";

    private final LoadBalance loadBalance;
    private final CuratorFramework client;
    private final org.apache.curator.x.discovery.ServiceDiscovery<ServiceInfo> serviceDiscovery;

    /**
     * 服务缓存：缓存每个服务名的 `ServiceCache` 对象，并监听服务变化。
     */
    private final Map<String, ServiceCache<ServiceInfo>> serviceCacheMap = new ConcurrentHashMap<>();

    /**
     * 本地缓存服务列表，服务变化时由 `ServiceCacheListener` 更新。
     */
    private final Map<String, List<ServiceInfo>> serviceMap = new ConcurrentHashMap<>();

    /**
     * 构造方法，传入 Zookeeper 地址和负载均衡策略
     *
     * @param registryAddress zookeeper 的连接地址
     * @param loadBalance     负载均衡策略
     */
    public ZkServiceDiscovery(String registryAddress, LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        this.client = createZookeeperClient(registryAddress);
        this.serviceDiscovery = createServiceDiscovery();
    }

    /**
     * 创建并启动 Zookeeper 客户端。
     */
    private CuratorFramework createZookeeperClient(String registryAddress) {
        try {
            CuratorFramework client = CuratorFrameworkFactory.newClient(
                    registryAddress, SESSION_TIMEOUT, CONNECT_TIMEOUT,
                    new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRY));
            client.start();
            log.info("Zookeeper client started successfully.");
            return client;
        } catch (Exception e) {
            log.error("Failed to create Zookeeper client", e);
            throw new RpcException("Failed to create Zookeeper client", e);
        }
    }


    /**
     * 创建服务发现实例。
     */
    private org.apache.curator.x.discovery.ServiceDiscovery<ServiceInfo> createServiceDiscovery() {
        try {
            org.apache.curator.x.discovery.ServiceDiscovery<ServiceInfo> discovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(new JsonInstanceSerializer<>(ServiceInfo.class))
                    .basePath(BASE_PATH)
                    .build();
            discovery.start();
            log.info("Service discovery started successfully.");
            return discovery;
        } catch (Exception e) {
            log.error("Failed to start ServiceDiscovery", e);
            throw new RpcException("Failed to start ServiceDiscovery", e);
        }
    }

    /**
     * 根据请求的服务名称进行服务发现并负载均衡
     */
    @Override
    public ServiceInfo discover(RpcRequest rpcRequest) {
        try {
            return loadBalance.select(getServices(rpcRequest.getServiceName()), rpcRequest);
        } catch (Exception e) {
            log.error("Error while discovering service: {}", rpcRequest.getServiceName(), e);
            throw new RpcException("Service discovery failed for " + rpcRequest.getServiceName(), e);
        }
    }

    @Override
    public List<ServiceInfo> getServices(String serviceName) throws Exception {
        if (!serviceCacheMap.containsKey(serviceName)) {
            initServiceCache(serviceName);
        }
        return serviceMap.get(serviceName);
    }

    private void initServiceCache(String serviceName) {
        ServiceCache<ServiceInfo> serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name(serviceName)
                .build();
        serviceCache.addListener(new ServiceCacheListener() {

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState newState) {
                log.info("Zookeeper client connection state changed: {}", newState);
            }

            @Override
            public void cacheChanged() {
                List<ServiceInfo> services = serviceCache.getInstances().stream()
                        .map(ServiceInstance::getPayload).collect(Collectors.toList());
                serviceMap.put(serviceName, services);
                log.info("Updated service cache for service: {}. Current instances: {}", serviceName, services.size());
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        // 关闭服务缓存
        for (ServiceCache<ServiceInfo> serviceCache : serviceCacheMap.values()) {
            if (serviceCache != null) {
                serviceCache.close();
            }
        }
        serviceCacheMap.clear();
        log.info("All service caches have been closed.");

        // 关闭服务发现实例
        if (serviceDiscovery != null) {
            serviceDiscovery.close();
            log.info("Service discovery has been closed.");
        }

        // 关闭 Curator 客户端
        if (client != null) {
            client.close();
            log.info("Zookeeper client has been closed.");
        }
    }
}
