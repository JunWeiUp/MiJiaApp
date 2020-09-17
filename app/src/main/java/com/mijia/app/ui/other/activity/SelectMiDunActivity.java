package com.mijia.app.ui.other.activity;

import android.content.Intent;
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
import com.mijia.app.bean.GsInfoBean;
import com.mijia.app.bean.GsInfoBean_;
import com.mijia.app.databinding.ActivitySelectMiDunBinding;
import com.mijia.app.ui.other.adapter.MiDunAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

public class SelectMiDunActivity extends BaseActivity<ActivitySelectMiDunBinding, BaseViewModel> {

    private MiDunAdapter mDunAdapter;
    private Box<GsInfoBean> mGsInfoBeanBox;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_mi_dun;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mGsInfoBeanBox = MyApp.getInstance().getBoxStore().boxFor(GsInfoBean.class);
        initRecycler();
        List<GsInfoBean> list;
        list = mGsInfoBeanBox.query().equal(GsInfoBean_.gsId, Objects.requireNonNull(SelectUploadLocationActivity.midunId.get())).build().find();
        mDunAdapter.replaceData(list);
        mBinding.ivReturn.setOnClickListener(v -> {
            finish();
        });
    }

    private void initRecycler() {
        mDunAdapter = new MiDunAdapter(R.layout.item_midun_list);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(mDunAdapter);
        mDunAdapter.setOnItemClickListener((adapter, view, position) -> {
            SelectUploadLocationActivity.midun.set(mDunAdapter.getItem(position).getGsName());
            SelectUploadLocationActivity.midunId.set(mDunAdapter.getItem(position).getGsId());
            startActivity(new Intent(SelectMiDunActivity.this, SelectDiskActivity.class));
            finish();
        });
    }
}
