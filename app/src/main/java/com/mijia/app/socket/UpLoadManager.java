package com.mijia.app.socket;

import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.Gson;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.MyApp;
import com.mijia.app.bean.UpRowTaskBean;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.bean.UploadResponseBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.service.MiJiaService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class UpLoadManager implements UpLoadTask.TimeOutTimingListener {

    private ConcurrentLinkedQueue<UploadResponseBean> queue = new ConcurrentLinkedQueue();
    private Gson mGson = new Gson();

    private ConcurrentMap<String, UpLoadTask> mTaskList = new ConcurrentHashMap();

    private static UpLoadManager MANAGER = new UpLoadManager();

    private MiJiaService.ConnectBinder mConnectBinder;

    private ThreadTask mThreadTask = new ThreadTask();

    public static UpLoadManager getInstance() {
        return MANAGER;
    }

    private Box<UpRowTaskBean> mBox;

    private UpLoadManager() {
        mBox = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);
        List<UpRowTaskBean> list = mBox.query().build().find();
        if (!list.isEmpty()) {
            for (UpRowTaskBean rowTaskBean : list) {
                int currTaskStatus = rowTaskBean.getCurrTaskStatus();
                String fullPath = rowTaskBean.getFullPath();
                String locationPath = rowTaskBean.getLocationPath();
                long fileSize = rowTaskBean.getFileSize();
                long filePointer = rowTaskBean.getFilePointer();

                String fileAllNumber = rowTaskBean.getFileAllNumber();
                String filePositionNumber = rowTaskBean.getFilePositionNumber();

                UploadRequestBean uploadRequestBean = new UploadRequestBean();
                uploadRequestBean.setFullpath(fullPath);
                UpLoadTask upLoadTask = new UpLoadTask(uploadRequestBean, locationPath);
                upLoadTask.setPause(true);
                upLoadTask.setTimeOutTimingListener(this);
                upLoadTask.setLastIndex(Integer.valueOf(filePositionNumber));
                upLoadTask.setCurrTaskStatus(currTaskStatus);

                mTaskList.put(fullPath, upLoadTask);
            }
        }
    }

    public int taskRuningNumber() {
        int number = 0;
        for (String key : mTaskList.keySet()) {
            UpLoadTask task = mTaskList.get(key);
            if ((task.getCurrTaskStatus() == 0 || task.getCurrTaskStatus() == 1) && !task.isPause()) {
                number++;
            }
        }
        return number;
    }


    public boolean addTask(UpLoadTask task) {
        UploadRequestBean uploadRequestBean = task.getUploadRequestBean();
        String fullPath = uploadRequestBean.getFullpath();
        if (!mTaskList.containsKey(fullPath)) {
            task.setTimeOutTimingListener(this);
            mTaskList.put(fullPath, task);
            uploadRequestBean.setUserName(AccountHelper.getNickname());
            uploadRequestBean.setUserId(AccountHelper.getUserId());
            uploadRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
            uploadRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());

            uploadRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
            uploadRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
            String f = task.getFileForPosition(1);
            uploadRequestBean.setData(f);
            uploadRequestBean.setAll(task.getTotal() + "");
            uploadRequestBean.setIndex(1 + "");
            String datalen = "0";
            if (task.getTotal() == 1) {
                datalen = task.getFileSize() + "";
            } else {
                datalen = task.getPACKSIZE() + "";
            }
            uploadRequestBean.setDatalen(datalen);

            String json = mGson.toJson(uploadRequestBean);
            byte[] bytes = UdpDataUtils.getDataByte((byte) 0x03, 1, 1, json);
            mConnectBinder.send(bytes);
            if (mUpLoadScheduleListener != null) {
                mUpLoadScheduleListener.onTaskListChange();
            }
        } else {
            task.setPause(true);
            task = null;
            return false;
        }
        return true;
    }


    public void upLoadRequestContent(byte[] dataBytes) {
        String json = new String(dataBytes).trim();
        UploadResponseBean uploadResponseBean = mGson.fromJson(json, UploadResponseBean.class);

        String key = uploadResponseBean.getFullpath();
        UpLoadTask task = mTaskList.get(key);
        if (task != null) {
            int currIndex = Integer.valueOf(uploadResponseBean.getIndex());
            long totalNumber = task.getTotal();
            if (currIndex >= totalNumber) {
                task.setCurrTaskStatus(2);
                if (mUpLoadScheduleListener != null) {
                    mUpLoadScheduleListener.onTaskListChange();
                }
            } else {
                queue.offer(uploadResponseBean);
                task.onLastReceivePacketTime();// 刷新最后一次接受到的时间
                System.out.println("---==  receive:" + queue.size() + "  " + mThreadTask.isRuning + "   " + uploadResponseBean.toString() + "   " + Thread.currentThread().getName());
                if (!mThreadTask.isRuning) {
                    ThreadUtils.executeByIo(mThreadTask, 10);
                    if (queue.size() % 100 == 0) {
                        dealWithByte();
                    }
                }
            }

        }
    }

    private void dealWithByte() {
        ThreadTask task = new ThreadTask();
        ThreadUtils.executeByIo(task, 10);
    }

    @Override
    public void outTiming(UpLoadTask task) {
        int index = (int) task.getLastIndex();
        sendContent(task, index - 1);
    }

    @Override
    public void refreshTaskList() {
        // 超时 失败后- 刷新列表
        if (mUpLoadScheduleListener != null) {
            mUpLoadScheduleListener.onTaskListChange();
        }
    }

    private long mLastRefreshTime = 0;

    private class ThreadTask extends ThreadUtils.SimpleTask<String> {
        public volatile boolean isRuning = false;

        //
        @Nullable
        @Override
        public String doInBackground() {
            try {
                isRuning = true;
//                System.out.println("---==  threadName:" + Thread.currentThread().getName());
                while (!queue.isEmpty()) {
//                    System.out.println("---==  " + queue.size());
                    UploadResponseBean uploadResponseBean = queue.poll();
                    String index = uploadResponseBean.getIndex();
                    String key = uploadResponseBean.getFullpath();

                    UpLoadTask task = mTaskList.get(key);
                    int receiveIndex = Integer.valueOf(index);
                    sendContent(task, receiveIndex);
                    long currTime = System.currentTimeMillis();
                    if (currTime - mLastRefreshTime > 1000) {
                        mLastRefreshTime = currTime;
                        Observable.create(emitter -> {
                            if (mUpLoadScheduleListener != null) {
                                mUpLoadScheduleListener.onUpSchedule();
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                isRuning = false;
            }

            isRuning = false;
            return "";
        }

        @Override
        public void onSuccess(@Nullable String result) {

        }
    }


    public void sendContent(UpLoadTask task, int receiveIndex) {
        if (!task.isPause()) {
            UploadRequestBean uploadRequestBean = task.getUploadRequestBean();
            String f = task.getFileForPosition(receiveIndex + 1);

            uploadRequestBean.setUserName(AccountHelper.getNickname());
            uploadRequestBean.setUserId(AccountHelper.getUserId());
            uploadRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
            uploadRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());

            uploadRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
            uploadRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

            uploadRequestBean.setData(f);

            uploadRequestBean.setAll(task.getTotal() + "");
            uploadRequestBean.setIndex(receiveIndex + 1 + "");
            String datalen = "0";

            //  endPosition == mFileSize
            if (task.getCurrReadEndPosition() == task.getFileSize()) {
                datalen = task.getCurrReadEndPosition() - task.getFilePointer() + "";
            } else {
                datalen = task.getPACKSIZE() + "";
            }
            uploadRequestBean.setDatalen(datalen);
            String json = mGson.toJson(uploadRequestBean);
            byte[] bytes = UdpDataUtils.getDataByte((byte) 0x03, 1, 1, json);
            mConnectBinder.send(bytes);
        }
    }

    public void setConnectBinder(MiJiaService.ConnectBinder connectBinder) {
        mConnectBinder = connectBinder;
    }


    public ConcurrentMap<String, UpLoadTask> getTaskList() {
        return mTaskList;
    }


    public interface UpLoadScheduleListener {
        // 单个任务 上传 进度监听
        void onUpSchedule();

        // 任务列表发生改变  // 增加任务/删除任务/ 有任务完成
        void onTaskListChange();

    }

    private UpLoadScheduleListener mUpLoadScheduleListener;


    public void setUpLoadScheduleListener(UpLoadScheduleListener upLoadScheduleListener) {
        mUpLoadScheduleListener = upLoadScheduleListener;
    }

    public void exit() {
        // 如果退出 ，则 暂停 0/1 的任务
        for (String key : mTaskList.keySet()) {
            UpLoadTask task = mTaskList.get(key);
            if ((task.getCurrTaskStatus() == 0 || task.getCurrTaskStatus() == 1)) {
                if (!task.isPause()) {
                    task.setPause(true);
                }
            }
        }
    }

    public void removeTask(String fullPath) {
        UpLoadTask task = mTaskList.remove(fullPath);
        if (task != null) {
            task.del();
        }
    }
}
