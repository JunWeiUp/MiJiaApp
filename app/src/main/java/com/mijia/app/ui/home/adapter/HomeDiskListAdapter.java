package com.mijia.app.ui.home.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.DiskInfoBean;
import com.mijia.app.bean.GsInfoBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ItemHomeDiskBinding;
import com.mijia.app.utils.ViewSizeChangeAnimation;
import com.mijia.app.widget.CircleProgressView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2019/6/4.
 */

public class HomeDiskListAdapter extends BaseQuickAdapter<DiskInfoBean, BaseViewHolder> {

    public int getSelectIndex() {
        return selectIndex;
    }

    private int selectIndex = 0;
    private Context mContext;
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    @BindingAdapter(value = {"appWidth"})
    public static void setWidth(View view, int width) {
        width = SizeUtils.dp2px(width);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter(value = {"appHeight"})
    public static void setHeight(View view, int height) {
        height = SizeUtils.dp2px(height);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }


    public HomeDiskListAdapter(int layoutResId, Context context) {
        super(layoutResId);
        this.mContext = context;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskInfoBean item) {

        ItemHomeDiskBinding binding = DataBindingUtil.bind(helper.itemView);

//        CardView mainView = helper.getView(R.id.card_main);
        CardView mainView = binding.cardMain;
        TextView tvStatus = helper.getView(R.id.tv_open_status);
        TextView tvDiskName = helper.getView(R.id.tv_disk_name);
        TextView tvAllDanWei = helper.getView(R.id.tv_all_danwei);
        TextView tvCanuseDanWei = helper.getView(R.id.tv_canuser_danwei);
        tvDiskName.setText(item.getDiskName());
        ImageView ivMore = helper.getView(R.id.iv_more);

        int progress = (int) ((Float.parseFloat(item.getDiskInuse()) / Float.parseFloat(item.getDiskSize())) * 100);
        CircleProgressView progressView = helper.getView(R.id.progress);
        progressView.setShowTick(false);
        progressView.showAnimation(0, progress, 500);

        tvStatus.setText("online".equals(item.getDiskStatu()) ? "连接" : "断开");
//        progressView.setProgress(80);


        TextView tvAllsize = helper.getView(R.id.tv_all_rongliang);

        double size = Double.parseDouble(item.getDiskSize());

        if (size >= 1024) {
            tvAllsize.setText(String.valueOf(getTwoDouble(size / 1024)));
            tvAllDanWei.setText("G");
        } else {
            tvAllsize.setText(String.valueOf(getTwoDouble(size)));
            tvAllDanWei.setText("M");
        }


        TextView tvUseSize = helper.getView(R.id.tv_have_rongliang);
        double canUse = Double.parseDouble(item.getDiskSize()) - Double.parseDouble(item.getDiskInuse());
        if (canUse >= 1024) {
            tvUseSize.setText(String.valueOf(getTwoDouble(canUse / 1024)));
            tvCanuseDanWei.setText("G");
        } else {
            tvUseSize.setText(String.valueOf(getTwoDouble(canUse)));
            tvCanuseDanWei.setText("M");
        }


        TextView tvISMove = helper.getView(R.id.tv_is_move);
        tvISMove.setText("local".equals(item.getDiskType()) ? "否" : "是");


        if (selectIndex == helper.getAdapterPosition()) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mainView.getLayoutParams();
            if (defaultWidth == 0) {
                defaultWidth = layoutParams.width;
            }
            if (defaultHeight == 0) {
                defaultHeight = layoutParams.height;
            }

            Animation animation = new ViewSizeChangeAnimation(mainView, (int) (defaultHeight * 1.31f), (int) (defaultWidth * 1.35f));
            animation.setDuration(300);
            mainView.startAnimation(animation);
            mainView.setSelected(true);
            binding.setSelected(true);

            Constants.SYS_CONTANTS_BEAN.SelectedDisk.set(item.getDiskName());
            Constants.SYS_CONTANTS_BEAN.SelectedDiskId.set(item.getDiskId());
            Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.set("online".equals(item.getDiskStatu()));
        } else {

            Animation animation = new ViewSizeChangeAnimation(mainView, defaultHeight, defaultWidth);
            animation.setDuration(300);
            mainView.startAnimation(animation);
            mainView.setSelected(false);
            binding.setSelected(false);
        }


        helper.addOnClickListener(R.id.iv_more);
        helper.addOnClickListener(R.id.card_main);
    }

    private double getTwoDouble(double dou) {
        BigDecimal b = new BigDecimal(dou);
        dou = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return dou;
    }

    /**
     * 判断double是否是整数
     *
     * @param obj
     * @return
     */
    public static boolean isIntegerForDouble(double obj) {
        double eps = 1e-10;  // 精度范围
        return obj - Math.floor(obj) < eps;
    }
}
