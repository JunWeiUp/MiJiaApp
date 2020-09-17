package com.mijia.app.ui.home.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.databinding.ActivityAboutUsActivitsyBinding;

public class AboutUsActivitsy extends BaseActivity<ActivityAboutUsActivitsyBinding, BaseViewModel> {


    private String title ;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_about_us_activitsy;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
          title = getIntent().getStringExtra("title");
          mBinding.tvContent.setText(title);

          mBinding.ivReturn.setOnClickListener(view -> finish());
    }
}
