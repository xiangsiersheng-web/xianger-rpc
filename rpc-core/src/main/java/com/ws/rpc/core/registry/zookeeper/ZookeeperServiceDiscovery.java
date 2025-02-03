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
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;

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
    private final Map<String, TreeCache> serviceWatchers;
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
            return null;
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
            // 尝试初始化节点
            initializeNode(serviceKey);
            return serviceCache.get(serviceKey);
        }
    }

    /**
     * 初始化节点
     *
     * @param serviceKey 服务key
     * @throws Exception 异常
     */
    private void initializeNode(String serviceKey) throws Exception {
        // 首次发现需要初始化缓存，并添加一个监听器
        List<ServiceInfo> serviceInfos = fetchServiceInfos(serviceKey);
        serviceCache.put(serviceKey, serviceInfos);
        log.info("Loaded and cached {} instances for serviceKey: {}", serviceInfos.size(), serviceKey);

        // 添加一个监听器
        addServiceCacheListener(serviceKey);
    }


    /**
     * 获取服务信息(查询子节点)
     *
     * @param serviceKey 服务key
     * @return 服务信息
     * @throws Exception 异常
     */
    private List<ServiceInfo> fetchServiceInfos(String serviceKey) throws Exception {
        String path = BASE_PATH + "/" + serviceKey;
        // fix: 如果该节点不存在，就会执行错误，也无法在后续注册监听事件。所以应该创建一个空的节点
        if (!CuratorUtils.checkNodeExists(client, path)) {
            CuratorUtils.createPersistentNode(client, path, null);
            log.debug("The client found that the node does not exist and created a new node.");
        }
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

    /**
     * 添加服务缓存监听器
     * @param serviceKey
     */
    private void addServiceCacheListener(String serviceKey) {
        // 如果已经有监听器了，则不再添加
        if (serviceWatchers.containsKey(serviceKey)) {
            log.info("Watcher already added for serviceKey: {}", serviceKey);
            return;
        }

        String path = BASE_PATH + "/" + serviceKey;

        TreeCache treeCache = TreeCache.newBuilder(client, path)
                .setCacheData(true)
                .setMaxDepth(1) // 只监听当前节点和直接子节点
                .build();

        treeCache.getListenable().addListener((client, event) -> {
            try {
                handleTreeCacheEvent(serviceKey, path, event);
            } catch (Exception e) {
                log.error("Error handling TreeCache event", e);
            }
        });

        try {
            treeCache.start();
            serviceWatchers.put(serviceKey, treeCache);
            log.info("Added TreeCache watcher for serviceKey: {}", serviceKey);
        } catch (Exception e) {
            log.warn("Failed to start TreeCache for serviceKey: {}", serviceKey, e);
        }
    }

    /**
     * 处理TreeCache事件
     * @param serviceKey
     * @param watchPath
     * @param event
     * @throws Exception
     */
    private void handleTreeCacheEvent(String serviceKey, String watchPath, TreeCacheEvent event) throws Exception {
        if (event.getData() == null) return;

        String eventPath = event.getData().getPath();
        log.debug("Processing event [{}] on path: {}", event.getType(), eventPath);

        switch (event.getType()) {
            case NODE_REMOVED:
                // 主节点被删除
                if (watchPath.equals(eventPath)) {
                    log.info("Service node {} removed, cleaning resources", serviceKey);
                    cleanUpResources(serviceKey);
                }
                // 子节点被删除
                else {
                    refreshServiceCache(serviceKey);
                }
                break;

            case NODE_ADDED:    // 添加和更新事件的处理逻辑相同
            case NODE_UPDATED:
                // 主节点重新创建时重新初始化
                if (watchPath.equals(eventPath)) {
                    log.info("Service node {} recreated", serviceKey);
                    initializeNode(serviceKey);
                }
                refreshServiceCache(serviceKey);
                break;

            case INITIALIZED:
                log.info("TreeCache initialized for {}", serviceKey);
                break;
        }
    }

    /**
     * 节点删除时清理资源
     * @param serviceKey
     */
    private synchronized void cleanUpResources(String serviceKey) {
        // 清理缓存
        serviceCache.remove(serviceKey);

        // 关闭并移除监听器
        TreeCache watcher = serviceWatchers.remove(serviceKey);
        if (watcher != null) {
            try {
                watcher.close();
                log.info("Closed TreeCache for serviceKey: {}", serviceKey);
            } catch (Exception e) {
                log.warn("Error closing TreeCache for {}", serviceKey, e);
            }
        }
    }

    /**
     * 子节点变更，刷新缓存
     * @param serviceKey
     * @throws Exception
     */
    private void refreshServiceCache(String serviceKey) throws Exception {
        List<ServiceInfo> serviceInfos = fetchServiceInfos(serviceKey);
        if (!serviceInfos.isEmpty()) {
            serviceCache.put(serviceKey, serviceInfos);
        } else {
            log.warn("No instances available after refresh, serviceKey: {}", serviceKey);
        }
    }

    @Override
    public void destroy() throws Exception {
        serviceCache.clear();
        for (TreeCache value : serviceWatchers.values()) {
            try {
                value.close();
            } catch (Exception e) {
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
