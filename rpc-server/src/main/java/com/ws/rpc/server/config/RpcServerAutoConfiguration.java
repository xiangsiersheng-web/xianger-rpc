package com.ws.rpc.server.config;

import com.ws.rpc.core.registry.ServiceRegistry;
import com.ws.rpc.core.registry.zookeeper.ZookeeperServiceRegistry;
import com.ws.rpc.server.spring.RpcBeanPostProcessor;
import com.ws.rpc.server.spring.RpcServerRunner;
import com.ws.rpc.server.transport.RpcServer;
import com.ws.rpc.server.transport.netty.NettyRpcServer;
import com.ws.rpc.server.transport.socket.SocketRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 20:51
 */
@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {
    private final RpcServerProperties properties;

    public RpcServerAutoConfiguration(RpcServerProperties rpcServerProperties) {
        this.properties = rpcServerProperties;
    }

    @Bean(name = "serviceRegistry")
    public ServiceRegistry serviceRegistry(@Value("${rpc.server.registry:zookeeper}") String registryType) {
        switch (registryType) {
            case "zookeeper":
                return new ZookeeperServiceRegistry(properties.getRegistryAddr());
            default:
                throw new IllegalArgumentException("Unsupported registry type: " + registryType);
        }
    }


    @Bean(name = "rpcServer")
    public RpcServer rpcServer(@Value("${rpc.server.transport:netty}") String transportType) {
        switch (transportType) {
            case "netty":
                return new NettyRpcServer();
            case "socket":
                return new SocketRpcServer();
            default:
                throw new IllegalArgumentException("Unsupported transport type: " + transportType);
        }
    }

    @Bean(name = "rpcBeanPostProcessor")
    @ConditionalOnBean({ServiceRegistry.class})
    public RpcBeanPostProcessor rpcBeanPostProcessor(@Autowired RpcServerProperties properties,
                                                     @Autowired ServiceRegistry serviceRegistry) {
        return new RpcBeanPostProcessor(properties, serviceRegistry);
    }

    @Bean(name = "rpcServerRunner")
    @ConditionalOnBean({RpcServer.class, ServiceRegistry.class})
    public RpcServerRunner rpcServerRunner(@Autowired RpcServer rpcServer,
                                           @Autowired RpcServerProperties properties,
                                           @Autowired ServiceRegistry serviceRegistry) {
        return new RpcServerRunner(rpcServer, properties, serviceRegistry);
    }
}
