package com.mijia.app;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.blankj.utilcode.util.ServiceUtils;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivitySplashBinding;
import com.mijia.app.service.MiJiaService;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @ClassName:SplashActivity
 * @PackageName:com.anxinxiaoyuan.app
 * @Create On 2018/8/28 0028   09:34
 * @Site:http://www.handongkeji.com
 * @author:zhouhao
 * @Copyrights 2018/8/28 0028 handongkeji All rights reserved.
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding, BaseViewModel> {

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initBefore() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
//        ServiceUtils.startService(MiJiaService.class);
        regToWx();
    }




    // IWXAPI 是第三方app和微信通信的openApi接口
    private IWXAPI api;

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);

        // 将应用的appId注册到微信
        api.registerApp(Constants.APP_ID);

//        //建议动态监听微信启动广播进行注册到微信
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // 将该app注册到微信
//                api.registerApp(Constants.APP_ID);
//            }
//        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

    }

    @Override
    public void onResume() {
        super.onResume();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mBinding.getRoot(), View.ALPHA, 0.1F, 1F);
        objectAnimator.setDuration(1500);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimateEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    private void onAnimateEnd() {
        startActivity(new Intent(SplashActivity.this, AdvertisingActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
