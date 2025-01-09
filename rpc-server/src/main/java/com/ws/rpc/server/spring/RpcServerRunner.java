package com.ws.rpc.server.spring;

import com.ws.rpc.core.registry.ServiceRegistry;
import com.ws.rpc.server.config.RpcServerProperties;
import com.ws.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

/**
 * RpcServerRunner - 启动 RPC 服务器
 * @author ws
 * @version 1.0
 * @date 2024-12-28 21:40
 */
@Slf4j
public class RpcServerRunner implements CommandLineRunner {

    private final RpcServer rpcServer;
    private final RpcServerProperties properties;
    private final ServiceRegistry serviceRegistry;

    public RpcServerRunner(RpcServer rpcServer, RpcServerProperties properties, ServiceRegistry serviceRegistry) {
        this.rpcServer = rpcServer;
        this.properties = properties;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动 RPC 服务器
        new Thread(() -> rpcServer.start(properties.getPort())).start();
        log.info("Rpc server [{}] started. AppName: {}, Port: {}",
                rpcServer.getClass().getSimpleName(), properties.getProviderName(), properties.getPort());

        // 添加 JVM 关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutting down RpcServer...");
                serviceRegistry.destroy();
                log.info("RpcServer stopped and unregistered from the registry.");
            } catch (Exception e) {
                log.error("Error occurred while shutting down RpcServer.", e);
            }
        }));
    }
}
