package com.ws.rpc.server.transport;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 3:28
 */
public interface RpcServer {

    /**
     * 启动服务
     * @param port
     */
    void start(int port);
}
