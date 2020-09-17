package com.mijia.app.service;

import java.net.DatagramPacket;

public interface ReciveFloderOrderListener  {

    void onRecive(DatagramPacket packet);
}
