package com.mijia.app.ui.other.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.PhotoArrayListBean;
import com.mijia.app.utils.file.MediaBean;
import com.nevermore.oceans.uits.ImageLoader;

/**
 * Created by Administrator on 2019/6/10.
 */

public class PhotoArrayListAdapter extends BaseQuickAdapter<PhotoArrayListBean, BaseViewHolder> {


    public PhotoArrayListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoArrayListBean item) {
        TextView tvDate = helper.getView(R.id.tv_photo_array_time);
        TextView tvNum = helper.getView(R.id.tv_photo_num);
        ImageView iamgeViewOne = helper.getView(R.id.iv_one);
        ImageView iamgeViewTwo = helper.getView(R.id.iv_two);
        ImageView iamgeViewThree = helper.getView(R.id.iv_three);
        ImageView iamgeViewFour = helper.getView(R.id.iv_four);

        tvDate.setText(item.getDate());
        tvNum.setText(item.getMediaBean().size() + "张");

        ImageLoader.loadImage(iamgeViewOne, item.getMediaBean().size() > 0 ? item.getMediaBean().get(0).getPath() : "");
        ImageLoader.loadImage(iamgeViewTwo, item.getMediaBean().size() > 1 ? item.getMediaBean().get(1).getPath() : "");
        ImageLoader.loadImage(iamgeViewThree, item.getMediaBean().size() > 2 ? item.getMediaBean().get(2).getPath() : "");
        ImageLoader.loadImage(iamgeViewFour, item.getMediaBean().size() > 3 ? item.getMediaBean().get(3).getPath() : "");

    }


}
