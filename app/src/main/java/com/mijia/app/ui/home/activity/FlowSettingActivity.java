package com.mijia.app.ui.home.activity;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.ActivityFlowSettingBinding;

public class FlowSettingActivity extends BaseActivity<ActivityFlowSettingBinding, BaseViewModel> {


    private SharedPreferences mSharedPreferences;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_flow_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        mSharedPreferences = getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE);
        changeIvStatus();
        mBinding.ivReturn.setOnClickListener(v -> finish());
        mBinding.ivAgreeBackupContacts.setOnClickListener(v -> {
            mSharedPreferences.edit().putBoolean(Sys.IS_AGREE_BACKUP_CONTACTS, !mBinding.ivAgreeBackupContacts.isSelected()).apply();
            changeIvStatus();
        });
        mBinding.ivAgreeDownContacts.setOnClickListener(v -> {
            mSharedPreferences.edit().putBoolean(Sys.IS_AGREE_DOWN_CONTACTS, !mBinding.ivAgreeDownContacts.isSelected()).apply();
            changeIvStatus();
        });
        mBinding.ivAgreeDownFile.setOnClickListener(v -> {
            mSharedPreferences.edit().putBoolean(Sys.IS_AGREE_DOWN_FILE, !mBinding.ivAgreeDownFile.isSelected()).apply();
            changeIvStatus();
        });
        mBinding.ivAgreeUploadFile.setOnClickListener(v -> {
            mSharedPreferences.edit().putBoolean(Sys.IS_AGREE_UPLOAD_FILE, !mBinding.ivAgreeUploadFile.isSelected()).apply();
            changeIvStatus();
        });

    }

    private void changeIvStatus() {
        mBinding.ivAgreeBackupContacts.setSelected(mSharedPreferences.getBoolean(Sys.IS_AGREE_BACKUP_CONTACTS, false));
        mBinding.ivAgreeDownContacts.setSelected(mSharedPreferences.getBoolean(Sys.IS_AGREE_DOWN_CONTACTS, false));
        mBinding.ivAgreeDownFile.setSelected(mSharedPreferences.getBoolean(Sys.IS_AGREE_DOWN_FILE, false));
        mBinding.ivAgreeUploadFile.setSelected(mSharedPreferences.getBoolean(Sys.IS_AGREE_UPLOAD_FILE, false));
    }
}
