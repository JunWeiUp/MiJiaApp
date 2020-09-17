package com.handong.framework.base;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public interface DataCallback<T> {


    void onSuccess(T t);

    void onFiled(int status, String message);
}
