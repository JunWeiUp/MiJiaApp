package com.handongkeji.interfaces;

/**
 * Created by Administrator on 2017/6/23 0023.
 */

public interface OnResult<T> {

    void onSuccess(T t);

    void onFailed(String errorMsg);
}
