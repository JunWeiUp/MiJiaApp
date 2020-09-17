package com.mijia.app.ui.trans;

import android.os.Environment;

import com.handong.framework.base.BaseViewModel;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.socket.UpLoadTask;

import java.io.File;

public class TransferUploadViewModel extends BaseViewModel {
    private UpLoadManager manager = UpLoadManager.getInstance();

    public void addTask1() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/22222222.txt");
        UploadRequestBean uploadRequestBean = new UploadRequestBean();
        uploadRequestBean.setFullpath("E:/迅雷下载/22222222-up.txt");
        UpLoadTask task = new UpLoadTask(uploadRequestBean, file.getAbsolutePath());
        manager.addTask(task);

        // /sdcard/test/360jiagubao_windows_64.zip
        File file1 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/360jiagubao_windows_64.zip");
        UploadRequestBean uploadRequestBean1 = new UploadRequestBean();
        uploadRequestBean1.setFullpath("E:/迅雷下载/360jiagubao_windows_64-up.zip");
        UpLoadTask task1 = new UpLoadTask(uploadRequestBean1, file1.getAbsolutePath());
        manager.addTask(task1);

        // /sdcard/test/H3CNE第16章：IP子网划分.mp4
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/H3CNE第16章：IP子网划分.mp4");
        UploadRequestBean uploadRequestBean2 = new UploadRequestBean();
        uploadRequestBean2.setFullpath("E:/迅雷下载/H3CNE第16章：IP子网划分-up.mp4");
        UpLoadTask task2 = new UpLoadTask(uploadRequestBean2, file2.getAbsolutePath());
        manager.addTask(task2);
        // /sdcard/test/软件设计师教程 第5版@www.java1234.com.pdf

        File file3 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/软件设计师教程 第5版@www.java1234.com.pdf");
        UploadRequestBean uploadRequestBean3 = new UploadRequestBean();
        uploadRequestBean3.setFullpath("E:/迅雷下载/软件设计师教程 第5版@www.java1234.com-up.pdf");
        UpLoadTask task3 = new UpLoadTask(uploadRequestBean3, file3.getAbsolutePath());
        manager.addTask(task3);
    }

    public void addTask2() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/111111.txt");
        UploadRequestBean uploadRequestBean = new UploadRequestBean();
        uploadRequestBean.setFullpath("E:/迅雷下载/111111-up.txt");
        UpLoadTask task = new UpLoadTask(uploadRequestBean, file.getAbsolutePath());
        manager.addTask(task);

        File file1 = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "test/33333333.txt");
        UploadRequestBean uploadRequestBean1 = new UploadRequestBean();
        uploadRequestBean1.setFullpath("E:/迅雷下载/33333333-up.txt");
        UpLoadTask task1 = new UpLoadTask(uploadRequestBean1, file1.getAbsolutePath());
        manager.addTask(task1);


    }
}
