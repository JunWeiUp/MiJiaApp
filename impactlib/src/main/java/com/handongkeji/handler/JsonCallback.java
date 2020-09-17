package com.handongkeji.handler;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public interface JsonCallback {

    void onSuccess(String json);

    void onFailed(Throwable throwable);
}
