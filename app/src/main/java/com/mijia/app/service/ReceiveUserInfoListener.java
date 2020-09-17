package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReceiveUserInfoListener {
    void onReceiveUserInfo(DatagramPacket packet);
}