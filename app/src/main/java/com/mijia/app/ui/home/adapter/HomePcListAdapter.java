package com.mijia.app.ui.home.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.PointerIcon;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.GsInfoBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.constants.Constants;

/**
 * Created by Administrator on 2019/6/4.
 */

public class HomePcListAdapter extends BaseQuickAdapter<GsInfoBean, BaseViewHolder> {

    private int selectIndex = 0;

    public HomePcListAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void convert(BaseViewHolder helper, GsInfoBean item) {

        helper.getView(R.id.rv_main).setSelected(helper.getAdapterPosition() == selectIndex);
        helper.getView(R.id.tv_pc_online_status).setSelected(helper.getAdapterPosition() == selectIndex);
        helper.getView(R.id.iv_more).setSelected(helper.getAdapterPosition() == selectIndex);
        helper.getView(R.id.tv_pc_title).setSelected(helper.getAdapterPosition() == selectIndex);
        helper.getView(R.id.iv_online_status).setSelected(item.getGsStatu().equals("online"));
        TextView tvOnLineStatus = helper.getView(R.id.tv_pc_online_status);
        tvOnLineStatus.setText("online".equals(item.getGsStatu()) ? "在线" : "离线");

        TextView tvName = helper.getView(R.id.tv_pc_title);
        tvName.setText(item.getGsName());

        if (selectIndex == helper.getAdapterPosition()) {
            Constants.SYS_CONTANTS_BEAN.SelectedMiDun.set(item.getGsName());
            Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.set(item.getGsId());
        }

        helper.addOnClickListener(R.id.iv_more);
        helper.addOnClickListener(R.id.rv_main);

    }

}
