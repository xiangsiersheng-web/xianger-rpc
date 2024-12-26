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

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
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
