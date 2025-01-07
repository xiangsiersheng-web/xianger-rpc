package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:12
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final boolean useWeight;

    // 构造函数：如果 useWeight 为 true，则使用权重轮询
    public RoundRobinLoadBalance(boolean useWeight) {
        this.useWeight = useWeight;
    }

    // 默认构造函数，不使用权重
    public RoundRobinLoadBalance() {
        this(false);
    }

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        if (useWeight) {
            return selectWithWeight(serviceInfoList);
        } else {
            return selectWithoutWeight(serviceInfoList);
        }
    }

    private ServiceInfo selectWithWeight(List<ServiceInfo> serviceInfoList) {
        int totalWeight = serviceInfoList.stream().mapToInt(ServiceInfo::getWeight).sum();
        int prev, next;
        do {
            prev = atomicInteger.get();
            next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
        } while (!atomicInteger.compareAndSet(prev, next));
        int idx = prev % totalWeight;

        int currWright = 0;
        for (ServiceInfo serviceInfo : serviceInfoList) {
            currWright += serviceInfo.getWeight();
            if (idx < currWright) {
                return serviceInfo;
            }
        }
        return serviceInfoList.get(0);
    }

    private ServiceInfo selectWithoutWeight(List<ServiceInfo> serviceInfoList) {
        int size = serviceInfoList.size();

//        int prev = atomicInteger.get();
//        int next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
//        while (!atomicInteger.compareAndSet(prev, next)) {
//            prev = atomicInteger.get();
//            next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
//        }
        int prev, next;
        do {
            prev = atomicInteger.get();
            next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
        } while (!atomicInteger.compareAndSet(prev, next));

        return serviceInfoList.get(prev % size);
    }
}
