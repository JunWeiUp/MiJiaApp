package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReceiveOrderListener {
    void onReceiveOrder(DatagramPacket packet);
}
