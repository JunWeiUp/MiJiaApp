package com.handongkeji.base;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2017/10/18 0018.
 */

public interface IBaseView {


   FragmentActivity getActivity();

   void toastError(String message);

    void toastInfo(String message);

    void toastWarning(String message);

    void toastNormal(String message);

    void toastSuccess(String message);

    /**
     * 显示加载框
     * @param message
     */
    void showLoading(String message);

    /**
     * 加载完成，隐藏加载框
     */
    void dismissLoading();


    void goNotFund();

}
