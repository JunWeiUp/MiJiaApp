package com.mijia.app.ui.other.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.handongkeji.utils.DateUtil;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.PhotoArrayListBean;
import com.mijia.app.bean.PhotoArrayListBean_;
import com.mijia.app.bean.UpRowTaskBean;
import com.mijia.app.databinding.FragmentSelectPhotoBinding;
import com.mijia.app.ui.other.activity.SelectPhotoActivity;
import com.mijia.app.ui.other.adapter.PhotoAdapter;
import com.mijia.app.utils.file.FileUtils;
import com.mijia.app.utils.file.MediaBean;
import com.mijia.app.utils.file.MediaBean_;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import io.objectbox.query.QueryFilter;
import io.objectbox.relation.ToMany;

/**
 * Created by Administrator on 2019/6/11.
 */

public class SelectPhotoFragment extends BaseFragment<FragmentSelectPhotoBinding, BaseViewModel> {

    private PhotoAdapter mPhotoAdapter;

    private Box<PhotoArrayListBean> mPhotoArrayListBeanBox;
    private Box<MediaBean> mMediaBeanBox;
    private String date;
    private int type;//0 未上传 1全部

    public static SelectPhotoFragment instants(String date, int type) {
        SelectPhotoFragment selectPhotoFragment = new SelectPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putInt("type", type);
        selectPhotoFragment.setArguments(bundle);
        return selectPhotoFragment;
    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_select_photo;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mMediaBeanBox = MyApp.getInstance().getBoxStore().boxFor(MediaBean.class);
        mPhotoArrayListBeanBox = MyApp.getInstance().getBoxStore().boxFor(PhotoArrayListBean.class);

        type = getArguments().getInt("type");
        date = getArguments().getString("date");
        PhotoArrayListBean allImageListBean = mPhotoArrayListBeanBox.query().equal(PhotoArrayListBean_.date, date).build().findFirst();
//        List<MediaBean> list= mMediaBeanBox.query().equal(MediaBean_.mediaId,bean.getId()).build().find();
        mPhotoAdapter = new PhotoAdapter(R.layout.item_select_photo);
        binding.recycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        binding.recycler.setAdapter(mPhotoAdapter);

        List<MediaBean> mNoUpLoadNum = new ArrayList<>();
        ToMany<MediaBean> toMany = allImageListBean.getMediaBeans();

        for (MediaBean me : toMany) {
            System.out.println("----  " + me.getPath() + "   " + me.getUpLoadingStatus() + "   " + me.getMediaId());
            if (me.getUpLoadingStatus() == -1) {
                mNoUpLoadNum.add(me);
            }
        }

        Box<UpRowTaskBean> upRowTaskBeanBox = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);
        List<UpRowTaskBean> upRowTaskBeans = upRowTaskBeanBox.getAll();



        List<MediaBean> realMediaBeanList = new ArrayList<>();
        for (MediaBean mediaBean : mNoUpLoadNum) {
            String mediaPath = mediaBean.getPath();
            boolean ishave = false;
            for (UpRowTaskBean upRowTaskBean : upRowTaskBeans) {
                if (upRowTaskBean.getLocationPath().equals(mediaPath)) {
                    ishave = true;
                }
            }
            if (!ishave) {
                realMediaBeanList.add(mediaBean);
            }
        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (type == 0) {
//                    SelectPhotoActivity.noUploadNum.set(mNoUpLoadNum.size());
                    SelectPhotoActivity.noUploadNum.set(realMediaBeanList.size());
                } else {
                    SelectPhotoActivity.allNum.set(toMany.size());
                }
            }
        }, 300);

        if (type == 0) {
            mPhotoAdapter.replaceData(realMediaBeanList);
        }else{
            mPhotoAdapter.replaceData(mNoUpLoadNum);
        }

//        mPhotoAdapter.replaceData(mNoUpLoadNum);
//        getAllPhotoInfo();
        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(MyApp.getInstance()).resumeRequests();
                } else {
                    Glide.with(MyApp.getInstance()).pauseRequests();
                }
            }
        });

        mPhotoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_select:
                    mPhotoAdapter.getData().get(position).setSelect(!mPhotoAdapter.getData().get(position).isSelect());
                    mPhotoAdapter.notifyDataSetChanged();
                    for (MediaBean datum : mPhotoAdapter.getData()) {
                        if (!datum.isSelect()) {
                            if (type == 0) {
                                SelectPhotoActivity.isNoUpdataAllSelect.set(false);
                            } else {
                                SelectPhotoActivity.isAllAllSelect.set(false);
                            }
                            return;
                        }
                    }
                    if (type == 0) {
                        SelectPhotoActivity.isNoUpdataAllSelect.set(true);
                    } else {
                        SelectPhotoActivity.isAllAllSelect.set(true);
                    }
                    break;
                case R.id.iv_image:
                    ArrayList<String> list = new ArrayList<>();
                    list.add(mPhotoAdapter.getData().get(position).getPath());
                    startActivity(new Intent(getActivity(), ImageBroseActivity.class)
                            .putExtra(ImageBroseActivity.PICS, list));
                    break;
                default:
                    break;
            }
        });

    }

    public void allSelect() {
        for (MediaBean datum : mPhotoAdapter.getData()) {
            datum.setSelect(type == 0 ? SelectPhotoActivity.isNoUpdataAllSelect.get() : SelectPhotoActivity.isAllAllSelect.get());
        }
        mPhotoAdapter.notifyDataSetChanged();
    }


    public void getAllPhotoInfo() {
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
            final Cursor mCursor = getActivity().getContentResolver().query(mImageUri,
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
                    long date = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000;
                    //用于展示相册初始化界面

//                    if (path.contains("/storage/emulated/0/messageBoard/photoImgs")) {
//                    mediaBeen.add(new MediaBean(path, size, displayName
//                            ,MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver()
//                            ,mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                            , MediaStore.Images.Thumbnails.MICRO_KIND, null)));
                    mediaBeen.add(new MediaBean(path, size, displayName
                            , mCursor.getColumnIndex(MediaStore.Images.Media._ID), height, width, date));
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

            Log.i("FileList", "图片数量：" + mediaBeen.size());
            getActivity().runOnUiThread(() -> {
                HashMap<String, MediaBean> mediaBeanHashMap = new HashMap<>();
                for (MediaBean mediaBean : mediaBeen) {
                    String date = DateUtil.getTimeMonth(mediaBean.getDate() * 1000);
                    mediaBeanHashMap.put(date, mediaBean);
                }
                mPhotoAdapter.replaceData(mediaBeen);
            });
//                //更新界面
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //...
//                        Log.e(TAG,"mediaBeen="+mediaBeen.size());
//                        albumAdapter = new AlbumAdapter(AvaterActivity.this,mediaBeen);
//                        gvAlbum.setAdapter(albumAdapter);
//                        gvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                Intent cropIntent = new Intent(mContext, CropImgActivity.class);
//                                cropIntent.putExtra("status",status);
//                                cropIntent.putExtra("takepath",mediaBeen.get(position).getPath());
//                                startActivity(cropIntent);
//                            }
//                        });
//                    }
//                });
        }).start();
    }

    public List<MediaBean> getSelectedImageData() {
        List<MediaBean> selectedList = new ArrayList<>();

        List<MediaBean> list = mPhotoAdapter.getData();
        for (MediaBean m : list) {
            if (m.isSelect()) {
                selectedList.add(m);
            }
        }
        return selectedList;
    }

}
