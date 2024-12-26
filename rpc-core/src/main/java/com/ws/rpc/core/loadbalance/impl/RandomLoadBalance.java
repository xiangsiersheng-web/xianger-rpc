package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.AbstractLoadBalance;
import com.ws.rpc.core.loadbalance.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:10
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    Random random = new Random();

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        return serviceInfoList.get(random.nextInt(serviceInfoList.size()));
    }
}
