package com.mijia.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.bean.DownLoadResponseBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.Url;
import com.mijia.app.socket.BackupDownLoad;
import com.mijia.app.socket.BackupUpload;
import com.mijia.app.socket.Communication;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.socket.TempLoadingForMultiple;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;

import static com.mijia.app.constants.Constants.HEARTBEATPERIOD;
import static com.mijia.app.constants.Constants.HEARTOUTTIMING;
import static com.mijia.app.constants.Constants.NEICONNECTTIME;
import static com.mijia.app.constants.Constants.isPcOnLine;

/**
 * 
 * 
 * author yandon'tknow
 * email yancygzzy@gmail.com
 * create  
 */
public class MiJiaService extends Service {

    // 用来设置 rxjava 的 线程 背压
    private Subscription mSubscription = null;

    private ConnectBinder mConnectBinder = new ConnectBinder();
    private WifiManager.MulticastLock mWifiLock;
    private Communication mCommunication = Communication.getInstance();//通信
    private CompositeDisposable mDisposables = new CompositeDisposable();//订阅管理类
    private String destIp, destPort, outIP, outPort;//目标IP 目标端口 pc的ip pc的端口


    private DownLoadManager mDownLoadManager;
    private UpLoadManager mUpLoadManager;
    private MessageReceiveManager mMessageReceiveManager;
    private Gson mGson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();
        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = manager.createMulticastLock("test wifi");
        mWifiLock.acquire();
        mDownLoadManager = DownLoadManager.getInstance();
        mUpLoadManager = UpLoadManager.getInstance();
        mUpLoadManager.setConnectBinder(mConnectBinder);
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        mDownLoadManager.setConnectBinder(mConnectBinder);
//        mCommunication.startReceiveInfo(mReceiveObserver);
        mCommunication.startReceiveInfoThread(mReceiveObserver);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mConnectBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    private Disposable mHoleDisposable, mHeartBeatDisposable, mServerConnectDisposable;

    private Disposable mOuterDisposable;

    public class ConnectBinder extends Binder {


        /**
         * 设置pc 机的地址
         *
         * @param ip
         * @param port
         */
        public void setAddress(String ip, String port) {
            destIp = ip;
            destPort = port;
        }

        // 开始 发动 发送包
        public void hole() {
            if (mHoleDisposable == null) {
                mHoleDisposable = mCommunication.startHole(destIp, destPort);
                mDisposables.add(mHoleDisposable);
            }
        }

        public void serverConnect() {
            if (mServerConnectDisposable == null) {
                mServerConnectDisposable = mCommunication.out();
                mDisposables.add(mServerConnectDisposable);
            }
        }

        public void outerHole() {
            if (mOuterDisposable == null) {
                mOuterDisposable = mCommunication.startOuterNetHole(destIp, destPort);
                mDisposables.add(mOuterDisposable);
            }
        }

        public void send(String destIp, String port, String data) {
            mCommunication.sendData(destIp, port, data);
        }

        public void send(String data) {
            if (TextUtils.isEmpty(destIp) || TextUtils.isEmpty(destPort)) {
//                throw new RuntimeException("目标地址/端口号为空");
                return;
            }
            mCommunication.sendData(destIp, destPort, data);
        }

        public void send(byte[] data) {
            if (TextUtils.isEmpty(destIp) || TextUtils.isEmpty(destPort)) {
//                throw new RuntimeException("目标地址/端口号为空");
                return;
            }
            mCommunication.sendData(destIp, destPort, data);
        }
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }


