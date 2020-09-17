package com.mijia.app.ui.other.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.DiskInfoBean;

/**
 * Created by Administrator on 2019/6/11.
 */

public class DiskAdapter extends BaseQuickAdapter<DiskInfoBean,BaseViewHolder> {

    public DiskAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskInfoBean item) {
        TextView tvDiskName = helper.getView(R.id.tv_disk_name);
        tvDiskName.setText(item.getDiskName());
    }
}
