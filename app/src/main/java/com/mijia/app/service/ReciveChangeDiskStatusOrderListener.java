package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReciveChangeDiskStatusOrderListener {
    void onRecive(DatagramPacket packet);
}
