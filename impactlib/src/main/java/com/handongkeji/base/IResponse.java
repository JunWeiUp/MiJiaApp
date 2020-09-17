package com.handongkeji.base;

/**
 * Created by Administrator on 2017/10/19 0019.
 */

public interface IResponse <T>{

    void onFailed(int status,String message);

    void onSuccess(T t);
}
