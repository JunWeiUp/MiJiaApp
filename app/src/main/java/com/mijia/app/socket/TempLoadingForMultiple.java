package com.mijia.app.socket;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.Gson;
import com.hwangjr.rxbus.RxBus;
import com.mijia.app.MainActivity;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.DownLoadResponseBean;
import com.mijia.app.constants.RxBusAction;
import com.nevermore.oceans.uits.ImageLoader;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TempLoadingForMultiple implements DownLoadWrite.WriteFileListener, DownTask.TaskErrorListener {
    private Gson mGson = new Gson();

    private static TempLoadingForMultiple mLoadingForMultiple = new TempLoadingForMultiple();
    private static ConcurrentMap<String, DownTask> mTaskList;
    private DownLoadWrite mDownLoadWrite;
    private static ConcurrentMap<String, ImageView> mViewList = new ConcurrentHashMap();

    private TempLoadingForMultiple() {
        mTaskList = new ConcurrentHashMap();
        mDownLoadWrite = new DownLoadWrite(mTaskList);
        mDownLoadWrite.setWriteFileListener(this);
    }

    public static void loadImage(ImageView imageView) {
        mLoadingForMultiple.mTypes = LoadTypes.images;
        DownTask task = (DownTask) imageView.getTag();
        task.setTimeOutTimingListener(mLoadingForMultiple);
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String fullPath = downLoadRequestBean.getFullpath();
        fullPath = fullPath + downLoadRequestBean.getType();

        if (!mTaskList.keySet().contains(fullPath)) {
            mTaskList.put(fullPath, task);
            mViewList.put(fullPath, imageView);
            mLoadingForMultiple.sendRequest(downLoadRequestBean);
        } else {
            task.clear();
            task = null;
            System.gc();
        }
    }

    public static void loadFile(DownTask task) {
        mLoadingForMultiple.mTypes = LoadTypes.file;
        mLoadingForMultiple.stopAll();
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String fullPath = downLoadRequestBean.getFullpath();
        fullPath = fullPath + downLoadRequestBean.getType();
        mTaskList.put(fullPath, task);
        mLoadingForMultiple.sendRequest(downLoadRequestBean);
    }

    @Override
    public void outTiming(DownLoadRequestBean value) {
        mLoadingForMultiple.sendRequest(value);
    }

    @Override
    public void refreshTaskList() {

    }

    @Override
    public void checkWaitTask() {

    }

    enum LoadTypes {
        audio, images, file, pic, video
    }

    LoadTypes mTypes = LoadTypes.images;


    // 加载音视频文件
    public static void loadAudioFile(DownTask task) {
        mLoadingForMultiple.mTypes = LoadTypes.audio;
        mLoadingForMultiple.stopAll();
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String fullPath = downLoadRequestBean.getFullpath();
        fullPath = fullPath + downLoadRequestBean.getType();
        mTaskList.put(fullPath, task);
        mLoadingForMultiple.sendRequest(downLoadRequestBean);
    }

    // 加载音视频文件
    public static void loadVideoFile(DownTask task) {
        mLoadingForMultiple.mTypes = LoadTypes.video;
        mLoadingForMultiple.stopAll();
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String fullPath = downLoadRequestBean.getFullpath();
        fullPath = fullPath + downLoadRequestBean.getType();
        mTaskList.put(fullPath, task);
        mLoadingForMultiple.sendRequest(downLoadRequestBean);
    }

    // 加载音视频文件
    public static void loadPicFile(DownTask task) {
        mLoadingForMultiple.mTypes = LoadTypes.pic;
        mLoadingForMultiple.stopAll();
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String fullPath = downLoadRequestBean.getFullpath();
        fullPath = fullPath + downLoadRequestBean.getType();
        mTaskList.put(fullPath, task);
        mLoadingForMultiple.sendRequest(downLoadRequestBean);
    }


    public static void downImageContent(DownLoadResponseBean downloadResponseBean) {
        mLoadingForMultiple.taskImageRequest(downloadResponseBean);
    }

    private void sendRequest(DownLoadRequestBean downLoadRequestBean) {
        String json = mGson.toJson(downLoadRequestBean);
        byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
        MainActivity.mConnectBinder.send(bytes);
    }

    public static boolean constanceKey(String key) {
        return mTaskList.keySet().contains(key);
    }

    private Observable mObservable = Observable.timer(200, TimeUnit.SECONDS).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());


    public void taskImageRequest(DownLoadResponseBean downloadResponseBean) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Nullable
            @Override
            public String doInBackground() {
                String key = downloadResponseBean.getFullpath();
                key = key + downloadResponseBean.getType();
                DownTask downTask = mTaskList.get(key);
                if (downTask != null) {

                    String all = downloadResponseBean.getAll();
                    String index = downloadResponseBean.getIndex();
                    // 请求下一个 文件包
                    DownLoadRequestBean downloadTaskBean = downTask.getDownLoadResponseBean();
                    int nextIndex = downloadTaskBean.getNextIntIndex();

                    if (Integer.valueOf(index) + 1 >= nextIndex) {
                        mDownLoadWrite.addData(downloadResponseBean);
                        if (!mDownLoadWrite.isRunningTask()) {
                            mDownLoadWrite.dealWithByte();
                        }

                        downloadTaskBean.setIndex((Integer.valueOf(index) + 1) + "");
                        int intAll = Integer.valueOf(all);
                        System.out.println("-----  " + all + "    " + index + "   " + downloadTaskBean.getIndex());
                        if (Integer.valueOf(index) < intAll && Integer.valueOf(downloadTaskBean.getIndex()) <= intAll) {
                            sendRequest(downloadTaskBean);
                        }
                        // 音频 接收到 数据后 ，进行播放
                        if (Integer.valueOf(index) == 1) {
                            mObservable.subscribe((Consumer<Long>) aLong -> {
                                // 通知进行播放

                            });
                        }
                    }


                }
                return "";
            }

            @Override
            public void onSuccess(@Nullable String result) {
            }
        }, 1);
    }

    @Override
    public void onPreviewLocationPath(String key, String path) {
        if (mTypes == LoadTypes.images) {
            ImageView imageView = mViewList.get(key);
            if (imageView != null) {
                DownTask task = (DownTask) imageView.getTag();
                DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
                String fullPath = downLoadRequestBean.getFullpath();
                fullPath = fullPath + downLoadRequestBean.getType();
                if (TextUtils.equals(key, fullPath)) {
                    Observable.create(emitter -> {
                        imageView.setTag(null);
                        ImageLoader.loadImage(imageView, path);
                        imageView.setTag(task);
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                }
            }
        }
        if (mTypes == LoadTypes.audio) {
            RxBus.get().post(RxBusAction.RADIO_PREVIEW_FINISH, path);
        }

        if (mTypes == LoadTypes.video) {
            RxBus.get().post(RxBusAction.VIDEO_PREVIEW_FINISH, path);
        }

        if (mTypes == LoadTypes.pic) {
            RxBus.get().post(RxBusAction.PIC_PREVIEW_FINISH, path);
        }

        if (mTypes == LoadTypes.file) {
            RxBus.get().post(RxBusAction.FILE_PREVIEW_FINISH, path);
        }

    }

    @Override
    public void onRefreshUI() {

    }

    @Override
    public void onFinishedTaskChange() {

    }

    public static void stopAllTask() {
        mLoadingForMultiple.stopAll();
    }

    private void stopAll() {
        // 删除不完成的文件
        mDownLoadWrite.stopTask();
        for (String key : mTaskList.keySet()) {
            DownTask downTask = mTaskList.get(key);
            String locationFilePath = downTask.getLocationFilePath();
            File locationFile = new File(locationFilePath);
            if (locationFile.exists()) {
                if (locationFile.length() < downTask.getFileSize() / 10 * 9) {
                    locationFile.delete();
                }
            }
        }
        mTaskList.clear();
        mViewList.clear();
    }

}
