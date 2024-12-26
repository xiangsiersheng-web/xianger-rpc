package com.ws.rpc.core.loadbalance.impl;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:18
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        return null;
    }
}
