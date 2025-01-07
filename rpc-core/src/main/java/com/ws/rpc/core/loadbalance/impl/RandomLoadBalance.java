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
    private final Random random = new Random();
    private final boolean useWeight;

    public RandomLoadBalance(boolean useWeight) {
        this.useWeight = useWeight;
    }

    public RandomLoadBalance() {
        this(false);
    }

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        if (useWeight) {
            // 基于权重进行随机选择
            return selectByWeight(serviceInfoList);
        } else {
            // 基于简单的随机选择
            return serviceInfoList.get(random.nextInt(serviceInfoList.size()));
        }
    }

    private ServiceInfo selectByWeight(List<ServiceInfo> serviceInfoList) {
        int totalWeight = serviceInfoList.stream().mapToInt(ServiceInfo::getWeight).sum();
        int randomWeight = random.nextInt(totalWeight);
        for (ServiceInfo serviceInfo : serviceInfoList) {
            randomWeight -= serviceInfo.getWeight();
            if (randomWeight < 0) {
                return serviceInfo;
            }
        }
        return serviceInfoList.get(0);
    }
}
