package com.nevermore.oceans.http;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class BaseEntity<T>{
    @SerializedName(value = "status",alternate = "code")
    private int status;

    @SerializedName(value = "message",alternate = {"msg","content"})
    private String message;
    private T data;

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
}
