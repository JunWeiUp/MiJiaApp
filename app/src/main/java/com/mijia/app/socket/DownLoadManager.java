package com.mijia.app.socket;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.mijia.app.MyApp;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.DownLoadResponseBean;
import com.mijia.app.bean.DownRowTaskBean;
import com.mijia.app.service.MiJiaService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DownLoadManager implements DownTask.TaskErrorListener, DownLoadWrite.WriteFileListener {
    private ConcurrentMap<String, DownTask> mTaskList = new ConcurrentHashMap();

    private static DownLoadManager downLoadManager = new DownLoadManager();
    private Gson mGson = new Gson();
    private MiJiaService.ConnectBinder mConnectBinder;
    private DownLoadScheduleListener mDownLoadScheduleListener;
    private DownLoadWrite mDownLoadWrite;

    private DownLoadManager() {
        Box<DownRowTaskBean> box = MyApp.getInstance().getBoxStore().boxFor(DownRowTaskBean.class);
        List<DownRowTaskBean> list = box.query().build().find();
        if (list != null && !list.isEmpty()) {
            for (DownRowTaskBean taskBean : list) {
                String key = taskBean.getFullpath();
                DownLoadRequestBean requestBean = new DownLoadRequestBean();
                int currStatus = taskBean.getCurrTaskStatus();
                int type = taskBean.getType();
                key = key + type;

                long filePointer = taskBean.getFilePointer();

                requestBean.setIndex(taskBean.getFilePositionNumber());
                requestBean.setFullpath(taskBean.getFullpath());
                requestBean.setDiskId(taskBean.getDiskId());
                requestBean.setDiskName(taskBean.getDiskName());
                requestBean.setGsId(taskBean.getGsId());
                requestBean.setGsName(taskBean.getGsName());
                requestBean.setUserId(taskBean.getUserId());
                requestBean.setUserName(taskBean.getUserName());


                DownTask downTask = new DownTask(requestBean, taskBean.getFileSize(), false);
                downTask.setPause(true);
                downTask.setTimeOutTimingListener(this);
                downTask.setFilePointer(filePointer);
                downTask.setCurrTaskStatus(currStatus);
                if (currStatus == 0 || currStatus == 1) {
                    requestBean.setIndex((taskBean.getFilePositionIntNumber() + 1) + "");
                }
                downTask.setEndTime(taskBean.getFinishTime());

                mTaskList.put(key, downTask);
            }
        }


        mDownLoadWrite = new DownLoadWrite(mTaskList);
        mDownLoadWrite.setWriteFileListener(this);

    }

    public Map<String, DownTask> getTaskList() {
        return mTaskList;
    }


    private boolean checkTask() {
        // 存在 0和1 的任务；并且含有为暂停的任务
        for (String key : mTaskList.keySet()) {
            DownTask task = mTaskList.get(key);
            boolean b = task.getCurrTaskStatus() == 0 || task.getCurrTaskStatus() == 1 ? true : false;
            if (b) {
                if (!task.isPause()) {//没有暂停-- 正在运行
                    return true;
                }
            }
        }
        return false;
    }

    public int taskRunningNumber() {
        int number = 0;
        for (String key : mTaskList.keySet()) {
            DownTask task = mTaskList.get(key);
            if ((task.getCurrTaskStatus() == 0 || task.getCurrTaskStatus() == 1) && !task.isPause()) {
                number++;
            }
        }
        return number;
    }


    public static DownLoadManager getInstance() {
        return downLoadManager;
    }

//    public boolean addTask(DownTask task) {
//        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
//        String key = downLoadRequestBean.getFullpath();
//        key = key + downLoadRequestBean.getType();
//
//        boolean contains = mTaskList.keySet().contains(key);
//        if (!contains) {
//            mTaskList.put(key, task);
//            if (!task.isPreviewFile()) {
//                task.setTimeOutTimingListener(this);
//
//                String json = mGson.toJson(downLoadRequestBean);
//                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
//                mConnectBinder.send(bytes);
//                if (mDownLoadScheduleListener != null) {
//                    mDownLoadScheduleListener.onTaskListChange();
//                }
//            } else {
//                String json = mGson.toJson(downLoadRequestBean);
//                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
//                mConnectBinder.send(bytes);
//            }
//        } else {
//            if (mTaskList.get(key).getCurrTaskStatus() != 2) {
//                ToastUtils.showShort("该文件正在下载中！");
//                task.clear();
//                task = null;
//                System.gc();
//            } else {
//
//                String finalKey = key;
//                DownTask finalTask = task;
//                new AlertDialog.Builder(MyApp.getInstance())
//                        .setTitle("提示")
//                        .setMessage("已经有对应的密文下载,是否继续？")
//                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                mTaskList.get(finalKey).delFile();
//                                mTaskList.remove(finalKey);
//
//
//                                mTaskList.put(finalKey, finalTask);
//                                if (!finalTask.isPreviewFile()) {
//                                    finalTask.setTimeOutTimingListener(DownLoadManager.this);
//
//                                    String json = mGson.toJson(downLoadRequestBean);
//                                    byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
//                                    mConnectBinder.send(bytes);
//                                    if (mDownLoadScheduleListener != null) {
//                                        mDownLoadScheduleListener.onTaskListChange();
//                                    }
//                                } else {
//                                    String json = mGson.toJson(downLoadRequestBean);
//                                    byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
//                                    mConnectBinder.send(bytes);
//                                }
//                            }
//                        })
//                        .setNegativeButton("否", null)
//                        .show();
//
//
//            }
//
//        }
//        return contains;
//    }

    public boolean addTask(DownTask task) {
        DownLoadRequestBean downLoadRequestBean = task.getDownLoadResponseBean();
        String key = downLoadRequestBean.getFullpath();
        key = key + downLoadRequestBean.getType();

        boolean contains = mTaskList.keySet().contains(key);
        if (!contains) {
            mTaskList.put(key, task);
            if (!task.isPreviewFile()) {
                task.setTimeOutTimingListener(this);

                String json = mGson.toJson(downLoadRequestBean);
                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
                mConnectBinder.send(bytes);
                if (mDownLoadScheduleListener != null) {
                    mDownLoadScheduleListener.onTaskListChange();
                }
            } else {
                String json = mGson.toJson(downLoadRequestBean);
                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
                mConnectBinder.send(bytes);
            }
        } else {
            if (mTaskList.get(key).getCurrTaskStatus() != 2) {
                ToastUtils.showShort("该文件正在下载中！");
                task.clear();
                task = null;
                System.gc();
            }

        }
        return contains;
    }

    private List<DownTask> loadingTaskList = new ArrayList<>();
    private List<DownTask> waitTaskList = new ArrayList<>();


    public void getLoadingList() {
        for (String key : mTaskList.keySet()) {
            // 控制数量
            DownTask downTask = mTaskList.get(key);
            if (0 == downTask.getCurrTaskStatus() || 1 == downTask.getCurrTaskStatus()) {
                loadingTaskList.add(downTask);
            } else {
                waitTaskList.add(downTask);
            }
        }
    }

    public void downLoadContent(DownLoadResponseBean downloadResponseBean) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<String>() {
            @Nullable
            @Override
            public String doInBackground() throws Throwable {
                String key = downloadResponseBean.getFullpath();
                key = key + downloadResponseBean.getType();
                DownTask downTask = mTaskList.get(key);
                if (downTask == null) {
                    return "";
                }
                downTask.setAll(downloadResponseBean.getAll());
                if (downTask != null) {
                    if (downTask.getCurrTaskStatus() == 3 || downTask.getCurrTaskStatus() == 2) {
                        return "";
                    }

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
                        int intAll = Integer.valueOf(all);
                        if (!downTask.isPause()) {
                            downloadTaskBean.setIndex((Integer.valueOf(index) + 1) + "");
                            String requstJson = mGson.toJson(downloadTaskBean);
                            if (Integer.valueOf(index) < intAll) {
                                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, requstJson);
                                mConnectBinder.send(bytes);
                            }
                        }
                    }
                }
                return "";
            }

            @Override
            public void onSuccess(@Nullable String result) {

            }
        });


    }

    public void removeTask(String key) {
        DownTask task = mTaskList.remove(key);
        if (task != null) {
            task.delFile();
            if (mDownLoadScheduleListener != null) {
                mDownLoadScheduleListener.onTaskListChange();
            }
        }

    }

    // 超时/暂停重新开始后 发送请求包
    @Override
    public void outTiming(DownLoadRequestBean value) {
        String requstJson = mGson.toJson(value);
        byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, requstJson);
        mConnectBinder.send(bytes);
        //更新进度
        Observable.just("").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            if (mDownLoadScheduleListener != null) {
                mDownLoadScheduleListener.onLoadSchedule();
            }
        });
    }

    // 任务失败监听
    @Override
    public void refreshTaskList() {
        if (mDownLoadScheduleListener != null) {
            mDownLoadScheduleListener.onTaskListChange();
        }
    }

    @Override
    public void checkWaitTask() {
//        checkContainsWaitTask();
    }


    public void setDownLoadScheduleListener(DownLoadScheduleListener loadScheduleListener) {
        mDownLoadScheduleListener = loadScheduleListener;
    }


    public void setConnectBinder(MiJiaService.ConnectBinder connectBinder) {
        mConnectBinder = connectBinder;
    }

    @Override
    public void onPreviewLocationPath(String key, String path) {

    }

    @Override
    public void onRefreshUI() {
        if (mDownLoadScheduleListener != null) {
            mDownLoadScheduleListener.onLoadSchedule();
        }
    }

    // 写入文件的监听有下载完成的任务后调用
    @Override
    public void onFinishedTaskChange() {
        if (mDownLoadScheduleListener != null) {
            mDownLoadScheduleListener.onTaskListChange();
        }
//        checkContainsWaitTask();
    }

    // 视察是否存在 等待的任务，然后开启它
    public void checkContainsWaitTask() {

        int taskRunningNumber = taskRunningNumber();
        if (taskRunningNumber < 4) {
            // 开启一个新的任务
            List<DownTask> list = new ArrayList<>();


            for (String key : mTaskList.keySet()) {
                DownTask task = mTaskList.get(key);
                if (task.getCurrTaskStatus() == 4) {
                    list.add(task);
                    break;
                }
            }

            if (!list.isEmpty()) {
                Collections.sort(list, (downTask, t1) -> downTask.getTaskId() > t1.getTaskId() ? 1 : 0);
                DownTask newDownTask = list.get(0);

                newDownTask.setCurrTaskStatus(0);
                newDownTask.setPause(false);
                DownLoadRequestBean downLoadRequestBean = newDownTask.getDownLoadResponseBean();
                String json = mGson.toJson(downLoadRequestBean);
                byte[] bytes = UdpDataUtils.getDataByte((byte) 0x04, 1, 1, json);
                mConnectBinder.send(bytes);
//                mConnectBinder.send(request);
                if (mDownLoadScheduleListener != null) {
                    mDownLoadScheduleListener.onTaskListChange();
                }
            }
        }
    }


    public interface DownLoadScheduleListener {
        // 单个任务 下载 进度监听
        void onLoadSchedule();

        // 任列表发生改变  // 增加任务/删除任务/ 有任务完成务
        void onTaskListChange();

    }


    public void exit() {
        for (String key : mTaskList.keySet()) {
            DownTask task = mTaskList.get(key);
            if ((task.getCurrTaskStatus() == 0 || task.getCurrTaskStatus() == 1)) {
                if (!task.isPause()) {
                    task.setPause(true);
                }
            }
        }
    }

    public boolean checkIsExistsTask(String key) {
        return mTaskList.keySet().contains(key);
    }

}
