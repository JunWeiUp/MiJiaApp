package com.mijia.app.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.bean.ObservableEmptyImp;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.Url;
import com.mijia.app.utils.NetworkUtils;

import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 通信类
 */
public class Communication {

    private static Communication mCommunication = new Communication();
    private static DatagramSocket client = null;
    private HoldInterface mHoldInterface = new StandardHoldImp();

    private OuterNetHodImp outerNetHodImp = new OuterNetHodImp();

    private HoldInterface mOutHoldInterface = new OuterNetHodImp();


    public static Communication getInstance() {
        try {
            if (client == null)
                client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mCommunication;
    }

    /**
     * 外网穿透
     * @param ip
     * @param port
     * @return
     */
    public Disposable startOuterNetHole(String ip,String port){
        return observable.subscribe(o -> outerNetHodImp.sendHoleInfo(ip,port,client));
    }
    // 开始打洞
    public Disposable startHole(String ip, String port) {
        return observable.subscribe(o -> mHoldInterface.sendHoleInfo(ip, port, client));
    }


    /**
     * 外网穿透
     * @param ip
     * @param port
     */
    public void startOutHole(String ip,String port){
        outerNetHodImp.sendHoleInfo(ip,port,client);
    }

    /**
     * 内网穿透
     * @param ip
     * @param port
     */
    public void startNeiHole(String ip,String port){
        mHoldInterface.sendHoleInfo(ip, port, client);
    }

    // 接收信息
    public void startReceiveInfo(Subscriber<DatagramPacket> flowableSubscriber) {
        Flowable.create((FlowableOnSubscribe<DatagramPacket>) emitter -> {
            while (true) {
                byte[] buf = new byte[Constants.PACKSIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                client.receive(packet);
                emitter.onNext(packet);
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.newThread()).subscribe(flowableSubscriber);
    }



    // 接收信息
    public void startReceiveInfoThread(Subscriber<DatagramPacket> flowableSubscriber) {
        new Thread(() -> {
            while (true) {
                byte[] buf = new byte[Constants.PACKSIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    client.receive(packet);
                    flowableSubscriber.onNext(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private Gson mGson = new Gson();
    Observable observable = Observable.interval(0,Constants.HEARTBEATPERIOD, TimeUnit.SECONDS);


    /**
     * {
     * "type":17,
     * "userName": "test_user",
     * "userId": "aaaaaaaaaa",
     * "sIp":"192.168.1.100",		    //app内网ip
     * "sPort":"8888"				//app内网port
     * }
     * @return
     */
    public Disposable out(){
        return observable.subscribe(o -> {
            String myIP = "";
            try {
                myIP = NetworkUtils.getLocalIPAddress();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","17");
            jsonObject.put("userName",AccountHelper.getNickname());
            jsonObject.put("userId",AccountHelper.getUserId());
            jsonObject.put("sIp",myIP);
            jsonObject.put("sPort",getLocationPort());
            byte[] sendBuf = jsonObject.toString().getBytes();
//            byte[] sendBuf = (cmdString + json).getBytes();
            SocketAddress clientB_Address = new InetSocketAddress(Url.SERVER_IP, Integer.valueOf(Url.SERVER_PORT));
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, clientB_Address);
            client.send(sendPacket);
            Log.i("UDP_SEND", "server 17---json====" + jsonObject.toString());
        }, throwable -> {

        });
    }


    //    发送心跳包
    public Disposable heartBeatInterval(String destIp, String port) {

        byte[] headCmd = new byte[15];
        headCmd[0] = 0x0C;

        String myIP = "";
        try {
            myIP = NetworkUtils.getLocalIPAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<>();
        map.put("userName", AccountHelper.getNickname());
        map.put("userId", AccountHelper.getUserId());
        map.put("ip", myIP);
        map.put("port", getLocationPort() + "");
        String json = mGson.toJson(map);
        String cmdString = new String(headCmd);

        return observable.subscribe(o -> {
            byte[] sendBuf = (cmdString + json).getBytes();
            SocketAddress clientB_Address = new InetSocketAddress(destIp, Integer.valueOf(port));
            DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, clientB_Address);
            client.send(sendPacket);
            Log.i("UDP_SEND", "0x0c---json====" + json.toString());
        }, throwable -> {

        });
    }

    /**
     * @param destIp 目的ip
     * @param port   目的端口
     * @param data   要发送的数据
     * @return
     */
    public void sendData(String destIp, String port, String data) {
        sendData(destIp, port, data, null);
    }


    /**
     * @param destIp          目的ip
     * @param port            目的端口
     * @param data            要发送的数据
     * @param observableEmpty 发送状态
     */
    public void sendData(String destIp, String port, String data, ObservableEmptyImp observableEmpty) {
        Observable observable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                byte[] bytes = data.getBytes();
                SocketAddress clientB_Address = new InetSocketAddress(destIp, Integer.valueOf(port));
                DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, clientB_Address);
                client.send(sendPacket);
            } catch (Exception e) {
                System.out.println("------ 发送 失败： " + data + "  " + e.getMessage());
                emitter.onNext(false);
            }
            emitter.onNext(true);
            System.out.println("------ 发送 成功： " + data);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        if (observableEmpty == null) {
            observable.subscribe();
        } else {
            observable.subscribe(observableEmpty);
        }
    }

    /**
     * @param destIp 目的ip
     * @param port   目的端口
     * @param data   要发送的数据
     * @return
     */
    public void sendData(String destIp, String port, byte[] data) {
        sendData(destIp, port, data, null);
    }


    /**
     * @param destIp          目的ip
     * @param port            目的端口
     * @param data            要发送的数据
     * @param observableEmpty 发送状态
     */
    public void sendData(String destIp, String port, byte[] data, ObservableEmptyImp observableEmpty) {
        Observable observable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                byte[] bytes = data;
                SocketAddress clientB_Address = new InetSocketAddress(destIp, Integer.valueOf(port));
                DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, clientB_Address);
                client.send(sendPacket);
            } catch (Exception e) {
//                System.out.println("------ 发送 失败： " + data + "  " + e.getMessage());
                emitter.onNext(false);
            }
            emitter.onNext(true);
//            System.out.println("------ 发送 成功： " + data);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        if (observableEmpty == null) {
            observable.subscribe();
        } else {
            observable.subscribe(observableEmpty);
        }
    }


    public int getLocationPort() {
        return client.getLocalPort();
    }


}
