package com.ws.rpc.server.transport.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ws.rpc.core.codec.RpcFrameDecoder;
import com.ws.rpc.core.codec.RpcMessageCodec;
import com.ws.rpc.server.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-02 0:53
 */
@Slf4j
public class NettyRpcServer implements RpcServer {
    @Override
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventLoopGroup serviceHandlerGroup = new DefaultEventLoopGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                new ThreadFactoryBuilder().setNameFormat("service-handler-%d").setDaemon(false).build()
        );
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输次数
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 临时存放已完成三次握手的请求的队列的最大长度，调大后可以提高服务器的并发能力
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            log.info("The client connected [{}].", ch.remoteAddress());
                            ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new RpcFrameDecoder());
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new RpcMessageCodec());
                            ch.pipeline().addLast(serviceHandlerGroup, new NettyRpcRequestHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(hostAddress, port).sync();
            log.info("server started on address: [{}:{}]", hostAddress, port);
            channelFuture.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            log.error("An error occurred while starting the server");
            throw new RuntimeException(e);
        } finally {
            log.info("Shutting down boss and worker event loops");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
