package com.mijia.app.ui.trans.fragment;

import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.handong.framework.adapter.LazyFPagerAdapter;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.databinding.FragmentTransferListBinding;
import com.mijia.app.databinding.SelectFileUpTabLayoutBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class TransferListFragment extends BaseFragment<FragmentTransferListBinding, BaseViewModel> {

    //是否开启选择
    public static final ObservableBoolean isOpenSelect = new ObservableBoolean(false);

    //当前页面下标
    public static final ObservableInt pageIndex = new ObservableInt(0);

    //0 未选 1 下载、上传失败、正在下载上传  2 下载、上传完成
    public static final ObservableInt selectType = new ObservableInt(0);

    //选中数量
    public static final ObservableInt selectedNum = new ObservableInt(0);

    private List<Fragment> fragments = new ArrayList<>();
    private SelectFileUpTabLayoutBinding mTabLayoutBinding;

    private TransferDownloadFragment mTransferDownloadFragment;
    private TransferUploadFragment mTransferUploadFragment;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_transfer_list;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setHandler(this);
        binding.setIsOpenSelect(isOpenSelect);
        binding.setSelectedNum(selectedNum);
        binding.setSelectType(selectType);
        binding.setSys(Constants.SYS_CONTANTS_BEAN);
        RxBus.get().register(this);

        selectType.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (selectType.get() == 1 || selectType.get() == 2) {
                    MainActivity.isBottomGone.set(true);
                } else {
                    MainActivity.isBottomGone.set(false);
                }
            }
        });

        isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (!isOpenSelect.get()) {
                    MainActivity.isBottomGone.set(false);
                }
            }
        });

        mTransferDownloadFragment = new TransferDownloadFragment();
        mTransferUploadFragment = new TransferUploadFragment();

        fragments.add(mTransferDownloadFragment);
        fragments.add(mTransferUploadFragment);
        fragments.add(new TranferAuthorizationFragment());
        LazyFPagerAdapter adapter = new LazyFPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        initTabLayout();

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.ivAllSelect.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                pageIndex.set(position);
                isOpenSelect.set(false);
                selectType.set(0);
                selectedNum.set(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTabLayout() {
        String[] newTab = getResources().getStringArray(R.array.tab_trans_strings);
        List<String> tabTitles = Arrays.asList(newTab);
        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            mTabLayoutBinding = SelectFileUpTabLayoutBinding.inflate(LayoutInflater.from(getActivity()), binding.tabLayout, false);
            mTabLayoutBinding.setItemTitle(tabTitles.get(i));
            mTabLayoutBinding.tvValue.setVisibility(View.GONE);
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            tab.setCustomView(mTabLayoutBinding.getRoot());
            if (0 == i) {
                tab.select();
            }
        }
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_all_select:
                isOpenSelect.set(!isOpenSelect.get());
                break;
            case R.id.tv_cancel:
                if (isOpenSelect.get()) {
                    isOpenSelect.set(false);
                    selectType.set(0);
                }
                break;
            case R.id.delText:
            case R.id.delOnlyText:
                if (0 == TransferListFragment.pageIndex.get()) {
                    if (mTransferDownloadFragment != null) {
                        mTransferDownloadFragment.delSelectedList();
                    }
                }

                if (1 == TransferListFragment.pageIndex.get()) {
                    if (mTransferUploadFragment != null) {
                        mTransferUploadFragment.delSelectedList();
                    }
                }
                break;
            case R.id.shareText:
                if (0 == TransferListFragment.pageIndex.get()) {
                    if (mTransferDownloadFragment != null) {
                        mTransferDownloadFragment.shareFile();
                    }
                }

                if (1 == TransferListFragment.pageIndex.get()) {
                    if (mTransferUploadFragment != null) {
                        mTransferUploadFragment.shareFile();
                    }
                }
                break;
        }
    }

    @Subscribe(tags = {@Tag(RxBusAction.MAIN_PAGE_CURRENT)}, thread = EventThread.MAIN_THREAD)
    public void changeToDownLoadPage(String str) {
        binding.viewPager.setCurrentItem(0);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
