package com.mijia.app.ui.other.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.GsInfoBean;

/**
 * Created by Administrator on 2019/6/11.
 */

public class MiDunAdapter extends BaseQuickAdapter<GsInfoBean, BaseViewHolder> {


    public MiDunAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, GsInfoBean item) {
        TextView tvName = helper.getView(R.id.tv_midun_name);
        tvName.setText(item.getGsName());
    }
}
