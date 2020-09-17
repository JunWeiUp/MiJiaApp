package com.handongkeji.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.imagepicker.loader.ImageLoader;

/**
 * Created by Administrator on 2017/8/17 0017.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height, OnImageResultListener listener) {
        displayImage(activity,path,imageView,width,height);
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(width, height);
        Glide.with(activity)
                .load(path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                ;
        Glide.with(context)
                .load(path)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
