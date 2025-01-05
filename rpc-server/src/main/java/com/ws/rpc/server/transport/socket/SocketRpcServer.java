package com.ws.rpc.server.transport.socket;

import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 3:30
 */

@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int coreNum = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            coreNum,
            coreNum * 2,
            60,
            java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.LinkedBlockingQueue<>(10000),
            new ThreadPoolExecutor.AbortPolicy()
    );


    @Override
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(port));
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("The client connected [{}:{}].", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRpcRequestHandler(socket));
            }
            // 服务端断开后，关闭线程池
            threadPool.shutdown();
        } catch (IOException e) {
            throw new RpcException(String.format("The socket server failed to start on port %d.", port), e);
        }
    }
}
