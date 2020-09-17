package com.nevermore.oceans.http;

/**
 *
 * 网络响应回调
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/5 0005
 * @github https://github.com/XuNeverMore
 */

public interface ResponseCallback<T> {

    /**
     * 网络请求成功
     * @param t 返回的实体类
     */
    void onSuccess(T t);

    /**
     * 请求失败回调
     * @param status 错误状态码
     * @param throwable 错误信息
     */
    void onFailed(int status, Throwable throwable);

}
