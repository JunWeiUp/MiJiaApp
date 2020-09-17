package com.mijia.app.socket;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.MyApp;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.DownRowTaskBean;
import com.mijia.app.bean.DownRowTaskBean_;
import com.mijia.app.utils.CacheManager;

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

public class DownTask {
    private Box<DownRowTaskBean> mBox;
    // （N 秒内更新该数据 和 任务计时同步）
    // 只需要在n秒内记录下,下载的数据长度,在除以n秒,再换算成kb,就能知道在这n秒内,你的下载速度是多少kb/秒...

    private long mTaskId = System.currentTimeMillis();
    //
    private DownLoadRequestBean mDownLoadRequestBean;
    private String mFileName = "";
    private String mLocationFilePath = "";

    private long endTime = 0;

    // 速率
    private String mDownSpeed = "0";


    // 文件大小
    protected long mFileSize;
    protected RandomAccessFile mRandomAccessFile;

    /**
     * 0:未发送请求
     * 1：接收到响应 （传输中）
     * 2：传输完成
     * 3：长时间（5 分钟）未接收到响应（下载失败）
     * 4: 等待下载
     * 5: 销毁
     */
    protected int currTaskStatus = 0;

    /**
     * 下载的位置
     */
    protected long mFilePointer;

    // 当前文件的总包数
    private String fileAllNumber = "";
    // 当前文件的 第几个包  
    private String filePositionNumber = "";


    private DownRowTaskBean mQueryRowTaskBean;

    private long lastPacketReceiveTimeMillis = 0;   //最后一个包抵达的时间
    private CompositeDisposable mDisposables = new CompositeDisposable();

    private TaskErrorListener mTimeOutTimingListener;

    // 是否暂停
    private boolean mIsPause = false;

    // 是否是预览文件
    private boolean mIsPreviewFile = false;

