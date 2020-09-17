package com.handong.framework.base;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.stream.MalformedJsonException;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.api.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.orhanobut.logger.Logger;
import com.qihuang.app.framework.R;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class BaseViewModel extends ViewModel implements GenericLifecycleObserver {


    public final MutableLiveData<Boolean> tokenExpir = new MutableLiveData<>();
    public final MutableLiveData<Boolean> error = new MutableLiveData<>();

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        switch (event) {
            case ON_DESTROY:
                compositeDisposable.dispose();
                break;
        }
    }

    public abstract class SimpleObserver<T> implements Observer<T> {
        public SimpleObserver() {
            RxJavaPlugins.setErrorHandler(throwable -> {
                //异常处理 onError 有时候不能捕获所有异常
                onError(throwable);
            });
        }

        @Override
        public void onSubscribe(Disposable d) {
            compositeDisposable.add(d);
        }

        @Override
        public void onNext(T t) {
            if (t instanceof ResponseBean) {
                ResponseBean responseBean = (ResponseBean) t;
                String msg = responseBean.getMessage();
                int status = responseBean.getStatus();

                if (responseBean.isSuccess()) {
                    onSuccess(t);
                } else if (responseBean.isMultipeDevice()) {
                    tokenExpir.postValue(true);
                    AccountHelper.logout();
                } else if (responseBean.isAccountFrozen()) {
                    onError(status, msg);
                    AccountHelper.logout();
                } else if (responseBean.isTokenExpire()) {
                    tokenExpir.postValue(true);
                    AccountHelper.logout();
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
                int code = ApiException.Code_Default;
                if (msg.toLowerCase().contains("token")) {
                    tokenExpir.postValue(true);
                } else {
                    onError(code, msg);
                }
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
        public abstract void onSuccess(@NonNull T t);


        /**
         * 连接失败或返回数据错误
         *
         * @param code errorMsg
         */
        public void onError(int code, String errorMsg) {
            error.postValue(true);
            if (!TextUtils.isEmpty(errorMsg)) {
                ToastUtils.showShort(errorMsg);
            }
        }
    }
}
