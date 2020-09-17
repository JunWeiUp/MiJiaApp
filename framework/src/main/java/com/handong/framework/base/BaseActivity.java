package com.handong.framework.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.qihuang.app.framework.R;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/10/11 0011.
 */

public abstract class BaseActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity
        implements DataBindingProvider {

    public T mBinding;
    public VM mViewModel;
    private QMUITipDialog tipDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBefore();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBinding = DataBindingUtil.setContentView(this, getLayoutRes());


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            StatusBarUtil.setColor(this, Color.parseColor("#50B2B2B2"));
        } else {
            StatusBarUtil.setTranslucent(this, 0);
            StatusBarUtil.setLightMode(this);
            StatusBarUtil.setColor(this, Color.WHITE, 0);
        }

        initViewModel();
        mViewModel.tokenExpir.observe(this, aBoolean -> {
            Intent intent = new Intent(getPackageName() + ".com.action.login");
            if (intent.resolveActivity(getPackageManager()) != null) {
                ToastUtils.showShort(getString(R.string.login_status_invalid));
                finishAffinity();
                startActivity(intent);
            }
        });

        mViewModel.error.observe(this, a -> {
            dismissLoading();
        });

        initView(savedInstanceState);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    protected void initBefore() {

    }

    @Override
    public void setContentView(int res) {
        super.setContentView(res);
//        AndroidBug5497Workaround.assistActivity(this);    //兼容Edittext弹不起来
    }

    protected void initViewModel() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            Type argument = actualTypeArguments[1];
            mViewModel = ViewModelProviders.of(this).get((Class<VM>) argument);
        }
    }

    public void showLoading(CharSequence msg) {
        if (tipDialog!=null) {
            tipDialog.dismiss();
        }
        tipDialog = new QMUITipDialog.Builder(this)
                .setTipWord(msg)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .create(true);
        tipDialog.show();
    }

    public void dismissLoading() {
        if (tipDialog != null) {
            tipDialog.dismiss();
            tipDialog = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

}
