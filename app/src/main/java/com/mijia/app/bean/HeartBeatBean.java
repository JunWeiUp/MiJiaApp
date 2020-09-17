package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

public class HeartBeatBean extends BaseBean {

    private String fip;
    private String fport;
    private String sip;
    private String sport;

    public String getFip() {
        return fip;
    }

    public void setFip(String fip) {
        this.fip = fip;
    }

    public String getFport() {
        return fport;
    }

    public void setFport(String fport) {
        this.fport = fport;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
