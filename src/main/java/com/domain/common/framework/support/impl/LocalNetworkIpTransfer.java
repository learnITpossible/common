package com.domain.common.framework.support.impl;

import com.domain.common.framework.support.SrvServerIpTransfer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class LocalNetworkIpTransfer implements SrvServerIpTransfer {

    private String cached;

    @Override
    public String getIp() throws Exception {

        if (cached != null) {
            return cached;
        }
        // 一个主机有多个网络接口
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            // 每个网络接口,都会有多个"网络地址",比如一定会有lookback地址,会有siteLocal地址等.以及IPV4或者IPV6    .
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
                    return address.getHostAddress();
                }
            }
        }
        return null;
    }

    @Override
    public void reset() {

    }

}
