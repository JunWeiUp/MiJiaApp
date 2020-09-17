package com.mijia.app.ui.other.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.handong.framework.adapter.LazyFPagerAdapter;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.bean.VideoVoiceFileBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.ActivitySelectVoiceVideoFileBinding;
import com.mijia.app.databinding.SelectFileUpTabLayoutBinding;
import com.mijia.app.ui.other.fragment.SelectVoiceVideoFileFragment;
import com.mijia.app.ui.other.viewmodel.SelectVoiceVideoFileViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mijia.app.constants.Constants.DISK_NOW_PAGE_PATH;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_FILE;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VIDEO;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VOICE;

public class SelectVoiceVideoFileActivity extends BaseActivity<ActivitySelectVoiceVideoFileBinding, SelectVoiceVideoFileViewModel> {

    private List<Fragment> fragments = new ArrayList<>();
    private SelectFileUpTabLayoutBinding mTabLayoutBinding;
    private int type;

    public static final ObservableInt noUploadNum = new ObservableInt(0);
    public static final ObservableInt allNum = new ObservableInt(0);

    public static final ObservableBoolean isNoUpdataAllSelect = new ObservableBoolean(false);
    public static final ObservableBoolean isAllAllSelect = new ObservableBoolean(false);

    private SelectVoiceVideoFileFragment mNoUpdataVoiceVideoFileFragment;
    private SelectVoiceVideoFileFragment mAllVoiceVideoFileFragment;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_voice_video_file;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        noUploadNum.set(0);
        allNum.set(0);
        type = getIntent().getIntExtra("type", 0);
        switch (type) {
            case UPLOAD_VIDEO:
                mBinding.tvTitle.setText("选择视频");
                break;
            case UPLOAD_FILE:
                mBinding.tvTitle.setText("选择文档");
                break;
            case UPLOAD_VOICE:
                mBinding.tvTitle.setText("选择音频");
                break;
            default:
                break;
        }
        mNoUpdataVoiceVideoFileFragment = SelectVoiceVideoFileFragment.instants(type, 0);
        mAllVoiceVideoFileFragment = SelectVoiceVideoFileFragment.instants(type, 1);
        fragments.add(mNoUpdataVoiceVideoFileFragment);
        fragments.add(mAllVoiceVideoFileFragment);
        LazyFPagerAdapter adapter = new LazyFPagerAdapter(getSupportFragmentManager(), fragments);
        mBinding.viewPager.setAdapter(adapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        initTabLayout();

        mBinding.ivReturn.setOnClickListener(v -> {
            finish();
        });

        mBinding.tvChoiceFloder.setOnClickListener(v -> {
            startActivityForResult(new Intent(SelectVoiceVideoFileActivity.this, SelectUploadLocationActivity.class)
                    .putExtra(SelectUploadLocationActivity.SelectLocationType, SelectUploadLocationActivity.Upload), 0x30);
        });


        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBinding.tvAllSelect.setText(isNoUpdataAllSelect.get() ? "取消全选" : "全选");
                } else {
                    mBinding.tvAllSelect.setText(isAllAllSelect.get() ? "取消全选" : "全选");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        isNoUpdataAllSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isNoUpdataAllSelect.get()) {
                    mBinding.tvAllSelect.setText("取消全选");
                } else {
                    mBinding.tvAllSelect.setText("全选");
                }
            }
        });

        isAllAllSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isAllAllSelect.get()) {
                    mBinding.tvAllSelect.setText("取消全选");
                } else {
                    mBinding.tvAllSelect.setText("全选");
                }
            }
        });

        mBinding.tvAllSelect.setOnClickListener(v -> {
            if (mBinding.viewPager.getCurrentItem() == 0) {
                if (isNoUpdataAllSelect.get()) {
                    isNoUpdataAllSelect.set(false);
                } else {
                    isNoUpdataAllSelect.set(true);
                }
                mNoUpdataVoiceVideoFileFragment.allSelect();
            } else {
                if (isAllAllSelect.get()) {
                    isAllAllSelect.set(false);
                } else {
                    isAllAllSelect.set(true);
                }
                mAllVoiceVideoFileFragment.allSelect();
            }
        });


        mViewModel.setDestPath(DISK_NOW_PAGE_PATH+"/");
        mBinding.selectedPathText.setText(DISK_NOW_PAGE_PATH+"/");

        mBinding.rvSure.setOnClickListener(view -> {
            List<VideoVoiceFileBean> list = mNoUpdataVoiceVideoFileFragment.getSelectedList();
            if (TextUtils.isEmpty(mViewModel.mDestPath)) {
                ToastUtils.showShort("未选择上传路径");
                return;
            }

            if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                return;
            }

            boolean isAgreeBackupContacts = getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_UPLOAD_FILE, false);

            if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                ToastUtils.showShort("请在设置中开启流量上传文件！");
                return;
            }


            String ss = mViewModel.upLoadList(list);

            finish();

            if (!TextUtils.isEmpty(ss)) {
                ToastUtils.showShort(ss + " 该目录已存在");
                return;
            }
            ToastUtils.showShort("添加任务成功");
        });
    }

    private void initTabLayout() {
        String[] newTab = getResources().getStringArray(R.array.tab_strings);
        List<String> tabTitles = Arrays.asList(newTab);

        for (int i = 0; i < mBinding.tabLayout.getTabCount(); i++) {
            mTabLayoutBinding = SelectFileUpTabLayoutBinding.inflate(LayoutInflater.from(this), mBinding.tabLayout, false);
            mTabLayoutBinding.setItemTitle(tabTitles.get(i));
            if (i == 0) {
                mTabLayoutBinding.setItemValue(noUploadNum);
            } else {
                mTabLayoutBinding.setItemValue(allNum);
            }
            TabLayout.Tab tab = mBinding.tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mTabLayoutBinding.getRoot());
            }
            if (0 == i) {
                assert tab != null;
                tab.select();
            }
        }
        mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x30 && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(Sys.SELECT_PATH);
            mViewModel.setDestPath(path);
            mBinding.selectedPathText.setText(path);
        }
    }
}
