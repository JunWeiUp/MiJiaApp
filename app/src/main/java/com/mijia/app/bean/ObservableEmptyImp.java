package com.mijia.app.bean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ObservableEmptyImp<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T o) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
