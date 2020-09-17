package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

public class TransOrderResultBean extends BaseBean {



    private String jsonResult;
    private String orderId;

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
