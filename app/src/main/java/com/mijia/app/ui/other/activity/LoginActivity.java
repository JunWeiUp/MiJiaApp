package com.mijia.app.ui.other.activity;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.AdvertisingActivity;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.SplashActivity;
import com.mijia.app.bean.CarouselBean;
import com.mijia.app.bean.LoginBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivityLoginBinding;
import com.mijia.app.dialog.PrivacyPolicyAndTermsOfUseDialog;
import com.mijia.app.ui.home.activity.FuWuXieYiActivity;
import com.mijia.app.ui.home.activity.YinSiZhengCeActivity;
import com.mijia.app.viewmodel.LoginViewModel;
import com.nevermore.oceans.uits.ImageLoader;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, BaseViewModel> {


    private LoginViewModel mLoginViewModel;

    private String version;

    private String isChecking;

    public static String LoginName = "18518754540";



    @Override
    public int getLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        version = sharedPreferences.getString("version", "");
        isChecking = sharedPreferences.getString("isChecking", "");

//        if (!StringUtils.isEmpty(version) && !StringUtils.isEmpty(isChecking)) {
//            if (version.equals(AppUtils.getAppVersionName())) {
//                if (isChecking.equals("1")) {
//                    mBinding.llLogin.setVisibility(View.VISIBLE);
//                    mBinding.rvLogin.setVisibility(View.GONE);
//                }
//            }
//        }

        if (getSharedPreferences("isFirst",MODE_PRIVATE).getBoolean("isFirst",true)) {

            PrivacyPolicyAndTermsOfUseDialog privacyPolicyAndTermsOfUseDialog = new PrivacyPolicyAndTermsOfUseDialog(this);

            privacyPolicyAndTermsOfUseDialog.show();

        }

        mBinding.ivSelected.setSelected(true);

        mBinding.ivSelected.setOnClickListener(view -> {
            mBinding.ivSelected.setSelected(!mBinding.ivSelected.isSelected());
        });

        mBinding.tvFuwuxieyi.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, FuWuXieYiActivity.class));
        });

        mBinding.tvYinsizhengce.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, YinSiZhengCeActivity.class));
        });

        mBinding.tvJiadenglu.setOnClickListener(view -> {
            String tel = mBinding.etTel.getText().toString();
            String pwd = mBinding.etPwd.getText().toString();
            if (!mBinding.ivSelected.isSelected()) {
                ToastUtils.showShort("请仔细阅读并同意用户协议和隐私政策！");
                return;
            }

            if (StringUtils.isEmpty(tel)) {
                ToastUtils.showShort("请输入账号！");
                return;
            }

            if (!RegexUtils.isMobileSimple(tel)) {
                ToastUtils.showShort("请输入正确的手机号！");
                return;
            }

            if (StringUtils.isEmpty(pwd)) {
                ToastUtils.showShort("请输入密码！");
                return;
            }

            if (tel.equals(LoginName) && pwd.equals("123456")) {
                mLoginViewModel.login("o6up41OdFOFpxDVY_quVEOO7xMWI");
            } else {
                ToastUtils.showShort("账号或密码不正确，请重新尝试！");
            }

        });

        List<CarouselBean> carouselBeans = new ArrayList<>();
        carouselBeans.add(new CarouselBean(R.drawable.startone, ""));
        carouselBeans.add(new CarouselBean(R.drawable.starttwo, ""));
        carouselBeans.add(new CarouselBean(R.drawable.startthree, ""));
        carouselBeans.add(new CarouselBean(R.drawable.startfour, ""));


        mBinding.banner.setBannerStyle(BannerConfig.CENTER);
        mBinding.banner.setImageLoader(new com.youth.banner.loader.ImageLoader() {

            @Override
            public ImageView createImageView(Context context) {
                ImageView imageView = super.createImageView(context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
                int i = SizeUtils.dp2px(20);
                int i1 = SizeUtils.dp2px(60);
                imageView.setPadding(i, i, i, i1);
                ViewCompat.setElevation(imageView, 10);
                return imageView;
            }

            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                if (path instanceof CarouselBean) {
//                    ImageLoader.loadRoundImage1(imageView, path);
                    CarouselBean data = (CarouselBean) path;
                    ImageLoader.loadImage(imageView, data.getPic());
                }
            }
        });

        mBinding.banner.setImages(carouselBeans)
                .setBannerAnimation(Transformer.Default)
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .isAutoPlay(true)
                .start();

        mLoginViewModel = new LoginViewModel();

        mLoginViewModel.mLoginBeanDataReduceLiveData.observe(this, loginBean -> {
            dismissLoading();
            if (loginBean != null) {
                if (loginBean.getErr().getCode() == 0) {
                    AccountHelper.login(loginBean.getUserId(),
                            loginBean.getHeadImg(),
                            loginBean.getNickName(),
                            loginBean.getSign());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    ToastUtils.showShort(loginBean.getErr().getMsg());
                }

            } else {

            }
        });

        mBinding.rvLogin.setOnClickListener(v -> {
            IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
            if (!api.isWXAppInstalled()) {
                ToastUtils.showShort("您的手机中未安装微信！");
                return;
            }
            showLoading("");
            // 通过WXAPIFactory工厂，获取IWXAPI的实例

            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "none";
            api.sendReq(req);

//            mLoginViewModel.login("o6up41OdFOFpxDVY_quVEOO7xMWI");

//            showLoading("");
////            mLoginViewModel.login(user_openId);
////            mLoginViewModel.login("o6up41JkS5sijT2ui_MTyW_puLu4");
//            mLoginViewModel.login("o6up41KXImvYFuEXZFAsZ_eAjyng");

//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
        });
    }

    private String user_openId, accessToken, refreshToken, scope, unionid;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        user_openId = intent.getStringExtra("openId");
        accessToken = intent.getStringExtra("accessToken");
        refreshToken = intent.getStringExtra("refreshToken");
        scope = intent.getStringExtra("scope");
        unionid = intent.getStringExtra("unionid");
        if (!StringUtils.isEmpty(user_openId)) {
            showLoading("");
            mLoginViewModel.login(unionid);
//            mLoginViewModel.login("o6up41JkS5sijT2ui_MTyW_puLu4");
//            mLoginViewModel.login("o6up41KXImvYFuEXZFAsZ_eAjyng");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.banner.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
