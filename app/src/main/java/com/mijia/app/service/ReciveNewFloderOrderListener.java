package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReciveNewFloderOrderListener {
    void onRecive(DatagramPacket datagramPacket);
}
