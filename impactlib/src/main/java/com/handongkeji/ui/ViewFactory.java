package com.handongkeji.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.handongkeji.impactlib.R;


/**
 * ImageView创建工厂
 */
public class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @param url
     * @return
     */

//    public static final int[] ids = new int[]{R.mipmap.banner1, R.mipmap.banner2};

    private static int index = 0;

    public static ImageView getImageView(Context context, String url) {

        ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
                R.layout.item_view_banner, null);
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
        return imageView;
    }


}
