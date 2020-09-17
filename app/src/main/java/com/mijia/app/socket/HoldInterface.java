package com.mijia.app.socket;

import java.net.DatagramSocket;

public interface HoldInterface {
    /**
     * @param ip
     * @param port

     */
    void sendHoleInfo(String ip, String port, DatagramSocket client);

}
