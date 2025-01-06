package com.ws.rpc.core.registry.zookeeper;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.LoadBalance;
import com.ws.rpc.core.registry.ServiceDiscovery;
import com.ws.rpc.core.registry.zookeeper.util.CuratorUtils;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ws.rpc.core.registry.zookeeper.ZookeeperConstants.BASE_PATH;
import static com.ws.rpc.core.registry.zookeeper.ZookeeperConstants.DEFAULT_SERIALIZATION_TYPE;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-06 22:29
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    private final LoadBalance loadBalance;
    private final CuratorFramework client;
    private final Map<String, List<ServiceInfo>> serviceCache;
    private final Map<String, PathChildrenCache> serviceWatchers;
    private final Serialization serialization;

    public ZookeeperServiceDiscovery(String registryAddress, LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        client = CuratorUtils.getZkClient(registryAddress);
        serviceCache = new ConcurrentHashMap<>();
        serviceWatchers = new ConcurrentHashMap<>();
        serialization = SerializationFactory.getSerialization(DEFAULT_SERIALIZATION_TYPE);
    }

    @Override
    public ServiceInfo discover(RpcRequest rpcRequest) {
        String serviceKey = rpcRequest.getServiceKey();
        try {
            List<ServiceInfo> serviceInfos = getServices(serviceKey);
            log.debug("Found {} instances", serviceInfos.size());
            return loadBalance.select(serviceInfos, rpcRequest);
        } catch (Exception e) {
            log.warn("Error while discovering service: {}", serviceKey, e);
            throw new RuntimeException("Error while discovering service.", e);
        }
    }

    @Override
    public List<ServiceInfo> getServices(String serviceKey) throws Exception {
        if (serviceCache.get(serviceKey) != null) {
            return serviceCache.get(serviceKey);
        }
        // 避免多个线程并发初始化缓存以及添加监听器
        synchronized (serviceKey.intern()) {
            if (serviceCache.get(serviceKey) != null) {
                return serviceCache.get(serviceKey);
            }
            // 首次发现需要初始化缓存，并添加一个监听器
            List<ServiceInfo> serviceInfos = fetchServiceInfos(serviceKey);
            serviceCache.put(serviceKey, serviceInfos);
            log.info("Loaded and cached {} instances for serviceKey: {}", serviceInfos.size(), serviceKey);

            // 添加一个监听器
            addServiceCacheListener(serviceKey);

            return serviceInfos;
        }
    }

    private List<ServiceInfo> fetchServiceInfos(String serviceKey) throws Exception {
        String path = BASE_PATH + "/" + serviceKey;
        // 获取所有子节点
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(client, path);
        // 获取所有子节点的数据
        ArrayList<ServiceInfo> serviceInfos = new ArrayList<>();
        for (String childNode : childrenNodes) {
            byte[] data = CuratorUtils.getData(client, path + "/" + childNode);
            ServiceInfo serviceInfo = serialization.deserialize(data, ServiceInfo.class);
            serviceInfos.add(serviceInfo);
        }
        return serviceInfos;
    }

    private void addServiceCacheListener(String serviceKey) {
        // 如果已经有监听器了，则不再添加
        if (serviceWatchers.containsKey(serviceKey)) {
            log.info("Watcher already added for serviceKey: {}", serviceKey);
            return;
        }

        String path = BASE_PATH + "/" + serviceKey;

        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        pathChildrenCache.getListenable().addListener((curatorFramework, event) -> {
            log.info("Service node changed: {}. Refreshing cache for serviceKey: {}", event.getType(), serviceKey);
            List<ServiceInfo> serviceInfos = fetchServiceInfos(serviceKey);
            serviceCache.put(serviceKey, serviceInfos);
        });
        try {
            pathChildrenCache.start();
            serviceWatchers.put(serviceKey, pathChildrenCache);
            log.info("Added watcher for serviceKey: {}", serviceKey);
        } catch (Exception e) {
            log.warn("Failed to start watcher for serviceKey: {}", serviceKey, e);
        }
    }

    @Override
    public void destroy() throws Exception {
        serviceCache.clear();
        for (PathChildrenCache value : serviceWatchers.values()) {
            try {
                value.close();
            } catch (IOException e) {
                log.warn("Failed to close watcher. {}", value, e);
            }
        }
        serviceWatchers.clear();

        try {
            client.close();
            log.info("Closed Zookeeper client.");
        } catch (Exception e) {
            log.warn("Failed to close Zookeeper client.", e);
        }
        log.info("Zookeeper Service Discovery destroyed.");
    }
}
