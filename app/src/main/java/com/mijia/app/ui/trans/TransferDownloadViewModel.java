package com.mijia.app.ui.trans;

import android.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.socket.DownTask;


public class TransferDownloadViewModel extends BaseViewModel {


//    {
//        "userName": "test_user",
//            "userId": "aaaaaaaaaa",
//            "gsName": "test_gs",
//            "gsId": "bbbbbbbbbbbbbb",
//            "diskName": "test_disk",
//            "diskId": "cccccccccccc",
//            "fullpath": "/1.txt",
//            "index":"3"
//    }


//    public void getOneFileInfo() {
//
//
//        DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
//        downloadTaskBean.setFullpath("E:/迅雷下载/33333333.txt");
//        downloadTaskBean.setIndex("1");
//        DownTask downTask = new DownTask(downloadTaskBean, 17984229, false);
//        DownLoadManager.getInstance().addTask(downTask);
//
//
//        DownLoadRequestBean downloadTaskBean1 = new DownLoadRequestBean();
//        downloadTaskBean1.setFullpath("E:/迅雷下载/H3CNE第16章：IP子网划分.mp4");
//        downloadTaskBean1.setIndex("1");
//        DownTask downTask1 = new DownTask(downloadTaskBean1, 18946767, false);
//        DownLoadManager.getInstance().addTask(downTask1);
//
//
//        DownLoadRequestBean downloadTaskBean2 = new DownLoadRequestBean();
//        downloadTaskBean2.setFullpath("E:/迅雷下载/空も飞べるはず - 豆瓣FM.mp4");
//        downloadTaskBean2.setIndex("1");
//        DownTask downTask2 = new DownTask(downloadTaskBean2, 4980782, false);
//        DownLoadManager.getInstance().addTask(downTask2);
//
//
//        DownLoadRequestBean downloadTaskBean3 = new DownLoadRequestBean();
//        downloadTaskBean3.setFullpath("E:/迅雷下载/算法图解.pdf");
//        downloadTaskBean3.setIndex("1");
//        DownTask downTask3 = new DownTask(downloadTaskBean3, 17918292, false);
//        DownLoadManager.getInstance().addTask(downTask3);
//
//
//        DownLoadRequestBean downloadTaskBean4 = new DownLoadRequestBean();
//        downloadTaskBean4.setFullpath("E:/迅雷下载/head6.png");
//        downloadTaskBean4.setIndex("1");
//        DownTask downTask4 = new DownTask(downloadTaskBean4, 18527, false);
//        DownLoadManager.getInstance().addTask(downTask4);
//
//
//        DownLoadRequestBean downloadTaskBean5 = new DownLoadRequestBean();
//        downloadTaskBean5.setFullpath("E:/迅雷下载/head5.png");
//        downloadTaskBean5.setIndex("1");
//        DownTask downTask5 = new DownTask(downloadTaskBean5, 18280, false);
//        DownLoadManager.getInstance().addTask(downTask5);
//
//        DownLoadRequestBean downloadTaskBean6 = new DownLoadRequestBean();
//        downloadTaskBean6.setFullpath("E:/迅雷下载/ewer3.jpg");
//        downloadTaskBean6.setIndex("1");
//        DownTask downTask6 = new DownTask(downloadTaskBean6, 9262, false);
//        DownLoadManager.getInstance().addTask(downTask6);
//
//
//    }
}
