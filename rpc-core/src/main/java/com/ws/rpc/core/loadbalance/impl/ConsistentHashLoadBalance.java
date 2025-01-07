package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ref: refer to dubbo consistent hash load balance: https://github.com/apache/dubbo/blob/2d9583adf26a2d8bd6fb646243a9fe80a77e65d5/dubbo-cluster/src/main/java/org/apache/dubbo/rpc/cluster/loadbalance/ConsistentHashLoadBalance.java
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:18
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    private final Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        // 确定请求的key(服务名+方法名) eg: com.ws.rpc.example.server.impl.HelloServiceImpl.sayHello
        String key = rpcRequest.getServiceKey() + '.' + rpcRequest.getMethodName();
        int identityHashCode = System.identityHashCode(serviceInfoList);
        ConsistentHashSelector selector = selectors.get(key);
        if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
            // 创建新的选择器
            selectors.put(key, new ConsistentHashSelector(serviceInfoList, 160, identityHashCode));
            selector = selectors.get(key);
        }
        // 将服务名+方法名+请求参数作为selectkey
        String selectKey = key + Arrays.toString(rpcRequest.getParameters());
        return selector.select(selectKey);
    }

    private final static class ConsistentHashSelector {
        /**
         * TreeMap 存储虚拟节点与实际节点的映射关系
         */
        private final TreeMap<Long, ServiceInfo> virtualInvokers;

        /**
         * invokers 的原始哈希码，用于检测 invokers 是否变化
         */
        private final int identityHashCode;

        /**
         * 构造方法，初始化虚拟节点和哈希环
         *
         * @param invokers         服务节点列表
         * @param replicaNumber    每个服务节点的虚拟节点数
         * @param identityHashCode invokers 的原始哈希码
         */
        public ConsistentHashSelector(List<ServiceInfo> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (ServiceInfo invoker : invokers) {
                String address = invoker.getHost() + ':' + invoker.getPort();
                // 将一个invoker映射到replicaNumber个虚拟节点上，用treemap存储虚拟节点（long）到serviceInfo的对应关系
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 对每个地址加上索引进行 MD5 哈希
                    byte[] digest = md5(address + i);
                    for (int h = 0; h < 4; h++) {
                        // 提取 MD5 摘要的四部分生成虚拟节点
                        long hash = hash(digest, h);
                        virtualInvokers.put(hash, invoker);
                    }
                }
            }
        }

        /**
         * 对字符串进行 MD5 哈希，返回摘要字节数组
         *
         * @param key 输入字符串
         * @return 长度为 16 的字节数组（MD5 摘要）
         */
        private byte[] md5(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                return md.digest(key.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 algorithm not available", e);
            }
        }

        /**
         * 根据 MD5 摘要生成哈希值
         *
         * @param digest MD5 摘要
         * @param number 当前分段索引（0-3）
         * @return 哈希值
         */
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        /**
         * 选择哈希环上最接近的服务节点
         *
         * @param key 用于哈希计算的键值
         * @return 对应的服务节点
         */
        public ServiceInfo select(String key) {
            // 对 key 进行 MD5 运算，取第一段生成哈希值
            byte[] digest = md5(key);
            long hash = hash(digest, 0);
            return selectForKey(hash);
        }

        /**
         * 在哈希环上查找第一个大于或等于指定哈希值的节点
         *
         * @param hash 哈希值
         * @return 服务节点
         */
        private ServiceInfo selectForKey(long hash) {
            // 找到哈希值大于或等于当前哈希的第一个虚拟节点
            Map.Entry<Long, ServiceInfo> entry = virtualInvokers.ceilingEntry(hash);
            // 如果没有找到（说明哈希值超出了环的最大值），则返回环的第一个节点
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }

        /**
         * 获取当前哈希选择器的身份标识哈希码
         *
         * @return 哈希码
         */
        public int getIdentityHashCode() {
            return identityHashCode;
        }
    }
}
