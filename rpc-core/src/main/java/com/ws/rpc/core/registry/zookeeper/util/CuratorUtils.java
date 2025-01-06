package com.ws.rpc.core.registry.zookeeper.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ws.rpc.core.registry.zookeeper.ZookeeperConstants.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-06 22:31
 */
@Slf4j
public final class CuratorUtils {

    /**
     * 通过连接地址创建zk客户端
     *
     * @param registryAddress 连接地址
     * @return zk客户端
     */
    public static CuratorFramework getZkClient(String registryAddress) {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(registryAddress)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .connectionTimeoutMs(CONNECT_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRY))
                .build();
        zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }


    public static void createPersistentNode(CuratorFramework zkClient, String path, byte[] data) throws Exception {
        if (zkClient.checkExists().forPath(path) != null) {
            log.warn("The node already exists. The node is:[{}]", path);
        } else {
            if (data != null) {
                // 如果有数据，创建带数据的节点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
                log.info("The node was created successfully. The node is:[{}], data:[{}]", path, data);
            } else {
                // 如果没有数据，创建空数据的节点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node was created successfully. The node is:[{}]", path);
            }
        }
    }


    public static void deleteNode(CuratorFramework zkClient, String path) throws Exception {
        if (zkClient.checkExists().forPath(path) != null) {
            zkClient.delete().deletingChildrenIfNeeded().forPath(path);
            log.info("The node was deleted successfully. The node is:[{}]", path);
        } else {
            log.warn("The node does not exist. The node is:[{}]", path);
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String path) throws Exception {
        try {
            return zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("Get children nodes for path [{}] failed.", path, e);
            throw e;
        }
    }

    public static byte[] getData(CuratorFramework zkClient, String path) throws Exception {
        try {
            return zkClient.getData().forPath(path);
        } catch (Exception e) {
            log.error("Get data for path [{}] failed.", path, e);
            throw e;
        }
    }
}
