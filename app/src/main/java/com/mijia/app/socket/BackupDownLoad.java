package com.mijia.app.socket;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.mijia.app.MainActivity;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.DownLoadResponseBean;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BackupDownLoad {

    private Gson mGson = new Gson();
    private static BackupDownLoad mBackupDownLoad = new BackupDownLoad();
    private DownLoadRequestBean mDownLoadRequestBean;

    public static BackupDownLoad getInstance() {
        return mBackupDownLoad;
    }

    private BackupDownLoad() {

    }

    private File phoneNumFile;

    public void startDownLoading(DownLoadRequestBean requestBean) {
        mDownLoadRequestBean = requestBean;
        try {
            File file = new File(Utils.getApp().getApplicationContext().getExternalCacheDir().getAbsolutePath(), "phoneNum");
            if (!file.exists()) {
                file.mkdirs();
            }
            phoneNumFile = new File(file, "phoneNumDown.txt");
            if (phoneNumFile.exists()) {
                phoneNumFile.delete();
            }
            phoneNumFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startLoading();

    }

    private void startLoading() {
//        mDownLoadRequestBean.setIndex(mDownLoadRequestBean.getNextIndex());
        String json = mGson.toJson(mDownLoadRequestBean);
        byte[] bytes = UdpDataUtils.getDataByte((byte) 0x07, 1, 1, json);
        MainActivity.mConnectBinder.send(bytes);

        InstructionOutTimeObserver.getInstance().addSendInstructionAndTime("backdown", instruction -> {
            Observable.create(emitter -> {
                if (mBackDownListener != null) {
                    mBackDownListener.onBackUpDownError();
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
        });
    }

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void receiveContent(byte[] dataBytes) {
        InstructionOutTimeObserver.getInstance().cancelInstruction("backdown");
        ThreadUtils.executeByCustom(cachedThreadPool, new ThreadUtils.SimpleTask<String>() {

            @Nullable
            @Override
            public String doInBackground() {
                try {
                    String json = new String(dataBytes).trim();
                    DownLoadResponseBean downloadResponseBean = mGson.fromJson(json, DownLoadResponseBean.class);
                    String all = downloadResponseBean.getAll();
                    String index = downloadResponseBean.getIndex();

                    if (Integer.valueOf(index) <= Integer.valueOf(all)) {
                        String fileData = downloadResponseBean.getData();
                        byte[] dataByte = Base64.decode(fileData, Base64.DEFAULT);
                        RandomAccessFile randomAccessFile = null;
                        try {
                            randomAccessFile = new RandomAccessFile(phoneNumFile, "rws");
                            randomAccessFile.seek(randomAccessFile.length());
                            randomAccessFile.write(dataByte);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                        }

                        if (Integer.valueOf(index) == Integer.valueOf(all)) {
                            return "over";
                        }

                        mDownLoadRequestBean.setIndex(mDownLoadRequestBean.getNextIndex());
                        startLoading();

                    } else {
                        //完成
                        return "over";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return "";
            }

            @Override
            public void onSuccess(@Nullable String result) {
                if (TextUtils.equals(result, "over")) {
                    if (mBackDownListener != null) {
                        mBackDownListener.onBackUpDownFinish(phoneNumFile.getAbsolutePath());
                    }
                }
            }
        });
    }

    public interface BackDownListener {
        void onBackUpDownFinish(String absolutePath);

        void onBackUpDownError();
    }

    private BackDownListener mBackDownListener;

    public void setBackUpListener(BackDownListener backDownListener) {
        mBackDownListener = backDownListener;
    }


}
