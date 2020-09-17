package com.mijia.app.ui.home.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.bean.SettingBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivitySettingBinding;
import com.mijia.app.dialog.TipsDialog;
import com.mijia.app.ui.other.activity.LoginActivity;
import com.mijia.app.ui.other.activity.WebViewActivity;
import com.mijia.app.viewmodel.SettingViewModel;
import com.nevermore.oceans.uits.ImageLoader;

public class SettingActivity extends BaseActivity<ActivitySettingBinding, BaseViewModel> {

    private SettingViewModel mSettingViewModel;

    private String storeUrl = "";
    private String aboutUs = "";

    private String version;

    private String isChecking;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBinding.tvAppVersionName.setText("密夹 V" + AppUtils.getAppVersionName());

        SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
        version = sharedPreferences.getString("version", "");
        isChecking = sharedPreferences.getString("isChecking", "");

        if (!StringUtils.isEmpty(version) && !StringUtils.isEmpty(isChecking)) {
            if (version.equals(AppUtils.getAppVersionName())) {
                if (isChecking.equals("1")) {
                    mBinding.llZhuxiao.setVisibility(View.VISIBLE);
                    mBinding.llZhuxiao.setOnClickListener(view -> {
                        TipsDialog tipsDialog = new TipsDialog(SettingActivity.this, "您确定要注销账号么？");
                        tipsDialog.setClickListener(new TipsDialog.OnClickListener() {
                            @Override
                            public void sure() {
                                LoginActivity.LoginName = "213213";
                                quit();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        tipsDialog.show();
                    });
                }
            }
        }

        mBinding.ivReturn.setOnClickListener(v -> finish());
        mBinding.llFlowSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, FlowSettingActivity.class));
        });

        mBinding.llSpaceSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, SpaceSettingActivity.class));
        });

        mBinding.llGoodsStore.setOnClickListener(v -> {
            startActivity(new Intent(this, WebViewActivity.class)
                    .putExtra(Constants.WEBURL, storeUrl)
                    .putExtra("title", "官方商城"));
        });

        mBinding.llAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivitsy.class)
                    .putExtra(Constants.WEBURL, "https://www.pgyer.com/4mqK")
                    .putExtra("title", aboutUs));
        });


        mBinding.tvFuwuxieyi.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, FuWuXieYiActivity.class));
        });

        mBinding.tvYinsizhengce.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, YinSiZhengCeActivity.class));
        });

        mBinding.llPhone.setOnClickListener(v -> {
            TipsDialog tipsDialog = new TipsDialog(SettingActivity.this, "是否拨打电话?");
            tipsDialog.setClickListener(new TipsDialog.OnClickListener() {
                @Override
                public void sure() {
                    callPhone(mBinding.tvPhone.getText().toString());
                }

                @Override
                public void cancel() {

                }
            });
            tipsDialog.show();

        });

        mBinding.tvLogout.setOnClickListener(v -> {
            TipsDialog tipsDialog = new TipsDialog(SettingActivity.this, "是否退出登录？");
            tipsDialog.setClickListener(new TipsDialog.OnClickListener() {
                @Override
                public void sure() {
                    quit();
                }

                @Override
                public void cancel() {

                }
            });
            tipsDialog.show();
        });

        mSettingViewModel = new SettingViewModel();

        mSettingViewModel.mSettingBean.observe(this, settingBean -> {
            if (settingBean != null && settingBean.getErr().getCode() == 0) {
                mBinding.tvPhone.setText(settingBean.getPhone());
                storeUrl = settingBean.getStore();
                aboutUs = settingBean.getAboutUs();
                ImageLoader.loadImage(mBinding.ivLogo, settingBean.getLogoImg(), R.drawable.beifen_logo);
            }
        });

        mSettingViewModel.getSettingData();
    }

    private void quit() {
        Constants.isPcOnLine = false;
        Constants.isPcConnecting = false;
        AccountHelper.setUserId();
        startActivity(new Intent(SettingActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    public void diallPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 拨打电话（直接拨打电话）
     * *     * @paramphoneNum 电话号码
     */
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 123);
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                Toast.makeText(this, "请允许拨号权限后再试", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }
}
