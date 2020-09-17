package com.mijia.app.socket;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.blankj.utilcode.util.ThreadUtils;
import com.mijia.app.MyApp;
import com.mijia.app.bean.UpRowTaskBean;
import com.mijia.app.bean.UpRowTaskBean_;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.utils.CacheManager;
import com.mijia.app.utils.file.MediaBean;
import com.mijia.app.utils.file.MediaBean_;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UpLoadTask {
    Observable observable = Observable.interval(5, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io());
    Disposable mTimeOutDisposable;

    private UploadRequestBean mUploadRequestBean;

    /**
     * 0:第一次发送了请求 ，但是没有收到回应
     * 1：发送之后，接收到响应 （传输中）
     * 2：传输完成
     * 3：长时间 未接收到响应（上传失败）
     * 4: 等待上传
     */
    protected int currTaskStatus = 0;

    protected RandomAccessFile mRandomAccessFile;
    // 文件大小
    protected long mFileSize;
    private long mTotal;

    private String mFileName = "";
    private long mLastReadPacketTime = 0;
    private int currRequestFrequency = 0;
    private boolean isPause = false;

    private Box<UpRowTaskBean> mBoxRowTask;

    public long getFileSize() {
        return mFileSize;
    }

    private UpRowTaskBean upRowTaskBean;

    int PACKSIZE = 1024 * 6;

    private long currReadEndPosition = 0;

    /**
     * 获取文件的片段
     *
     * @param requstIndex 1,2,3,4,5...
     */
    private long mLastIndex = 0;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    private boolean isSelected = false;

    private String mSrcPath = "";

    public UpLoadTask(UploadRequestBean uploadRequestBean, String srcPath) {
        mUploadRequestBean = uploadRequestBean;
        mSrcPath = srcPath;
        mBoxRowTask = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);

        try {
            File file = new File(srcPath);
            mFileName = file.getName();

            mRandomAccessFile = new RandomAccessFile(file, "rws");
            mFileSize = mRandomAccessFile.length();
            mTotal = mFileSize < PACKSIZE ? 1 : mFileSize % PACKSIZE == 0 ? mFileSize / PACKSIZE : mFileSize / PACKSIZE + 1;

            String fullPath = uploadRequestBean.getFullpath();
            List<UpRowTaskBean> queryList = mBoxRowTask.query().equal(UpRowTaskBean_.fullPath, fullPath).build().find();
            if (queryList.isEmpty()) {
                UpRowTaskBean rowTaskBean = new UpRowTaskBean();
                rowTaskBean.setFullPath(fullPath);
                rowTaskBean.setLocationPath(srcPath);
                rowTaskBean.setFileSize(mFileSize);
                rowTaskBean.setCurrTaskStatus(0);
                rowTaskBean.setFilePointer(0);
                rowTaskBean.setFileAllNumber(mTotal + "");
                rowTaskBean.setFinishTime(0);
                rowTaskBean.setFilePositionNumber(1 + "");
                mBoxRowTask.put(rowTaskBean);
                List<UpRowTaskBean> list = mBoxRowTask.query().equal(UpRowTaskBean_.fullPath, fullPath).build().find();
                upRowTaskBean = list.get(0);
            } else {
                upRowTaskBean = queryList.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currTaskStatus == 0 || currTaskStatus == 1) {
            startTimeOutTiming();
        }
    }

    public String getSrcPath() {
        return mSrcPath == null ? "" : mSrcPath;
    }

    public UploadRequestBean getUploadRequestBean() {
        return mUploadRequestBean;
    }


    long secondStartPointer = 0;
    long secondDownTimeMillis = 0;

    private String mUpSpeed = "0";

    public String getFileForPosition(int requstIndex) {
        byte[] bytes = new byte[PACKSIZE];
        String content = "";
        try {
            long index = Integer.valueOf(requstIndex) - 1;
            if (index < mTotal) {
                dbTask();
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
                content = Base64.encodeToString(bytes, Base64.NO_WRAP);
                this.currReadEndPosition = startPosition;
                currTaskStatus = 1;
                mLastIndex = requstIndex;
                mFilePointer = startPosition;

                long currTime = System.currentTimeMillis() / 1000;
                if (currTime - secondDownTimeMillis > 1) {
                    long difPointer = mFilePointer - secondStartPointer;
                    if (difPointer < 0) {
                        difPointer = 0;
                    }
                    mUpSpeed = CacheManager.getSmallFormatSize(difPointer);
                    secondStartPointer = mFilePointer;
                    secondDownTimeMillis = currTime;
                }

//                System.out.println("-----------  " + mLastIndex + "   " + mUpSpeed);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    private long mFilePointer = 0;
    private ExecutorService threadPool = Executors.newCachedThreadPool();


    public long getFilePointer() {
        return mFilePointer;
    }

    private void dbTask() {
        ThreadUtils.executeByCustom(threadPool, dbTask);
    }

    ThreadUtils.SimpleTask dbTask = new ThreadUtils.SimpleTask<String>() {
        @Nullable
        @Override
        public String doInBackground() {
            try {
                upRowTaskBean.setCurrTaskStatus(currTaskStatus);
                upRowTaskBean.setFilePointer(mFilePointer);
                upRowTaskBean.setFilePositionNumber(mLastIndex + "");
                mBoxRowTask.put(upRowTaskBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public void onSuccess(@Nullable String result) {

        }
    };


    public int getCurrTaskStatus() {
        return currTaskStatus;
    }

    public void setCurrTaskStatus(int currTaskStatus) {
        this.currTaskStatus = currTaskStatus;
        if (2 == currTaskStatus) {
            finishTime = System.currentTimeMillis();
            mDisposables.clear();
            mFilePointer = mFileSize;
            dbTask();
        }
    }

    // 上传完成的时间
    private long finishTime = 0;


    public void onLastReceivePacketTime() {
        currRequestFrequency = 0;
        mLastReadPacketTime = System.currentTimeMillis();
    }


    public long getLastIndex() {
        return mLastIndex;
    }

    public void setLastIndex(long lastIndex) {
        mLastIndex = lastIndex;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public String getFileName() {
        return mFileName == null ? "" : mFileName;
    }

    public long getCurrReadEndPosition() {
        return currReadEndPosition;
    }

    public long getTotal() {
        return mTotal;
    }

    //超时 计时
    private void startTimeOutTiming() {
        mTimeOutDisposable = observable.subscribe((Consumer<Long>) l -> {
            if (!isPause && (currTaskStatus == 0 || currTaskStatus == 1)) {
                long currTime = System.currentTimeMillis();
                if (currTime - mLastReadPacketTime > 1000) {
                    if (!TextUtils.equals("0", mUpSpeed)) {
                        mUpSpeed = "0";
                        if (mTimeOutTimingListener != null) {
                            mTimeOutTimingListener.refreshTaskList();
                        }
                    }
                }
//                long diff = currTime - mLastReadPacketTime;
//                System.out.println("-----  " + Thread.currentThread().getName() + "   " + diff + "  " + currTime + "  " + mLastReadPacketTime);
                //todo 未收到回复超过1秒
                if (currTime - mLastReadPacketTime > 1000) {
                    mUpSpeed = "0";
                    if (mTimeOutTimingListener != null) {
                        mTimeOutTimingListener.outTiming(UpLoadTask.this);
                        if (currRequestFrequency > 25) {
                            mDisposables.clear();
                            currTaskStatus = 3;
                            mTimeOutTimingListener.refreshTaskList();
                            currRequestFrequency = 0;
                            dbTask();

                            currRequestFrequency = 0;
                        }
                        currRequestFrequency++;
//                        System.out.println("---- number: " + currRequestFrequency);
                    }
                }
            } else {
                mDisposables.clear();
            }
        });
        mDisposables.add(mTimeOutDisposable);
    }


    public void retryTask() {
        isPause = false;
        currTaskStatus = 0;
        currRequestFrequency = 0;
        startTimeOutTiming();
        if (mTimeOutTimingListener != null) {
            mTimeOutTimingListener.refreshTaskList();
            mTimeOutTimingListener.outTiming(UpLoadTask.this);
        }
    }


    public interface TimeOutTimingListener {
        void outTiming(UpLoadTask task);

        void refreshTaskList();
    }


    private TimeOutTimingListener mTimeOutTimingListener;

    // 超时 调用 - 重新发送  或者 暂停重新开始后 发送
    public void setTimeOutTimingListener(TimeOutTimingListener l) {
        mTimeOutTimingListener = l;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        if (currTaskStatus == 0 || currTaskStatus == 1) {
            isPause = pause;
            if (pause) {
                mDisposables.clear();
            } else {
                currTaskStatus = 0;
                currRequestFrequency = 0;
                startTimeOutTiming();
                secondDownTimeMillis = 0;
                try {
                    secondStartPointer = mRandomAccessFile.getFilePointer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mTimeOutTimingListener != null) {
                    mTimeOutTimingListener.outTiming(UpLoadTask.this);
                }

            }
        }
    }

    public String getUpSpeed() {
        return mUpSpeed == null ? "0K" : mUpSpeed;
    }


    public void del() {
        mBoxRowTask.remove(upRowTaskBean);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPACKSIZE() {
        return PACKSIZE;
    }


}
