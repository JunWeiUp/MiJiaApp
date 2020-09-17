package com.mijia.app.socket;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.SparseArray;
import android.widget.ImageView;

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

public class PreviewFileDownLoad {
    SparseArray mSparseArray;
    private Gson mGson = new Gson();
    private DownLoadRequestBean mDownLoadRequestBean;
    private File phoneNumFile;

    private static PreviewFileDownLoad mPreviewFileDownLoad = new PreviewFileDownLoad();

    private PreviewFileDownLoad() {
    }

    public static void loadImage(ImageView imageView, String path) {
        imageView.setTag(path);


    }


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

        byte[] cmd = new byte[11];
        cmd[0] = 0x04;
        String cmdString = new String(cmd);

        String json = mGson.toJson(mDownLoadRequestBean);
        String request = cmdString + json;
        MainActivity.mConnectBinder.send(request);
    }


    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public void receiveContent(byte[] dataBytes) {
        ThreadUtils.executeByCustom(cachedThreadPool, new ThreadUtils.SimpleTask<String>() {
            @Nullable
            @Override
            public String doInBackground() {
                try {
                    byte[] headByte = new byte[4];
                    byte[] content = new byte[dataBytes.length - 4];

                    System.arraycopy(dataBytes, 0, headByte, 0, headByte.length);
                    System.arraycopy(dataBytes, 4, content, 0, content.length);
                    String json = new String(content).trim();
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
