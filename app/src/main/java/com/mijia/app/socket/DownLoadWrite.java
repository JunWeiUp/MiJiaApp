package com.mijia.app.socket;

import android.text.TextUtils;
import android.util.Base64;

import com.blankj.utilcode.util.ThreadUtils;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.DownLoadResponseBean;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DownLoadWrite {


    //    private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private ConcurrentLinkedQueue<DownLoadResponseBean> queue = new ConcurrentLinkedQueue();

    private ThreadTask mTask = new ThreadTask();

    private long freshData = 0;
    private ConcurrentMap<String, DownTask> mTaskList;


    public DownLoadWrite(ConcurrentMap<String, DownTask> taskList) {
        mTaskList = taskList;
    }

    public void addData(DownLoadResponseBean data) {
        queue.offer(data);
    }

    private class ThreadTask extends ThreadUtils.SimpleTask {
        public boolean isRuning = false;

        @Override
        public Object doInBackground() {
            isRuning = true;
            try {
                while (true) {
                    if (!queue.isEmpty()) {
//                        System.out.println("------==  img queue size :" + queue.size());
                        DownLoadResponseBean downloadResponseBean = queue.poll();
                        String key = downloadResponseBean.getFullpath();
                        key = key + downloadResponseBean.getType();
                        DownTask downTask = mTaskList.get(key);
                        if (downTask != null) {
                            DownLoadRequestBean downloadTaskBean = downTask.getDownLoadResponseBean();
                            String taskFullPath = downloadTaskBean.getFullpath();
                            String srcData = downloadResponseBean.getData();
                            byte[] bytes = Base64.decode(srcData, Base64.DEFAULT);
//                            byte[] bytes = srcData.getBytes();

                            int all = Integer.valueOf(downloadResponseBean.getAll());
                            int index = Integer.valueOf(downloadResponseBean.getIndex());// 1,2,3,4,5,6,7
                            int filePostionNumber = Integer.valueOf(downTask.getFilePositionNumber());
//                                System.out.println("------==  img thread Name : " + all + "  " + index + "   " + filePostionNumber);
                            if (downTask.getCurrTaskStatus() == 0 || downTask.getCurrTaskStatus() == 1) {
                                if (index >= filePostionNumber) {
                                    if (!downTask.isPause()) {
                                        if (!downTask.isPreviewFile()) {
                                            downTask.write(bytes, index + "", all + "");
                                        } else {
                                            downTask.write(bytes);
                                        }
                                        if (all == index) {
                                            downTask.closeFile();
                                            // 如果是缓存文件 则返回路径 并删除 该任务
                                            if (downTask.isPreviewFile()) {
                                                if (mWriteFileListener != null) {
                                                    mWriteFileListener.onPreviewLocationPath(key, downTask.getLocationFilePath());
                                                    mTaskList.remove(key);
                                                }
                                            }
                                        }
                                        if (!downTask.isPreviewFile()) {
                                            // 每隔1秒 刷新界面
                                            if (1 == downTask.getCurrTaskStatus()) {
                                                long currData = System.currentTimeMillis();
                                                if (currData - freshData > 1000) {
                                                    freshData = currData;
                                                    Observable.create(emitter -> {
                                                        if (mWriteFileListener != null) {
                                                            mWriteFileListener.onRefreshUI();
                                                        }
                                                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                                                }
                                            } else {
                                                Observable.create(emitter -> {
                                                    if (mWriteFileListener != null) {
                                                        mWriteFileListener.onFinishedTaskChange();
                                                    }
                                                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();


                                            }
                                        }
                                    } else {
                                        if (mTaskList.containsKey(key)) {
                                            queue.offer(downloadResponseBean);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        isRuning = false;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                isRuning = false;
            }
            return "over";
        }

        @Override
        public void onSuccess(Object result) {
            isRuning = false;
//            System.out.println("--  over");
        }
    }

    public boolean isRunningTask() {
        return mTask.isRuning;
    }

    public void dealWithByte() {
        ThreadUtils.executeByCpu(mTask, 10);
    }

    public interface WriteFileListener {
        void onPreviewLocationPath(String key, String path);

        void onRefreshUI();

        void onFinishedTaskChange();
    }

    private WriteFileListener mWriteFileListener;

    public void setWriteFileListener(WriteFileListener l) {
        mWriteFileListener = l;
    }

    public void stopTask() {
        mTask.cancel();
        queue.clear();
    }


}
