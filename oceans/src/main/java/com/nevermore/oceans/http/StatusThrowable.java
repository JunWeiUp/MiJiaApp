package com.nevermore.oceans.http;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class StatusThrowable extends Throwable {
    private int status;

    public StatusThrowable() {
    }

    public StatusThrowable(String message) {
        super(message);
    }

    public StatusThrowable(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusThrowable(Throwable cause) {
        super(cause);
    }

    public StatusThrowable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
