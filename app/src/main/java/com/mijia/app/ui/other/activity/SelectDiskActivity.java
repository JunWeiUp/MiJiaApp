package com.mijia.app.ui.other.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.DiskInfoBean;
import com.mijia.app.bean.GsInfoBean;
import com.mijia.app.bean.GsInfoBean_;
import com.mijia.app.databinding.ActivitySelectDiskBinding;
import com.mijia.app.ui.other.adapter.DiskAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

public class SelectDiskActivity extends BaseActivity<ActivitySelectDiskBinding, BaseViewModel> {

    private Box<GsInfoBean> mGsInfoBeanBox;
    private DiskAdapter mDiskAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_disk;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mGsInfoBeanBox = MyApp.getInstance().getBoxStore().boxFor(GsInfoBean.class);
        initRecycler();
        List<DiskInfoBean> diskList;
        diskList = Objects.requireNonNull(mGsInfoBeanBox.query().equal(GsInfoBean_.gsId, Objects.requireNonNull(SelectUploadLocationActivity.midunId.get())).build().findUnique()).diskList;
        mDiskAdapter.replaceData(diskList);
        mBinding.ivReturn.setOnClickListener(v -> {
            finish();
        });
        mBinding.tvMidunDiskPath.setText(
                SelectUploadLocationActivity.midun.get() + " > "
                        + SelectUploadLocationActivity.disk.get()
        );
    }

    private void initRecycler() {
        mDiskAdapter = new DiskAdapter(R.layout.item_disk_list);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(mDiskAdapter);
//        mDiskAdapter.replaceData(Arrays.asList(SelectUploadLocationActivity.midun.get()
//                        + "-disk1", SelectUploadLocationActivity.midun.get() + "-disk2",
//                SelectUploadLocationActivity.midun.get() + "-disk3", SelectUploadLocationActivity.midun.get() + "-disk4"));
        mDiskAdapter.setOnItemClickListener((adapter, view, position) -> {
            SelectUploadLocationActivity.disk.set(mDiskAdapter.getItem(position).getDiskName());
            SelectUploadLocationActivity.diskId.set(mDiskAdapter.getItem(position).getDiskId());
            finish();
        });
    }
}
