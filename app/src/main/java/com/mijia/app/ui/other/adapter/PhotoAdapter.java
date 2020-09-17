package com.mijia.app.ui.other.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.utils.file.MediaBean;
import com.nevermore.oceans.uits.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2019/6/11.
 */

public class PhotoAdapter extends BaseQuickAdapter<MediaBean, BaseViewHolder> {


    public PhotoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MediaBean item) {
        // 压缩图片
//        Bitmap bm = decodeSampledBitmapFromPath(item.getPath(), item.getWidth(), item.getHeight());
        ImageView ivImage = helper.getView(R.id.iv_image);

//        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(MyApp.getInstance().getContentResolver()
//                , item.getCusorId()
//                , MediaStore.Images.Thumbnails.MICRO_KIND, null);
        Glide.with(mContext).load(item.getPath()).into(ivImage);


        ImageView ivSelect = helper.getView(R.id.iv_select);
        ivSelect.setSelected(item.isSelect());

        helper.addOnClickListener(R.id.iv_image);
        helper.addOnClickListener(R.id.iv_select);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 压缩图片
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 获取图片宽高，不加载到内存中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

//        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inSampleSize = 16;

        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;

    }


    private int caculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;
        if (w > width || h > height) {
            int widthRadio = Math.round(w * 100.0f / width);
            int heightRadio = Math.round(h * 100.0f / height);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }
}
