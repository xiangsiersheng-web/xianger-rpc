package com.ws.rpc.client.loadbalance;

import com.ws.rpc.core.extension.ExtensionLoader;
import com.ws.rpc.core.loadbalance.LoadBalance;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-16 15:14
 */
public class TestSPI {
    public static void main(String[] args) {
        ExtensionLoader<LoadBalance> extensionLoader = ExtensionLoader.getExtensionLoader(LoadBalance.class);
        LoadBalance loadBalance = extensionLoader.getExtension("roundRobin");
        LoadBalance loadBalance2 = extensionLoader.getExtension("roundRobin");
        System.out.println(loadBalance == loadBalance2);

        System.out.println(extensionLoader.getExtension("random"));
    }
}