    private String TAG = "UDP";
    private Subscriber<DatagramPacket> mReceiveObserver = new Subscriber<DatagramPacket>() {
        @Override
        public void onSubscribe(Subscription s) {
//            mSubscription = s;
//            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(DatagramPacket packet) {
            byte[] dataBytes = packet.getData();
//             命令字    总包数   当前包数    CRC16校验码
//             cmd  	 all	index   	 crc    	 data


//            {
//                "type":17,
//                    "isonline":0或者1,            //0不在线，1在线
//                    "userName": "test_user",
//                    "userId": "aaaaaaaaaa"，
//                "sIp":"192.168.1.100",		    //PC内网ip
//                    "sPort":"8888",			//PC的内网port
//                    "fIp":"192.168.1.100",		    //PC外网ip
//                    "fPort":"8888"			//PC外网port
//            }
            if (packet.getAddress().getHostAddress().equals(Url.SERVER_IP) && String.valueOf(packet.getPort()).equals(Url.SERVER_PORT)) {
                String jsonlog = new String(dataBytes).trim();

                try {
                    JSONObject jsonObject = new JSONObject(jsonlog);

                    if (jsonObject.getString("type").equals("17")) {
                        destIp = jsonObject.getString("sIp");
                        destPort = jsonObject.getString("sPort");
                        outIP = jsonObject.getString("fIp");
                        outPort = jsonObject.getString("fPort");
                        isPcOnLine = true;
                        mOuterDisposable.dispose();
                        startNeiWangConnect();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return;
            }


            byte cmd = dataBytes[0];

            byte[] contentlog = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, contentlog, 0, contentlog.length);
            String jsonlog = new String(contentlog).trim();
//            JSONObject jsonObjectlog = null;
//            try {
//                jsonObjectlog = new JSONObject(jsonlog);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (jsonObjectlog!=null) {
            Log.i("UDP_RE", cmd + "json====" + jsonlog);
//            }
//            System.out.println("--- upd_rece " + cmd + "  " + jsonObjectlog.toString());


//            if (0X01 == cmd) {
//                Constants.isPcConnecting = true;
//                return;
//            }


            if (0X0E == cmd) {


//                {
//                    "userName": "test_user",
//                        "userId": "aaaaaaaaaa"，
//                    "ip":"192.168.1.101",		//APP的内网ip
//                        "port":"8889"				//APPA的内网port
//                }

                String myIP = "";
                try {
                    myIP = NetworkUtils.getLocalIPAddress();
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("userName", AccountHelper.getNickname());
                jsonObject.addProperty("userId", AccountHelper.getUserId());
                jsonObject.addProperty("ip", myIP);
                // jsonObject.addProperty("gsName",mPcListAdapter.getData().get(position).getGsName());
                jsonObject.addProperty("port", Communication.getInstance().getLocationPort());
                byte[] bytes = UdpDataUtils.getDataByte((byte) 0X0E, 0, 0, jsonObject.toString());
                mConnectBinder.send(bytes);
                return;
            }

            if (cmd == 0X0d) {
                Constants.isPcConnecting = true;
                byte[] content = new byte[dataBytes.length - 15];
                System.arraycopy(dataBytes, 15, content, 0, content.length);
                String json = new String(content).trim();
                JSONObject jsonObject = null;
                String pcPort = "";
                try {
                    jsonObject = new JSONObject(json);
                    pcPort = jsonObject.getString("port");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 接收到 对方的打洞信息 则终止发送，，然后向 该设备发送心跳包
                if (mHoleDisposable != null && !mHoleDisposable.isDisposed()) {
                    mDisposables.remove(mHoleDisposable);
                    mHoleDisposable = null;
                }

                // 向对方发送心跳包
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String ip = address.getHostAddress();

                if (StringUtils.isEmpty(pcPort)) {
                    pcPort = port + "";
                }
                if (mHeartBeatDisposable == null) {
                    mHeartBeatDisposable = mCommunication.heartBeatInterval(ip, pcPort + "");
                    mDisposables.add(mHeartBeatDisposable);
                    startIntervalJudge();
                }
//                Constants.isPcConnecting = true;
                return;
            }
            if (cmd == 0x0C) {
                Constants.isPcConnecting = true;
                // 发送心跳 并 返回
                // 5秒内接收到返回说明 还在和 pc通讯，否则 转 服务器通讯
                mLastReceiveHeartResponseMillis = System.currentTimeMillis();

            }
            // 0x02---获取文件列表信息
//            0x03---上传文件  0x06 备份通讯录
            if (cmd == 0x03) {
                mUpLoadManager.upLoadRequestContent(contentlog);
                return;
            }
            if (cmd == 0x06) {
                BackupUpload.getInstance().receiveContent(contentlog);
                return;
            }
            if (cmd == 0x07) {
                BackupDownLoad.getInstance().receiveContent(contentlog);
                return;
            }

//            0x04---下载文件
            if (cmd == 0x04) {
                try {
                    String json = new String(contentlog, "UTF-8").trim();
                    DownLoadResponseBean downloadResponseBean = mGson.fromJson(json, DownLoadResponseBean.class);
                    String key = downloadResponseBean.getFullpath();
                    key = key + downloadResponseBean.getType();
                    if (TempLoadingForMultiple.constanceKey(key)) {
                        TempLoadingForMultiple.downImageContent(downloadResponseBean);
                    } else {
                        mDownLoadManager.downLoadContent(downloadResponseBean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            mMessageReceiveManager.onReceive(packet);
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
    };

    Observable<Long> intervalJudge = Observable.interval(30, HEARTBEATPERIOD, TimeUnit.SECONDS).subscribeOn(Schedulers.computation());
    Observable<Long> neiConnectJudge = Observable.interval(0, NEICONNECTTIME, TimeUnit.SECONDS).subscribeOn(Schedulers.computation());

    private CompositeDisposable neiDisposables = new CompositeDisposable();

    private void startNeiWangConnect() {
        Disposable disposable = neiConnectJudge.subscribe(aLong -> {
            if (aLong == 4) {
                neiDisposables.clear();
                startWaiWangConnect();
                return;
            }
            if (isPcOnLine) {
                mCommunication.startNeiHole(destIp, destPort);
            }
        });
        neiDisposables.add(disposable);
    }


    private void startWaiWangConnect() {
        Disposable disposable = neiConnectJudge.subscribe(aLong -> {
            if (aLong == 15) {
                neiDisposables.clear();
                return;
            }
            if (isPcOnLine) {
                mCommunication.startOutHole(outIP, outPort);
            }
        });
        neiDisposables.add(disposable);
    }


    private void startIntervalJudge() {
        Disposable disposable = intervalJudge.subscribe(aLong -> {
            if (Constants.isPcConnecting) {
                long currMillis = System.currentTimeMillis();
                if ((currMillis - mLastReceiveHeartResponseMillis > HEARTOUTTIMING * 1000)) {
//                    // TODO: 2019/8/30 0030  重新打洞
                    Constants.isPcConnecting = false;
                    mDisposables.delete(mHeartBeatDisposable);
                    mHeartBeatDisposable = null;
                    mConnectBinder.hole();
                }
            }
        });
        mDisposables.add(disposable);

    }

    private volatile long mLastReceiveHeartResponseMillis;


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposables.clear();
        UpLoadManager.getInstance().exit();
        DownLoadManager.getInstance().exit();
        if (mWifiLock != null) {
            mWifiLock.release();
        }
    }
}
