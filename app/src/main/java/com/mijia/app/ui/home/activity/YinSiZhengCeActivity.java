package com.mijia.app.ui.home.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;

import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.databinding.ActivityYinSiZhengCeBinding;

public class YinSiZhengCeActivity extends BaseActivity<ActivityYinSiZhengCeBinding, BaseViewModel> {


    @Override
    public int getLayoutRes() {
        return R.layout.activity_yin_si_zheng_ce;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBinding.ivReturn.setOnClickListener(view -> {
            finish();
        });

        String exchange1 = getResources().getString(R.string.privacy_policy_new);
        mBinding.tvContent.setText(Html.fromHtml(exchange1));
    }
}
