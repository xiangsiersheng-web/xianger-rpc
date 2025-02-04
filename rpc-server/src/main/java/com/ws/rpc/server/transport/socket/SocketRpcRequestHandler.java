package com.ws.rpc.server.transport.socket;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.protocol.RpcMessage;
import com.ws.rpc.server.handler.RpcRequestHandler;
import io.protostuff.Rpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.server.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 3:57
 */

@Slf4j
public class SocketRpcRequestHandler implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler requestHandler;

    public SocketRpcRequestHandler(Socket socket) {
        this.socket = socket;
        this.requestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        log.debug("The server handle client message by thread {}.", Thread.currentThread().getName());
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()) ;
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            // SocketServer 接受和发送的数据为：RpcMessage, RpcMessage
            RpcMessage rpcMessage = (RpcMessage) ois.readObject();
            RpcResponse rpcResponse = new RpcResponse();
            try {
                Object result = requestHandler.handleRpcRequest(rpcMessage);
                rpcResponse.setResult(result);
            } catch (Exception e) {
                log.error("RpcRequestHandler handle error: {}", e.getMessage());
                rpcResponse.setException(e);
            }
            rpcMessage.setBody(rpcResponse);
            oos.writeObject(rpcMessage);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("The socket server failed to handle client rpc request.", e);
        }
    }
}