    private String all;

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public DownTask(DownLoadRequestBean downLoadRequestBean, long fileSize, boolean isPreviewFile) {
        mDownLoadRequestBean = downLoadRequestBean;
        this.mFileSize = fileSize;
        mIsPreviewFile = isPreviewFile;
        mBox = MyApp.getInstance().getBoxStore().boxFor(DownRowTaskBean.class);


        // E:\    迅雷下载\111111.txt       E:/迅雷下载/22222222.txt
        // 为改文件创建对应的 文件夹目录 和对应的文件
        String srcfullPath = mDownLoadRequestBean.getFullpath();
//        String fullPath = srcfullPath.replaceAll("E:/", "");

        // sdcard 绝对路径
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (isPreviewFile) {
            absolutePath = Utils.getApp().getApplicationContext().getExternalCacheDir().getAbsolutePath();
        }

        String userInfo = AccountHelper.getNickname() + AccountHelper.getUserId();
        ///sdcard/95xiu
        int type = mDownLoadRequestBean.getType();

        String typeName = "明文";
        if (0 == type) {
            typeName = "明文";
        } else {
            typeName = "密文";
        }

        File absolutePathFile = new File(absolutePath, "秘夹/" + userInfo + "/" + typeName);
        if (!absolutePathFile.exists()) {
            absolutePathFile.mkdirs();
        }


        File locationFile = new File(absolutePathFile, srcfullPath);
        mFileName = locationFile.getName();

//        String srcFileName = locationFile.getName();
//        String newFileName = type + "_" + srcFileName;

        File currParentFile = locationFile.getParentFile();
//        locationFile = new File(currParentFile, newFileName);
        if (!currParentFile.exists()) {
            currParentFile.mkdirs();
        }
        try {
            if (!locationFile.exists()) {
                locationFile.createNewFile();
                if (!isPreviewFile) {
                    List<DownRowTaskBean> downRowTaskList = mBox.query().equal(DownRowTaskBean_.locationPath, locationFile.getAbsolutePath()).build().find();
                    if (downRowTaskList != null && !downRowTaskList.isEmpty()) {
                        mQueryRowTaskBean = downRowTaskList.get(0);
                        mBox.remove(mQueryRowTaskBean);
                    }
                    DownRowTaskBean taskBean = new DownRowTaskBean();
                    taskBean.setFullpath(srcfullPath);
                    taskBean.setLocationPath(locationFile.getAbsolutePath());
                    taskBean.setFileSize(fileSize);
                    taskBean.setCurrTaskStatus(0);
                    taskBean.setFilePointer(0);
                    taskBean.setFileAllNumber("1");
                    taskBean.setFilePositionNumber("1");
                    taskBean.setUserName(downLoadRequestBean.getUserName());
                    taskBean.setUserId(downLoadRequestBean.getUserId());

                    taskBean.setGsName(downLoadRequestBean.getGsName());
                    taskBean.setGsId(downLoadRequestBean.getGsId());
                    taskBean.setDiskName(downLoadRequestBean.getDiskName());
                    taskBean.setDiskId(downLoadRequestBean.getDiskId());
                    taskBean.setType(downLoadRequestBean.getType());
                    mBox.put(taskBean);
                    List<DownRowTaskBean> downRowTaskList1 = mBox.query().equal(DownRowTaskBean_.locationPath, locationFile.getAbsolutePath()).build().find();
                    if (downRowTaskList1 != null && !downRowTaskList1.isEmpty()) {
                        mQueryRowTaskBean = downRowTaskList1.get(0);
                    }
                }
            } else {
                if (!isPreviewFile) {
                    List<DownRowTaskBean> downRowTaskList = mBox.query().equal(DownRowTaskBean_.locationPath, locationFile.getAbsolutePath()).build().find();
                    if (downRowTaskList != null && !downRowTaskList.isEmpty()) {
                        mQueryRowTaskBean = downRowTaskList.get(0);
                    }
                    // 本地有文件 - 数据库没有记录 则 创建一条新的记录 --  而且是重新下载
                    if (mQueryRowTaskBean == null) {
                        locationFile.delete();
                        locationFile.createNewFile();
                        DownRowTaskBean taskBean = new DownRowTaskBean();
                        taskBean.setFullpath(srcfullPath);
                        taskBean.setLocationPath(locationFile.getAbsolutePath());
                        taskBean.setFileSize(fileSize);
                        taskBean.setCurrTaskStatus(0);
                        taskBean.setFilePointer(0);
                        taskBean.setFileAllNumber("1");
                        taskBean.setFilePositionNumber("1");
                        taskBean.setUserName(downLoadRequestBean.getUserName());
                        taskBean.setUserId(downLoadRequestBean.getUserId());

                        taskBean.setGsName(downLoadRequestBean.getGsName());
                        taskBean.setGsId(downLoadRequestBean.getGsId());
                        taskBean.setDiskName(downLoadRequestBean.getDiskName());
                        taskBean.setDiskId(downLoadRequestBean.getDiskId());
                        taskBean.setType(downLoadRequestBean.getType());
                        mBox.put(taskBean);
                        downRowTaskList = mBox.query().equal(DownRowTaskBean_.locationPath, locationFile.getAbsolutePath()).build().find();
                        if (downRowTaskList != null && !downRowTaskList.isEmpty()) {
                            mQueryRowTaskBean = downRowTaskList.get(0);
                        }
                    } else {
                        currTaskStatus = mQueryRowTaskBean.getCurrTaskStatus();
                        mFileSize = mQueryRowTaskBean.getFileSize();
                        mFilePointer = mQueryRowTaskBean.getFilePointer();
                        fileAllNumber = mQueryRowTaskBean.getFileAllNumber();
                        filePositionNumber = mQueryRowTaskBean.getFilePositionNumber();
                    }
                }
            }
            mRandomAccessFile = new RandomAccessFile(locationFile, "rws");

            mLocationFilePath = locationFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isPreviewFile) {
            if (currTaskStatus == 0 || currTaskStatus == 1) {
                mDisposables.add(startTimeOutTiming());
            }
        } else {
            mDisposables.add(startPreviewTimeOutTiming());
        }
    }


    public long getFileSize() {
        return mFileSize;
    }

    public String getFilePositionNumber() {
        return TextUtils.isEmpty(filePositionNumber) ? "0" : filePositionNumber;
    }

    public void setFilePositionNumber(String filePositionNumber) {
        this.filePositionNumber = filePositionNumber;
    }

    public int getCurrTaskStatus() {
        return currTaskStatus;
    }

    long secondStartPointer = 0;

