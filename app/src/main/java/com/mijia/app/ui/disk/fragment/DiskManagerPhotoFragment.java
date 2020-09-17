package com.mijia.app.ui.disk.fragment;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.handongkeji.utils.DateUtil;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.mijia.app.R;
import com.mijia.app.bean.FileBean;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.FragmentDiskManagerPhotoBinding;
import com.mijia.app.socket.TempLoadingForMultiple;
import com.mijia.app.sys.FileBoxUtils;
import com.mijia.app.ui.disk.adapter.DiskPhotoArrayListAdapter;
import com.mijia.app.ui.disk.adapter.PhotoArrayMultipleItem;
import com.mijia.app.ui.other.adapter.PhotoAdapter;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 磁盘管理 子页面 图片页面
 *
 * @ClassName:DiskManagerPhotoFragment
 * @PackageName:com.mijia.app.ui.disk.fragment
 * @Create On 2019/6/10   10:56
 * @Site:http://www.handongkeji.com
 * @author:闫大仙er
 * @Copyrights 2019/6/10 handongkeji All rights reserved.
 */


public class DiskManagerPhotoFragment extends BaseFragment<FragmentDiskManagerPhotoBinding, BaseViewModel> {


    private DiskPhotoArrayListAdapter mDiskPhotoArrayListAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_disk_manager_photo;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);
        binding.setIsMiWenOpen(DiskManagerFragment.isMiWenOpen);
        binding.setIsOpenSelect(DiskManagerFragment.isOpenSelect);
        binding.setHandler(this);
        binding.noDataView.findViewById(R.id.tv_upload_file).setOnClickListener(v -> {
            RxBus.get().post(RxBusAction.MAIN_OPEN_UPLOAD_DIALOG, Sys.PHOTO + "");
//            startActivity(new Intent(getActivity(), SelectPhotoArrayActivity.class));
        });

        TextView tvRefresh = binding.noDataView.findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(v -> {
            binding.smartview.autoRefresh();
        });

        DiskManagerFragment.isMiWenOpen.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                for (DiskPhotoArrayListAdapter.PhotoAdapter photoAdapter : mDiskPhotoArrayListAdapter.getPhotoAdapterList()) {
                    photoAdapter.mingmichange();
                }
            }
        });

        DiskManagerFragment.isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!DiskManagerFragment.isOpenSelect.get()) {
                    for (DiskPhotoArrayListAdapter.PhotoAdapter photoAdapter : mDiskPhotoArrayListAdapter.getPhotoAdapterList()) {
                        photoAdapter.clearAllSelect();
                    }
                }
            }
        });

//        List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("/");
//        Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
//        mFileListAdapter.replaceData(fileBeanList);

        binding.smartview.setOnRefreshListener(refreshlayout -> {
            List<FileBean> fileBeanList = FileBoxUtils.getFileByType(0);

            Map<String, List<FileBean>> mediaBeanHashMap = new HashMap<>();
            for (FileBean mediaBean : fileBeanList) {
//            String time = stringToDate(mediaBean.getTime());
                String date = DateUtil.getTimeMonth(mediaBean.getTime() * 1000);
                List<FileBean> list = mediaBeanHashMap.get(date);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(mediaBean);
                mediaBeanHashMap.put(date, list);
            }

            ArrayList<String> keyList = new ArrayList<>();
            for (String s : mediaBeanHashMap.keySet()) {
                keyList.add(s);
            }
            sortData(keyList);
            List<FileBean> realseList = new ArrayList<>();
            List<PhotoArrayMultipleItem> photoArrayMultipleItems = new ArrayList<>();
            for (String s : keyList) {
                PhotoArrayMultipleItem multipleItem = new PhotoArrayMultipleItem(PhotoArrayMultipleItem.TITLE, null);
                multipleItem.setTitle(s);
                photoArrayMultipleItems.add(multipleItem);
                PhotoArrayMultipleItem multipleItem1 = new PhotoArrayMultipleItem(PhotoArrayMultipleItem.PHOTO, mediaBeanHashMap.get(s));
                photoArrayMultipleItems.add(multipleItem1);
            }

            if (photoArrayMultipleItems.size() == 0) {
                binding.noDataView.setVisibility(View.VISIBLE);
                mDiskPhotoArrayListAdapter.setNewData(null);
            } else {
                binding.noDataView.setVisibility(View.GONE);
                mDiskPhotoArrayListAdapter.setNewData(photoArrayMultipleItems);
            }
            binding.smartview.finishRefresh();
        });

        initPhotoArrayAdapter();
        binding.smartview.autoRefresh();

    }


    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    private void sortData(ArrayList<String> mList) {
        Collections.sort(mList, new Comparator<String>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(String lhs, String rhs) {
                Date date1 = stringToDate(lhs);
                Date date2 = stringToDate(rhs);
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }

    @Subscribe(tags = {@Tag(RxBusAction.DISK_MANAGER_RETURN)}, thread = EventThread.MAIN_THREAD)
    public void returnPage(String str) {
//            Objects.requireNonNull(getActivity()).onBackPressed();
        RxBus.get().post(RxBusAction.DISK_CHANGE_OR_DATA_REFRESH, "");
        TempLoadingForMultiple.stopAllTask();
    }

    private void initPhotoArrayAdapter() {
        mDiskPhotoArrayListAdapter = new DiskPhotoArrayListAdapter(getActivity(), null);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(mDiskPhotoArrayListAdapter);
        mDiskPhotoArrayListAdapter.setOnItemClickListener((DiskPhotoArrayListAdapter.OnItemClickListener) (adapter, view, position) -> {
            if (DiskManagerFragment.isMiWenOpen.get()) {
                if (adapter.getData().get(position).isSelected()) {
                    adapter.getData().get(position).setSelected(false);
                } else {
                    if (adapter.getSelectedNum() == 0) {
                        adapter.getData().get(position).setSelected(true);
                    } else {
                        ToastUtils.showShort("密文下载只支持单选！");
                    }
                }
            } else {
                adapter.getData().get(position).setSelected(!adapter.getData().get(position).isSelected());
            }
            adapter.refresh();
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                returnPage("");
                break;
            case R.id.tv_miwen:
                DiskManagerFragment.isMiWenOpen.set(!DiskManagerFragment.isMiWenOpen.get());
                if (!DiskManagerFragment.isOpenSelect.get()) {
                    DiskManagerFragment.isOpenSelect.set(true);
                }
                if (!DiskManagerFragment.isMiWenOpen.get()) {
                    binding.tvMiwen.setText("明文下载");
                } else {
                    binding.tvMiwen.setText("密文下载");
                }
                break;
            case R.id.tv_cancel:
                for (DiskPhotoArrayListAdapter.PhotoAdapter photoAdapter : mDiskPhotoArrayListAdapter.getPhotoAdapterList()) {
                    photoAdapter.clearAllSelect();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (DiskPhotoArrayListAdapter.PhotoAdapter photoAdapter : mDiskPhotoArrayListAdapter.getPhotoAdapterList()) {
            photoAdapter.clearAllSelect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        TempLoadingForMultiple.stopAllTask();
    }
}
