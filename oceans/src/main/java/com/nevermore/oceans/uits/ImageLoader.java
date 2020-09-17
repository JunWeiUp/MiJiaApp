package com.nevermore.oceans.uits;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class ImageLoader {

    public static void newLoadImage(ImageView imageView,String path){
        if (imageView.getContext()!=null) {
            Glide.with(imageView.getContext())
                    .load(path)
                    .into(imageView);
        }
    }

    public static void loadImage(ImageView imageView, String imagePth) {
        loadImage(imageView, imagePth, 0);
    }

    public static void loadImage(ImageView imageView, String imagePath, @DrawableRes int placeHolder) {
        loadImage(imageView, imagePath, placeHolder, placeHolder);
    }

    public static void loadImage(ImageView imageView, String imagePath, @DrawableRes int placeHolder, @DrawableRes int errRes) {
        if (CommonUtils.isStringNull(imagePath)) {
            imageView.setImageResource(errRes);
        } else {
            RequestOptions options = new RequestOptions();
            if (placeHolder > 0) {
                options.placeholder(placeHolder);
            }
            if (errRes > 0) {
                options.error(errRes);
            }
            options.diskCacheStrategy(DiskCacheStrategy.ALL);

            imagePath = fixUrl(imagePath);
            Glide.with(imageView.getContext())
                    .asBitmap()
                    .apply(options)
                    .load(imagePath)
                    .into(imageView);
        }
    }

    public static String fixUrl(String url) {

        if (!TextUtils.isEmpty(url) && url.startsWith("http://") && url.contains("\\")) {
            url = url.substring(7);
            url = url.replaceAll("\\\\", "/");
            url = "http://".concat(url);
            return url;
        }
        return url;
    }

    public static void loadImage(ImageView imageView, int resid) {
        Glide.with(imageView.getContext()).load(resid).into(imageView);
    }

    /**
     * 加再圆形图
     *
     * @param imageView
     * @param imagePath
     * @param placeholderid 占位图资源id
     */
    public static void loadRoundImage(final ImageView imageView, String imagePath, int placeholderid) {
        //imageView.setImageResource(placeholderid);

        RequestOptions options = new RequestOptions()
                .placeholder(placeholderid)
                .error(placeholderid)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        imagePath = fixUrl(imagePath);
        Glide.with(imageView.getContext())
                .asBitmap()
                .apply(options)
                .load(imagePath)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        roundedBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(roundedBitmapDrawable);
                    }
                });


    }

    public static void loadRoundImage1(final ImageView imageView, String imagePath) {
        loadRoundImage1(imageView, imagePath, 0);
    }

    /**
     * 圆角
     *
     * @param imageView
     * @param imagePath
     * @param placeholderid 占位图资源id
     */
    public static void loadRoundImage1(ImageView imageView, String imagePath, int placeholderid) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (placeholderid >= 0) {
            options.error(placeholderid)
                    .placeholder(placeholderid);
        }
        imagePath = fixUrl(imagePath);
        Glide.with(imageView.getContext())
                .asBitmap()
                .apply(options)
                .load(imagePath)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.getContext().getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(SizeUtils.dp2px(10)); //设置圆角弧度
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

//    public static void loadRundImage(Context context,String url,ImageView imageView){
//
//        Glide.with(context)
//
//                .load(url)
//                .transform(new GlideRoundTransform(context, 10)).into(imageView);
//    }

    //  加载头像
    public static void loadHeadImage(Context context, String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.morentouxiang)
                .error(R.drawable.morentouxiang)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        url = fixUrl(url);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);

    }

    public static void loadImage(Context context, String url, ImageLoadingListener loadingListener) {
        Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                if (loadingListener != null) {
                    loadingListener.onLoadingComplete(url, resource);
                }
            }
        });
    }

    /**
     * 加再圆形图
     *
     * @param imageView
     * @param imagePath
     * @param placeholderid 占位图资源id
     */
    public static void loadRoundImageTopLeftRight(final ImageView imageView, String imagePath) {
        //imageView.setImageResource(placeholderid);

        RequestOptions options = new RequestOptions()
//                .placeholder(placeholderid)
//                .error(placeholderid)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        imagePath = fixUrl(imagePath);
        Glide.with(imageView.getContext())
                .asBitmap()
                .apply(options)
                .load(imagePath)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        roundedBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(roundedBitmapDrawable);
                    }
                });


    }

    public interface ImageLoadingListener {
        void onLoadingComplete(String imageUri, Bitmap loadedImage);
    }

}
