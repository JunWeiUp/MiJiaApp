package com.mijia.app.ui.other.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.handongkeji.utils.DateUtil;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.PhotoArrayListBean;
import com.mijia.app.databinding.ActivitySelectPhotoArrayBinding;
import com.mijia.app.ui.other.adapter.PhotoArrayListAdapter;
import com.mijia.app.utils.file.MediaBean;

import java.lang.reflect.Array;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;

public class SelectPhotoArrayActivity extends BaseActivity<ActivitySelectPhotoArrayBinding, BaseViewModel> {

    private PhotoArrayListAdapter mAdapter;

    private Box<PhotoArrayListBean> mPhotoArrayListBeanBox;
    private Box<MediaBean> mediaBeanBox;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_photo_array;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mPhotoArrayListBeanBox = MyApp.getInstance().getBoxStore().boxFor(PhotoArrayListBean.class);
        mediaBeanBox = MyApp.getInstance().getBoxStore().boxFor(MediaBean.class);
        initRecycler();
        mBinding.ivReturn.setOnClickListener(v -> finish());
    }

    private void initRecycler() {
        mAdapter = new PhotoArrayListAdapter(R.layout.item_select_photo_array);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            startActivityForResult(new Intent(SelectPhotoArrayActivity.this, SelectPhotoActivity.class)
                    .putExtra("Date", mAdapter.getData().get(position).getDate()), 0x93);
        });
        getAllPhotoInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x93 && resultCode == 0x91) {
            finish();
        }


    }

    public void getAllPhotoInfo() {
        showLoading("");
        new Thread(() -> {
            List<MediaBean> mediaBeen = new ArrayList<>();
            HashMap<String, List<MediaBean>> allPhotosTemp = new HashMap<>();//所有照片
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri mThumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            String[] projImage = {MediaStore.Images.Media._ID
                    , MediaStore.Images.Media.DATE_ADDED
                    , MediaStore.Images.Media.DATA
                    , MediaStore.Images.Media.SIZE
                    , MediaStore.Images.Media.DISPLAY_NAME
                    , MediaStore.Images.Media.HEIGHT
                    , MediaStore.Images.Media.WIDTH};
            final Cursor mCursor = getContentResolver().query(mImageUri,
                    projImage,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    int height = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                    int width = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                    long date = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000;
                    //用于展示相册初始化界面

//                    if (path.contains("/storage/emulated/0/messageBoard/photoImgs")) {
//                    mediaBeen.add(new MediaBean(path, size, displayName
//                            ,MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver()
//                            ,mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                            , MediaStore.Images.Thumbnails.MICRO_KIND, null)));
                    mediaBeen.add(new MediaBean(path, size, displayName
                            , mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID)), height, width, date));
//                    }

//                        // 获取该图片的父路径名
//                        String dirPath = new File(path).getParentFile().getAbsolutePath();
//
//                        //存储对应关系
//                        if (allPhotosTemp.containsKey(dirPath)) {
//                            List<MediaBean> data = allPhotosTemp.get(dirPath);
//                            data.add(new MediaBean(path,size,displayName));
////                            Log.e(TAG,"getAllPhotoInfo  "+data.size()+",path="+data.get(0).getPath()+",name="+data.get(0).getDisplayName());
//                            continue;
//                        } else {
//                            List<MediaBean> data = new ArrayList<>();
//                            data.add(new MediaBean(path,size,displayName));
//                            allPhotosTemp.put(dirPath,data);
////                            Log.e(TAG,"getAllPhotoInfo  else "+data.size()+",path="+data.get(0).getPath()+",name="+data.get(0).getDisplayName());
//                        }
                }
                mCursor.close();
            }

//            Log.i("FileList", "图片数量：" + mediaBeen.size());
            runOnUiThread(() -> {

                Map<String, List<MediaBean>> mediaBeanHashMap = new HashMap<>();
                for (MediaBean mediaBean : mediaBeen) {
                    String date = DateUtil.getTimeMonth(mediaBean.getDate());
                    List<MediaBean> list = mediaBeanHashMap.get(date);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(mediaBean);
                    mediaBeanHashMap.put(date, list);
                }

                ArrayList<String> keyList = new ArrayList<>();
                for (String s : mediaBeanHashMap.keySet()) {
                    keyList.add(s);
                }
                sortData(keyList);
                List<PhotoArrayListBean> realseList = new ArrayList<>();
                for (String s : keyList) {
                    PhotoArrayListBean bean = new PhotoArrayListBean();
                    bean.setDate(s);
                    bean.setMediaBean(mediaBeanHashMap.get(s));
                    for (MediaBean mediaBean : mediaBeanHashMap.get(s)) {
                        bean.mMediaBeans.add(mediaBean);
                    }

                    realseList.add(bean);
                }
                mPhotoArrayListBeanBox.removeAll();
                mediaBeanBox.removeAll();
                mPhotoArrayListBeanBox.put(realseList);
                if (realseList.size() == 0) {
                    mBinding.viewNoData.setVisibility(View.VISIBLE);
                } else {

                    mAdapter.replaceData(realseList);
                }
                runOnUiThread(() -> dismissLoading());
            });

        }).start();
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    private void sortData(ArrayList<String> mList) {
        Collections.sort(mList, new Comparator<String>() {
            /**
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(String lhs, String rhs) {
                Date date1 = stringToDate(lhs);
                Date date2 = stringToDate(rhs);
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }

}
