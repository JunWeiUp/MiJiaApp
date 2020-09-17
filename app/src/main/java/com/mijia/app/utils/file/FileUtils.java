package com.mijia.app.utils.file;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.mijia.app.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2019/6/12.
 */

public class FileUtils {

    private Context mContext;

    public FileUtils(Context context) {
        this.mContext = context;
    }

    public void getAllPhotoInfo() {
        new Thread(() -> {
            List<MediaBean> mediaBeen = new ArrayList<>();
            HashMap<String, List<MediaBean>> allPhotosTemp = new HashMap<>();//所有照片
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri mThumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            String[] projImage = {MediaStore.Images.Media._ID
                    , MediaStore.Images.Media.DATA
                    , MediaStore.Images.Media.SIZE
                    , MediaStore.Images.Media.DISPLAY_NAME};
            final Cursor mCursor = mContext.getContentResolver().query(mImageUri,
                    projImage,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

            if (mCursor != null) {
                int index = 0;
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    index++;
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    int height = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                    int width = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                    //用于展示相册初始化界面
                    mediaBeen.add(new MediaBean(path, size, displayName
                            ,mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID),height,width,0));
                }
                mCursor.close();
            }

            Log.i("FileList", "图片数量：" + mediaBeen.size());
        }).start();
    }


    /**
     * 获取本地所有的视频
     *
     * @return list
     */
    public static List<VideoBean> getAllLocalVideos(Context context, int uid) {
        long totalUploadCount = 0;
        String[] projection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        //全部视频
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=?";
        String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                "video/mkv", "video/mov", "video/mpg"};
        List<VideoBean> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                if (size < 600 * 1024 * 1024) {//<600M
                    VideoBean materialBean = new VideoBean();
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                    materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                    materialBean.setLogo(path);
                    materialBean.setFilePath(path);
                    materialBean.setChecked(false);
                    materialBean.setFileType(2);
                    materialBean.setFileId(totalUploadCount++);
                    materialBean.setUploadedSize(0);
                    materialBean.setTimeStamps(System.currentTimeMillis() + "");
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                    String t = format.format(duration);
                    materialBean.setTime("视频长度" + t);
                    materialBean.setFileSize(size);
                    list.add(materialBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }


    private void getAllFile(){

    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        if (fileS==0) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


}
