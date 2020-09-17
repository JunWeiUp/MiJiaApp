package com.mijia.app.socket;

import com.google.gson.Gson;
import com.mijia.app.bean.DiskBackUpContactBean;
import com.mijia.app.service.BackUpListListener;
import com.mijia.app.service.FileTransferListener;
import com.mijia.app.service.ReceiveOrderListener;
import com.mijia.app.service.ReceiveUserInfoListener;
import com.mijia.app.service.ReciveChangeDiskStatusOrderListener;
import com.mijia.app.service.ReciveExitOrderListener;
import com.mijia.app.service.ReciveFloderOrderListener;
import com.mijia.app.service.ReciveNewFloderOrderListener;
import com.mijia.app.service.ReciveRenameOrderListener;

import java.net.DatagramPacket;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MessageReceiveManager {

    private Gson mGson = new Gson();

    private static MessageReceiveManager MANAGER = new MessageReceiveManager();


    // 接收用户信息 0x01
    private ReceiveUserInfoListener mReceiveUserInfoListener;
    //文件传输列表
    private FileTransferListener mFileTransferListener;
    //
    private ReceiveOrderListener mReceiveOrderListener;
    //密夹远程退出指令
    private ReciveExitOrderListener mReciveExitOrderListener;
    //密夹重命名指令
    private ReciveRenameOrderListener mReciveRenameOrderListener;
    //备份列表
    private BackUpListListener mBackUpListListener;



    //列表刷新监听
    private ReciveFloderOrderListener mReciveFloderOrderListener;


    //新建文件夹指令监听
    private ReciveNewFloderOrderListener mReciveNewFloderOrderListener;
    //选择文件夹页面

    private ReciveChangeDiskStatusOrderListener mChangeDiskStatusOrderListener;


    public static MessageReceiveManager getInstance() {
        return MANAGER;
    }


    private MessageReceiveManager() {
    }


    public void onReceive(DatagramPacket packet) {
        byte[] dataBytes = packet.getData();

        byte cmd = dataBytes[0];
//
//        if (mReceiveOrderListener != null) {
//            mReceiveOrderListener.onReceiveOrder(packet);
//            return;
//        }


        // 0x01---获取用户数据信息
        if (cmd == 0x01) {
            if (mReceiveUserInfoListener != null) {
                mReceiveUserInfoListener.onReceiveUserInfo(packet);
            }
        }
        //密夹远程退出
        if (cmd == 0X08) {
            if (mReciveExitOrderListener != null) {
                mReciveExitOrderListener.onReceiveOrder(packet);
            }
        }
        //密夹重命名
        if (cmd == 0X09) {
            if (mReciveRenameOrderListener != null) {
                mReciveRenameOrderListener.onRecive(packet);
            }
        }
        //改变disk状态
        if (cmd == 0X0A) {
            if (mChangeDiskStatusOrderListener != null) {
                mChangeDiskStatusOrderListener.onRecive(packet);
            }
        }

        if (cmd == 0X0B) {
            if (mReciveNewFloderOrderListener!=null) {
                mReciveNewFloderOrderListener.onRecive(packet);
            }
        }

        if (cmd==0X02) {
            if (mReciveFloderOrderListener!=null) {
                mReciveFloderOrderListener.onRecive(packet);
            }
        }

//        if (mReceiveOrderListener != null) {
//            mReceiveOrderListener.onReceiveOrder(packet);
//            return;
//        }
        if (cmd == 0x05) {
            if (mBackUpListListener != null) {
                byte[] content = new byte[dataBytes.length - 15];
                System.arraycopy(dataBytes, 15, content, 0, content.length);
                String receiveContent = new String(content).trim();
                DiskBackUpContactBean backUpHistoryListBean = mGson.fromJson(receiveContent, DiskBackUpContactBean.class);
                Observable.create(emitter -> {
                    mBackUpListListener.onReceiveListInfo(backUpHistoryListBean);
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            }
            return;
        }
    }


    public void setReciveExitOrderListener(ReciveExitOrderListener reciveExitOrderListener) {
        mReciveExitOrderListener = reciveExitOrderListener;
    }


    public void setOnFileTransferListener(FileTransferListener fileTransferListener) {
        mFileTransferListener = fileTransferListener;
    }

    public void setReceiveUserInfoListener(ReceiveUserInfoListener receiveUserInfoListener) {
        mReceiveUserInfoListener = receiveUserInfoListener;
    }

    public void setReceiveOrderListener(ReceiveOrderListener receiveOrderListener) {
        mReceiveOrderListener = receiveOrderListener;
    }

    public void setReciveRenameOrderListener(ReciveRenameOrderListener reciveRenameOrderListener) {
        mReciveRenameOrderListener = reciveRenameOrderListener;
    }
    public void setBackUpListListener(BackUpListListener backUpListListener) {
        mBackUpListListener = backUpListListener;
    }

    public void setChangeDiskStatusOrderListener(ReciveChangeDiskStatusOrderListener changeDiskStatusOrderListener) {
        mChangeDiskStatusOrderListener = changeDiskStatusOrderListener;
    }

    public void setmReciveNewFloderOrderListener(ReciveNewFloderOrderListener mReciveNewFloderOrderListener) {
        this.mReciveNewFloderOrderListener = mReciveNewFloderOrderListener;
    }
    public void setmReciveFloderOrderListener(ReciveFloderOrderListener mReciveFloderOrderListener) {
        this.mReciveFloderOrderListener = mReciveFloderOrderListener;
    }
}
