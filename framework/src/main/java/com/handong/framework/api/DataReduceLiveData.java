package com.handong.framework.api;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.stream.MalformedJsonException;
import com.handong.framework.base.BaseBean;
import com.handong.framework.base.ResponseBean;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;

public class DataReduceLiveData<T> extends MutableLiveData<T> implements Observer<T> {

    private static final int SUCCESS_CODE = 0;
    private static final int STATUS_2 = 200;
    private static final int MULTIDEVICE_CODE = 602;
    private static final int ACCOUNT_FROZEN_CODE = 609;
    private static final int TOKEN_EXPIRE_CODE = 403;

    private static final int Server_Order_No_Finish = -718; //服务器转发指令未执行

    private Disposable disposable;

    private GenericLifecycleObserver lifecycleObserver = new GenericLifecycleObserver() {
        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            switch (event) {
                case ON_DESTROY:
                    source.getLifecycle().removeObserver(this);
                    dispose();
                    break;
                default:
                    break;
            }
        }
    };

    public DataReduceLiveData() {
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull android.arch.lifecycle.Observer<T> observer) {
        super.observe(owner, observer);
        owner.getLifecycle().addObserver(lifecycleObserver);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
        if (t instanceof BaseBean) {

            String msg = ((BaseBean) t).getErr().getMsg();
            int status = ((BaseBean) t).getErr().getCode();

            if (SUCCESS_CODE == status || STATUS_2 == status) {

                onSuccess(t);

            } else if (status == Server_Order_No_Finish) {

                onSuccess(t);

            } else if (MULTIDEVICE_CODE == status) {

//                AccountHelper.mutildeviceLogin();
                dispose();

            } else if (ACCOUNT_FROZEN_CODE == status) {

                msg = "账号被冻结";
                onError(status, msg);

            } else if (TOKEN_EXPIRE_CODE == status) {

//                AccountHelper.newLogin();
                dispose();
            } else {
                onError(status, msg);
            }
        } else {


            onSuccess(t);


        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof CompositeException) {
            CompositeException compositeE = (CompositeException) e;
            for (Throwable throwable : compositeE.getExceptions()) {
                if (throwable instanceof SocketTimeoutException) {
                    onError(ApiException.Code_TimeOut, ApiException.SOCKET_TIMEOUT_EXCEPTION);
                } else if (throwable instanceof ConnectException) {
                    onError(ApiException.Code_UnConnected, ApiException.CONNECT_EXCEPTION);
                } else if (throwable instanceof UnknownHostException) {
                    onError(ApiException.Code_UnConnected, ApiException.CONNECT_EXCEPTION);
                } else if (throwable instanceof MalformedJsonException) {
                    onError(ApiException.Code_MalformedJson, ApiException.MALFORMED_JSON_EXCEPTION);
                } else {
                    onError(ApiException.Code_Default, "unknow error");
                }
            }
        } else {
            String msg = e.getMessage();
            if (msg == null) {
                onError(ApiException.Code_Default, e.toString());
                return;
            }
            int code;
//            if (msg.contains("#")) {
//                code = Integer.parseInt(msg.split("#")[0]);
//                onError(code, msg.split("#")[1]);
//            } else {
            code = ApiException.Code_Default;
            if (msg.toLowerCase().contains("token")) {
//                    newLogin();
            } else {
                onError(code, msg);
            }

//            }
        }
    }

    @Override
    public void onComplete() {

    }

    /**
     * 加载成功
     *
     * @param t 获得的数据实体
     */
    public void onSuccess(@NonNull T t) {
        postValue(t);
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 连接失败或返回数据错误
     *
     * @param code errorMsg
     */
    public void onError(int code, String errorMsg) {
        postValue(null);
        if (!TextUtils.isEmpty(errorMsg)) {
            ToastUtils.showShort(errorMsg);
        }
        dispose();
    }
}
