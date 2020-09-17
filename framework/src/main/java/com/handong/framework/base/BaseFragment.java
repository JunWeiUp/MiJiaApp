package com.handong.framework.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.qihuang.app.framework.R;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
//import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public abstract class BaseFragment<T extends ViewDataBinding, VM extends BaseViewModel> extends Fragment implements DataBindingProvider {

    protected T binding;
    protected VM viewModel;
    private QMUITipDialog tipDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = initViewModel();
        getLifecycle().addObserver(viewModel);
        initView(savedInstanceState);

        if (((BaseActivity) getActivity()).mViewModel != viewModel && viewModel != null) {

            viewModel.tokenExpir.observe(this, aBoolean -> {
                Intent intent = new Intent(getActivity().getPackageName() + ".com.action.login");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    ToastUtils.showShort(getString(R.string.login_status_invalid));
                    getActivity().finishAffinity();
                    startActivity(intent);
                }
            });

            viewModel.error.observe(this, a -> {
                dismissLoading();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO:友盟
//        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO:友盟
//        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLifecycle().removeObserver(viewModel);
    }

    public void showLoading(CharSequence msg) {
        if (tipDialog!=null) {
            tipDialog.dismiss();
        }
        tipDialog = new QMUITipDialog.Builder(getActivity())
                .setTipWord(msg)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .create(true);
        tipDialog.show();
    }

    public void dismissLoading() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }
    }


    protected VM initViewModel() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type argument = actualTypeArguments[1];
            viewModel = ViewModelProviders.of(getActivity()).get((Class<VM>) argument);
        }
        return viewModel;
    }



    private CompositeDisposable mCompositeDisposable;

    public void addSubscription(Observable observable, Consumer consumer) {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer));


    }

    public void addSubscription(Observable observable, DisposableObserver observer) {
        if (mCompositeDisposable == null)
            mCompositeDisposable = new CompositeDisposable();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
        mCompositeDisposable.add(observer);
    }

    public void addSubscription(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    public void addSubscription(Observable observable) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    //RXjava取消注册，以避免内存泄露
    public void onUnsubscribe() {
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }


}
