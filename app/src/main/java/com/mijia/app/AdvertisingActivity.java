package com.mijia.app;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.bean.AdvertisementBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivityAdvertisingBinding;
import com.mijia.app.ui.other.activity.LoginActivity;
import com.mijia.app.ui.other.activity.WebViewActivity;
import com.mijia.app.viewmodel.AdvertisementViewModel;
import com.nevermore.oceans.uits.ImageLoader;

public class AdvertisingActivity extends BaseActivity<ActivityAdvertisingBinding, BaseViewModel> {

    private AdvertisementViewModel mAdvertisementViewModel;

    private String version = "";
    private String isChecking = "";

    @Override
    public int getLayoutRes() {
        return R.layout.activity_advertising;
    }

    @Override
    protected void initBefore() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        TimeCount timeCount = new TimeCount(5000, 1000);
        timeCount.start();

        mBinding.tvSkip.setOnClickListener(v -> {
            timeCount.onFinish();
            timeCount.cancel();
        });

        mBinding.ivPic.setOnClickListener(v -> {
            timeCount.onFinish();
            timeCount.cancel();
            startActivity(new Intent(this, WebViewActivity.class)
                    .putExtra(Constants.WEBURL, "https://www.pgyer.com/4mqK")
                    .putExtra("title", "广告页")
                    .putExtra("isAdvertsing", true));
        });

        mAdvertisementViewModel = new AdvertisementViewModel();

        mAdvertisementViewModel.mReduceLiveData.observe(this, advertisementBean -> {
            if (advertisementBean != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                version = advertisementBean.getAndroidVersion();
                isChecking = advertisementBean.getAndroidIsChecking();
                sharedPreferences.edit().putString("version",version).putString("isChecking",isChecking).apply();

                ImageLoader.loadImage(mBinding.ivPic, advertisementBean.getBannerImg(), R.drawable.guanggao1);
            }
        });

        mAdvertisementViewModel.getAdvertisementData();
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            goNext();
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            mBinding.tvSkip.setText("跳过(" + (int) (millisUntilFinished / 1000) + ")");
        }
    }

    private void goNext() {
        if (StringUtils.isEmpty(AccountHelper.getUserId())) {
            startActivity(new Intent(AdvertisingActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(AdvertisingActivity.this, MainActivity.class));
        }
        finish();
    }
}
