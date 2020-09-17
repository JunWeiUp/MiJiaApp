package com.mijia.app.ui.other.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.VideoVoiceFileBean;
import com.mijia.app.utils.DurationUtils;
import com.nevermore.oceans.uits.ImageLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_FILE;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VIDEO;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VOICE;

/**
 * Created by Administrator on 2019/6/12.
 */

public class VoiceVideoFileAdapter extends BaseQuickAdapter<VideoVoiceFileBean, BaseViewHolder> {

    private int uploadType;

    public VoiceVideoFileAdapter(int layoutResId, int uploadType) {
        super(layoutResId);
        this.uploadType = uploadType;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoVoiceFileBean item) {
        ImageView ivFilePic = helper.getView(R.id.iv_file_pic);
        RelativeLayout rvVideoPic = helper.getView(R.id.rv_video_pic);
        TextView tvFileName = helper.getView(R.id.tv_file_name);
        TextView tvDuration = helper.getView(R.id.tv_video_time);
        ImageView ivVideoPic = helper.getView(R.id.iv_video_pic);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        ivSelect.setSelected(item.isSelect());
        helper.addOnClickListener(R.id.iv_select);
        helper.addOnClickListener(R.id.ll_item_view);
        switch (uploadType) {
            case UPLOAD_VIDEO:
                rvVideoPic.setVisibility(View.VISIBLE);
                ivFilePic.setVisibility(View.GONE);
                tvDuration.setText(DurationUtils.getDuration(item.getDuration()));
                break;
            case UPLOAD_FILE:
//                ivFilePic.setImageResource(R.drawable.cipan_fenlei_doc);
                rvVideoPic.setVisibility(View.GONE);
                ivFilePic.setVisibility(View.VISIBLE);

                if (item.getDisplayName().contains(".")) {
                    String strLater = item.getDisplayName().split("\\.").length > 1 ? item.getDisplayName().split("\\.")[1] : "";
                    if (StringUtils.isEmpty(strLater)) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_wenjian);
                    } else if (equals(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_doc);
                    } else if (equals(strLater, Arrays.asList("execl"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_excel);
                    } else if (equals(strLater, Arrays.asList("pdf"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_pdf);
                    } else if (equals(strLater, Arrays.asList("ppt"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_ppt);
                    } else if (equals(strLater, Arrays.asList("psd"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_ps);
                    } else if (equals(strLater, Arrays.asList("txt"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_txt);
                    } else if (equals(strLater, Arrays.asList("word"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_w);
                    } else if (equals(strLater, Arrays.asList("xls", "xlsx"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_xls);
                    } else if (equals(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                        ivFilePic.setImageResource(R.drawable.cipan_fenlei_yinpin);
                    }
                } else {
                    ivFilePic.setImageResource(R.drawable.cipan_fenlei_wenjian);
                }

                break;
            case UPLOAD_VOICE:
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_yinpin);
                rvVideoPic.setVisibility(View.GONE);
                ivFilePic.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        tvFileName.setText(item.getDisplayName());
//        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(MyApp.getInstance().getContentResolver()
//                , item.getCourseId()
//                , MediaStore.Video.Thumbnails.MICRO_KIND, null);
//        Glide.with(mContext).load(bitmap).into(ivVideoPic);

        Glide.with(ivVideoPic.getContext())
                .load(Uri.fromFile(new File(item.getPath())))
                .into(ivVideoPic);
//        if (!StringUtils.isEmpty(item.getThumb())) {
//            ImageLoader.loadImage(ivVideoPic,item.getThumb(),R.color.colorGray);
//        }
    }

    private boolean equals(String str, List<String> strings) {
        for (String string : strings) {
            if (str.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
