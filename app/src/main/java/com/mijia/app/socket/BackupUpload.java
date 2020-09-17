package com.mijia.app.socket;

import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.util.Base64;

import com.google.gson.Gson;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.MainActivity;
import com.mijia.app.bean.UpLoadBackUpRequestBean;
import com.mijia.app.bean.UploadBackUpResponseBean;
import com.mijia.app.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BackupUpload {

    private static BackupUpload instance = new BackupUpload();
    private Gson mGson = new Gson();

    public static BackupUpload getInstance() {
        return instance;
    }

    private int PACKSIZE = 1024;
    private long mTotal;
    private long mFileSize;
    private RandomAccessFile mRandomAccessFile;

    public BackupUpload upLoadBackUpPath(String path) {
        File file = new File(path);
        try {
            mRandomAccessFile = new RandomAccessFile(file, "rws");
            mFileSize = mRandomAccessFile.length();
            // 总个数
            mTotal = mFileSize < PACKSIZE ? 1 : mFileSize % PACKSIZE == 0 ? mFileSize / PACKSIZE : mFileSize / PACKSIZE + 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    public void startBackUp(int requstIndex) {
        String content = getFileForPosition(requstIndex);

        UpLoadBackUpRequestBean upLoadBackUpRequestBean = new UpLoadBackUpRequestBean();

        upLoadBackUpRequestBean.setUserName(AccountHelper.getNickname());
        upLoadBackUpRequestBean.setUserId(AccountHelper.getUserId());
        upLoadBackUpRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
        upLoadBackUpRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());

        upLoadBackUpRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
        upLoadBackUpRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

        upLoadBackUpRequestBean.setAll(mTotal + "");
        upLoadBackUpRequestBean.setIndex(requstIndex + "");

        String datalen = "0";
        if (mEndPosition == mFileSize) {
            datalen = (mEndPosition - mStartPosition) + "";
        } else {
            datalen = PACKSIZE + "";
        }
        upLoadBackUpRequestBean.setDatalen(datalen);

        upLoadBackUpRequestBean.setData(content);


        String json = mGson.toJson(upLoadBackUpRequestBean);


        byte[] bytes = UdpDataUtils.getDataByte((byte) 0x06, 1, 1, json);
        MainActivity.mConnectBinder.send(bytes);

        InstructionOutTimeObserver.getInstance().addSendInstructionAndTime("backup", instruction -> {
            Observable.create(emitter -> {
                if (mBackUpListener != null) {
                    mBackUpListener.onUpLoadingError();
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
        });

    }

    private long mEndPosition = 0, mStartPosition = 0;

    public String getFileForPosition(int requstIndex) {
        byte[] bytes = new byte[PACKSIZE];
        String content = "";
        try {
            long index = Integer.valueOf(requstIndex) - 1;
            long startPosition = index * PACKSIZE;  //
            long endPosition = mFileSize - startPosition <= PACKSIZE ? mFileSize : startPosition + PACKSIZE;
            mRandomAccessFile.seek(startPosition);
            if (endPosition == mFileSize) {
                int endByteLength = (int) (endPosition - startPosition);
                bytes = new byte[endByteLength];
                mRandomAccessFile.read(bytes, 0, endByteLength);
            } else {
                mRandomAccessFile.read(bytes, 0, PACKSIZE);
            }
            mEndPosition = endPosition;
            mStartPosition = startPosition;

            content = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    public void receiveContent(byte[] dataBytes) {
        String json = new String(dataBytes).trim();
        UploadBackUpResponseBean backUpResponseBean = mGson.fromJson(json, UploadBackUpResponseBean.class);

        int index = Integer.valueOf(backUpResponseBean.getIndex());
        InstructionOutTimeObserver.getInstance().cancelInstruction("backup");
        if (index < mTotal) {
            startBackUp(index + 1);
        } else {
            try {
                mRandomAccessFile.close();
                // 结束
                Observable.create(emitter -> {
                    if (mBackUpListener != null) {
                        mBackUpListener.onUpLoadingFinish();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface BackUpListener {
        void onUpLoadingFinish();

        void onUpLoadingError();
    }

    private BackUpListener mBackUpListener;

    public void setBackUpListener(BackUpListener backUpListener) {
        mBackUpListener = backUpListener;
    }


}
