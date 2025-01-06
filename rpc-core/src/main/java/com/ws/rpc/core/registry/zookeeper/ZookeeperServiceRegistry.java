package com.ws.rpc.core.registry.zookeeper;

import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.registry.ServiceRegistry;
import com.ws.rpc.core.registry.zookeeper.util.CuratorUtils;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.ws.rpc.core.registry.zookeeper.ZookeeperConstants.BASE_PATH;
import static com.ws.rpc.core.registry.zookeeper.ZookeeperConstants.DEFAULT_SERIALIZATION_TYPE;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-06 22:23
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private final CuratorFramework zkClient;
    private final Serialization serialization;
    private final Set<ServiceInfo> serviceInfos;

    public ZookeeperServiceRegistry(String registryAddress) {
        zkClient = CuratorUtils.getZkClient(registryAddress);
        serialization = SerializationFactory.getSerialization(DEFAULT_SERIALIZATION_TYPE);
        serviceInfos = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void register(ServiceInfo serviceInfo) throws Exception {
        String path = BASE_PATH + "/" + serviceInfo.getServiceKey() + "/" + serviceInfo.getAddress() + ":" + serviceInfo.getPort();
        byte[] data = serialization.serialize(serviceInfo);
        try {
            CuratorUtils.createPersistentNode(zkClient, path, data);
            serviceInfos.add(serviceInfo);
            log.info("Register service successfully. The service info is:[{}]", serviceInfo);
        } catch (Exception e) {
            log.warn("Failed to register service: [{}]", serviceInfo);
            throw new RuntimeException("Failed to register service.", e);
        }
    }

    @Override
    public void unregister(ServiceInfo serviceInfo) {
        String path = BASE_PATH + "/" + serviceInfo.getServiceKey() + "/" + serviceInfo.getAddress() + ":" + serviceInfo.getPort();
        try {
            CuratorUtils.deleteNode(zkClient, path);
            serviceInfos.remove(serviceInfo);
            log.info("Unregister service successfully. The service info is:[{}]", serviceInfo);
        } catch (Exception e) {
            log.warn("Failed to unregister service: [{}]", serviceInfo);
            throw new RuntimeException("Failed to unregister service.", e);
        }
    }

    @Override
    public void destroy() {
        try {
            for (ServiceInfo serviceInfo : serviceInfos) {
                String parentPath = BASE_PATH + "/" + serviceInfo.getServiceKey();
                String path = parentPath + "/" + serviceInfo.getAddress() + ":" + serviceInfo.getPort();
                try {
                    CuratorUtils.deleteNode(zkClient, path); // 直接删除节点
                    log.info("Deleted service node at path: {}", path);

                    // 如果父节点没有子节点，删除父节点
                    List<String> children = CuratorUtils.getChildrenNodes(zkClient, parentPath);
                    if (children.isEmpty()) {
                        CuratorUtils.deleteNode(zkClient, parentPath); // 删除父节点
                        log.info("Deleted empty parent node at path: {}", parentPath);
                    }
                } catch (Exception e) {
                    log.warn("Failed to delete service node at path: {}", path, e);
                }
            }

            serviceInfos.clear();
        } catch (Exception e) {
            log.warn("Failed to destroy the registry", e);
            throw new RuntimeException("Failed to destroy the registry.", e);
        } finally {
            try {
                if (zkClient != null) {
                    zkClient.close();
                    log.info("Zookeeper client closed successfully.");
                }
            } catch (Exception e) {
                log.warn("Failed to close zkClient", e);
            }
            log.info("Zookeeper Service Registry destroyed.");
        }
    }
}
