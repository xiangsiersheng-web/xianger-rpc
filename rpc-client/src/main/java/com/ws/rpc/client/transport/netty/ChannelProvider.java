package com.ws.rpc.client.transport.netty;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 20:48
 */
public class ChannelProvider {
    private final Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    public Channel get(String serviceAddress, Integer servicePort) {
        String key = serviceAddress + ":" + servicePort;
        if (channelCache.containsKey(key)) {
            Channel channel = channelCache.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelCache.remove(key);
            }
        }
        return null;
    }

    public void set(String serviceAddress, Integer servicePort, Channel channel) {
        String key = serviceAddress + ":" + servicePort;
        channelCache.put(key, channel);
    }
}
