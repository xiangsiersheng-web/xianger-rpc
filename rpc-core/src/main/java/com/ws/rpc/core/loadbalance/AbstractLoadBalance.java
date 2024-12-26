package com.ws.rpc.core.loadbalance;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:08
 */
public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public ServiceInfo select(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest) {
        if(serviceInfoList == null || serviceInfoList.isEmpty()) {
            return null;
        }
        if (serviceInfoList.size() == 1) {
            return serviceInfoList.get(0);
        }
        return doSelect(serviceInfoList, rpcRequest);
    }

    protected abstract ServiceInfo doSelect(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest);
}
