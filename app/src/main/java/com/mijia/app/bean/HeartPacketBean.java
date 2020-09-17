package com.mijia.app.bean;

public class HeartPacketBean {


    /**
     * userName : test_user
     * userId : aaaaaaaaaa
     * ip : 192.168.1.100
     * port : 8888
     */

    private String userName;
    private String userId;
    private String ip;
    private String port;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
