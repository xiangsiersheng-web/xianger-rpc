package com.ws.rpc.client.transport.socket;

import com.ws.rpc.client.dto.RpcRequestMetaData;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 1:04
 */

@Slf4j
public class SocketRpcClient implements RpcClient {
    private static final int CONNECT_TIMEOUT_MILLISECONDS = (int) TimeUnit.SECONDS.toMillis(10);

    @Override
    public RpcResponse sendRequest(RpcRequestMetaData rpcRequestMetaData) {
        InetSocketAddress serverSocketAddress = new InetSocketAddress(rpcRequestMetaData.getServiceAddress(), rpcRequestMetaData.getServicePort());
        try (Socket socket = new Socket()) {
            // 连接服务端，阻塞
            socket.connect(serverSocketAddress, CONNECT_TIMEOUT_MILLISECONDS);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcRequestMetaData.getRpcMessage().getBody());
            oos.flush();
            // 读取服务端返回的响应，阻塞
            int timeout = rpcRequestMetaData.getTimeout();
            if (timeout > 0) {
                socket.setSoTimeout(timeout);   // 设置读取超时时间
            }
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (RpcResponse) ois.readObject();
        } catch (Exception e) {
            throw new RpcException("The socket client failed to send or receive message.", e);
        }
    }
}
