package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReciveExitOrderListener  {
    void onReceiveOrder(DatagramPacket packet);
}
