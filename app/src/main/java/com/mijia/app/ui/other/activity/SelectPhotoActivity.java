package com.mijia.app.ui.other.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.handong.framework.adapter.LazyFPagerAdapter;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.ActivitySelectPhotoBinding;
import com.mijia.app.databinding.SelectFileUpTabLayoutBinding;
import com.mijia.app.ui.other.fragment.SelectPhotoFragment;
import com.mijia.app.ui.other.viewmodel.SelectPhotoViewModel;
import com.mijia.app.utils.file.MediaBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mijia.app.constants.Constants.DISK_NOW_PAGE_PATH;

public class SelectPhotoActivity extends BaseActivity<ActivitySelectPhotoBinding, SelectPhotoViewModel> {

    private List<Fragment> fragments = new ArrayList<>();
    private SelectFileUpTabLayoutBinding mTabLayoutBinding;
    private String date;

    public static final ObservableInt noUploadNum = new ObservableInt(0);
    public static final ObservableInt allNum = new ObservableInt(0);

    public static final ObservableBoolean isNoUpdataAllSelect = new ObservableBoolean(false);
    public static final ObservableBoolean isAllAllSelect = new ObservableBoolean(false);

    private SelectPhotoFragment mNoUploadPhotoFragment;
    private SelectPhotoFragment mAllPhotoFragment;
    private int selectFragmentTag = 0;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_photo;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        date = getIntent().getStringExtra("Date");
        mNoUploadPhotoFragment = SelectPhotoFragment.instants(date, 0);
        mAllPhotoFragment = SelectPhotoFragment.instants(date, 1);
        fragments.add(mNoUploadPhotoFragment);
        fragments.add(mAllPhotoFragment);
        LazyFPagerAdapter adapter = new LazyFPagerAdapter(getSupportFragmentManager(), fragments);
        mBinding.viewPager.setAdapter(adapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        initTabLayout();

        mBinding.ivReturn.setOnClickListener(v -> {
            finish();
        });

        mBinding.tvChoiceFloder.setOnClickListener(v -> {
            startActivityForResult(new Intent(SelectPhotoActivity.this, SelectUploadLocationActivity.class)
                    .putExtra(SelectUploadLocationActivity.SelectLocationType, SelectUploadLocationActivity.Upload), 0x23);

        });

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectFragmentTag = position;
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
                mNoUploadPhotoFragment.allSelect();
            } else {
                if (isAllAllSelect.get()) {
                    isAllAllSelect.set(false);
                } else {
                    isAllAllSelect.set(true);
                }
                mAllPhotoFragment.allSelect();
            }
        });

        mViewModel.setDestPath(DISK_NOW_PAGE_PATH + "/");
        mBinding.destPathText.setText(DISK_NOW_PAGE_PATH + "/");

        mBinding.rvSure.setOnClickListener(view -> {
//            if (0 == selectFragmentTag) {


            if (TextUtils.isEmpty(mViewModel.path)) {
                ToastUtils.showShort("未选择上传路径");
                return;
            }

            boolean isAgreeBackupContacts = getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_UPLOAD_FILE, false);

            if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                ToastUtils.showShort("请在设置中开启流量上传文件！");
                return;
            }

            List<MediaBean> selectedList ;
            // 未上传
            if (0 == selectFragmentTag) {
                selectedList = mNoUploadPhotoFragment.getSelectedImageData();
            }else{
                selectedList = mAllPhotoFragment.getSelectedImageData();
            }
            if (selectedList.size() == 0) {
                return;
            }
            String fileNames = mViewModel.upLoadImageForList(selectedList);

            if (!TextUtils.isEmpty(fileNames)) {
                ToastUtils.showShort(fileNames + " 文件(s)上传过");
                return;
            }
            ToastUtils.showShort("添加任务成功");
            setResult(0x91);
            finish();
//            }
//
//            if (1 == selectFragmentTag) {
//                // 已上传
//            }
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
            tab.setCustomView(mTabLayoutBinding.getRoot());
            if (0 == i) {
                tab.select();
            }
        }
        mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0x23 && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(Sys.SELECT_PATH);
            mViewModel.setDestPath(path);
            mBinding.destPathText.setText(path);

        }


    }
}
