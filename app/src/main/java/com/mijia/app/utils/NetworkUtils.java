package com.mijia.app.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {

    /**
     * 获取内网IP地址
     * @return
     * @throws SocketException
     */
    public static String getLocalIPAddress() throws SocketException {
        for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
            NetworkInterface intf = en.nextElement();
            for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                InetAddress inetAddress = enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        return "null";
    }


}
