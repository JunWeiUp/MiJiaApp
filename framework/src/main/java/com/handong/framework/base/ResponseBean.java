package com.handong.framework.base;

import com.google.gson.annotations.SerializedName;

public class ResponseBean<T> {

    private static final int SUCCESS_CODE = 1;
    private static final int STATUS_2 = 200;
    private static final int MULTIDEVICE_CODE = 602;
    private static final int ACCOUNT_FROZEN_CODE = 609;
    private static final int TOKEN_EXPIRE_CODE = 403;

    // 有意义的状态
    private static final int AVAILABLE_CODE = 3;
    private static final int OTHER_CODE = 2;

    @SerializedName(value = "status", alternate = {"code"})
    private int status;
    @SerializedName(value = "message", alternate = {"msg"})
    private String message;
    private T data;

    public boolean isSuccess() {
        return SUCCESS_CODE == status || STATUS_2 == status || AVAILABLE_CODE == status || OTHER_CODE == status;
    }

    public boolean isMultipeDevice() {
        return MULTIDEVICE_CODE == status;
    }

    public boolean isAccountFrozen() {
        return status == ACCOUNT_FROZEN_CODE;
    }

    public boolean isTokenExpire() {
        return status == TOKEN_EXPIRE_CODE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
