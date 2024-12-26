package com.ws.rpc.core.registry;

import com.ws.rpc.core.dto.ServiceInfo;

public interface ServiceRegistry {
    /**
     * 注册服务
     * @param serviceInfo
     * @throws Exception
     */
    void register(ServiceInfo serviceInfo) throws Exception;

    /**
     * 注销服务
     * @param serviceInfo
     * @throws Exception
     */
    void unregister(ServiceInfo serviceInfo) throws Exception;

    /**
     * 销毁
     * @throws Exception
     */
    void destroy() throws Exception;
}