    long secondDownTimeMillis = 0;


    public void write(byte[] bytes, String index, String all) {
        try {
            mRandomAccessFile.write(bytes);
            currRequestFrequency = 0;
            filePositionNumber = index;
            fileAllNumber = all;
            currTaskStatus = 1;
            long filePointer = mRandomAccessFile.getFilePointer();
            mFilePointer = filePointer;

            long currTime = System.currentTimeMillis() / 1000;
            if (currTime - secondDownTimeMillis > 1) {
                long difPointer = mFilePointer - secondStartPointer;
                if (difPointer < 0) {
                    difPointer = 0;
                }
                mDownSpeed = CacheManager.getSmallFormatSize(difPointer);
                secondStartPointer = mFilePointer;
                secondDownTimeMillis = currTime;
            }

            lastPacketReceiveTimeMillis = System.currentTimeMillis();
            if (!mIsPreviewFile) {
                startDBThrad();

            }
//            System.out.println("----time : " + (lastPacketReceiveTimeMillis - start) + "    length:" + bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] bytes) {
        try {
            mRandomAccessFile.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int currRequestFrequency = 0;

    Observable observable = Observable.interval(5, 1, TimeUnit.SECONDS).observeOn(Schedulers.newThread());


    private Disposable startPreviewTimeOutTiming() {
        return observable.subscribe((Consumer<Long>) l -> {

            long currTime = System.currentTimeMillis();
            if (currTime - lastPacketReceiveTimeMillis > 1000) {
                DownLoadRequestBean downloadTaskBean = getDownLoadResponseBean();
                if (mTimeOutTimingListener != null) {
                    mTimeOutTimingListener.outTiming(downloadTaskBean);
                    if (currRequestFrequency > 25) {
                        mDisposables.clear();
                        currRequestFrequency = 0;
                    }
                    currRequestFrequency++;
                }
            }
        });
    }

    //超时 计时
    private Disposable startTimeOutTiming() {
        return observable.subscribe((Consumer<Long>) l -> {
            if (!mIsPause && (currTaskStatus == 0 || currTaskStatus == 1)) {
                long currTime = System.currentTimeMillis();
                if (currTime - lastPacketReceiveTimeMillis > 1000) {
                    if (!TextUtils.equals("0", mDownSpeed)) {
                        mDownSpeed = "0";
                        if (mTimeOutTimingListener != null) {
                            mTimeOutTimingListener.refreshTaskList();
                        }
                    }
                }
                if (currTime - lastPacketReceiveTimeMillis > 1000) {
                    mDownSpeed = "0";
                    DownLoadRequestBean downloadTaskBean = getDownLoadResponseBean();
                    if (mTimeOutTimingListener != null) {
                        mTimeOutTimingListener.outTiming(downloadTaskBean);
                        if (currRequestFrequency > 25) {
                            currTaskStatus = 3;
                            mTimeOutTimingListener.refreshTaskList();
//                            mTimeOutTimingListener.checkWaitTask();
                            mDisposables.clear();
                            mQueryRowTaskBean.setCurrTaskStatus(currTaskStatus);
                            mBox.put(mQueryRowTaskBean);
                            currRequestFrequency = 0;
                        }
                        currRequestFrequency++;
                    }
                }
            } else if (currTaskStatus == 2 || isPause() || currTaskStatus == 4) {
                mDisposables.clear();
            }
        });
    }

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private ThreadUtils.SimpleTask dbTask = new ThreadUtils.SimpleTask<String>() {
        @Nullable
        @Override
        public String doInBackground() {
            try {
                String positionNumber = mQueryRowTaskBean.getFilePositionNumber();
                if (!TextUtils.equals(positionNumber, filePositionNumber) && !mIsPause && currTaskStatus == 1) {
                    mQueryRowTaskBean.setCurrTaskStatus(currTaskStatus);
                    mQueryRowTaskBean.setFilePointer(mFilePointer);
                    mQueryRowTaskBean.setFilePositionNumber(filePositionNumber);
                    mQueryRowTaskBean.setFileAllNumber(fileAllNumber);
                    mBox.put(mQueryRowTaskBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public void onSuccess(@Nullable String result) {

        }
    };

    private void startDBThrad() {
        ThreadUtils.executeByCustom(threadPool, dbTask);
    }

    // 超时 调用 - 重新发送  或者 暂停重新开始后 发送
    public void setTimeOutTimingListener(TaskErrorListener l) {
        mTimeOutTimingListener = l;
    }


    //
    public void setPause(boolean status) {
        if (currTaskStatus == 1 || currTaskStatus == 0) {
            DownLoadRequestBean downloadTaskBean = getDownLoadResponseBean();
            mIsPause = status;

            if (!status && mTimeOutTimingListener != null) {
                // 开始
                mTimeOutTimingListener.outTiming(downloadTaskBean);
                mDisposables.add(startTimeOutTiming());
//                startDownTimeMillis = System.nanoTime();
//                mDisposables.add(startDBThrad()); mFilePointer
                // 当前的文件位置
//    long downSpeed = (long) (1000000000 / 1 * filePointer / (System.nanoTime() - startDownTimeMillis + 1));
                secondDownTimeMillis = 0;
                try {
                    secondStartPointer = mRandomAccessFile.getFilePointer();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                // 暂停
                mDisposables.clear();
            }
        }
    }

    public boolean isPause() {
        return mIsPause;
    }

    // 重试
    public void retry() {
        mIsPause = false;
        currTaskStatus = 0;
        currRequestFrequency = 0;
        mDisposables.add(startTimeOutTiming());
//        startDownTimeMillis = System.nanoTime();
//        mDisposables.add(startDBThrad());
        mQueryRowTaskBean.setCurrTaskStatus(currTaskStatus);
        mBox.put(mQueryRowTaskBean);
        if (mTimeOutTimingListener != null) {
            mTimeOutTimingListener.refreshTaskList();
        }
        DownLoadRequestBean downloadTaskBean = getDownLoadResponseBean();
        if (mTimeOutTimingListener != null) {
            mTimeOutTimingListener.outTiming(downloadTaskBean);
        }
        lastPacketReceiveTimeMillis = System.currentTimeMillis();
    }

    public interface TaskErrorListener {
        void outTiming(DownLoadRequestBean value);

        void refreshTaskList();

        void checkWaitTask();

    }

    public void closeFile() {
        try {
            mDisposables.clear();
            mRandomAccessFile.close();
            currTaskStatus = 2;
            endTime = System.currentTimeMillis();
            if (!mIsPreviewFile) {
                mQueryRowTaskBean.setCurrTaskStatus(currTaskStatus);
                mQueryRowTaskBean.setFilePointer(mFilePointer);
                mQueryRowTaskBean.setFilePositionNumber(filePositionNumber);
                mQueryRowTaskBean.setFileAllNumber(fileAllNumber);
                mQueryRowTaskBean.setFinishTime(endTime);
                mBox.put(mQueryRowTaskBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DownLoadRequestBean getDownLoadResponseBean() {
        return mDownLoadRequestBean;
    }

    public long getTaskId() {
        return mTaskId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * @return 文件下载的位置
     */
    public long getFilePointer() {
        return mFilePointer;
    }

    public void setFilePointer(long filePointer) {
        mFilePointer = filePointer;

        try {
            mRandomAccessFile.seek(mFilePointer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getFileName() {
        return mFileName == null ? "" : mFileName;
    }

    public String getDownSpeed() {
        return mDownSpeed;
    }


    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public void delFile() {
        File locationFile = new File(mLocationFilePath);
        if (locationFile.exists()) {
            locationFile.delete();
        }
        try {
            mBox.remove(mQueryRowTaskBean);
        }catch (Exception e){

        }

    }

    public String getLocationFilePath() {
        return mLocationFilePath == null ? "" : mLocationFilePath;
    }

    public boolean isPreviewFile() {
        return mIsPreviewFile;
    }

    public void setCurrTaskStatus(int currTaskStatus) {
        this.currTaskStatus = currTaskStatus;
        if (currTaskStatus == 2 || currTaskStatus == 3 || currTaskStatus == 4) {
            mDisposables.clear();
        }
    }


    public void clear() {
        currTaskStatus = 5;
        mIsPause = true;
        mDisposables.clear();
        dbTask.cancel();
        threadPool.shutdownNow();
        mBox = null;
        mDownLoadRequestBean = null;
    }

}
