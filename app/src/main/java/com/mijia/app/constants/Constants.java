package com.mijia.app.constants;

import android.databinding.ObservableField;

import java.security.spec.PSSParameterSpec;

public class Constants {

    // 当前网络状态 移动网络=0  wifi=1
    public static int Net_Status = 0;

    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    public static final String APP_ID = "wx1372e84357d0fb4f";

    public static final String AppSecret = "5cf2ec96539f58ae56c3017a1294432b";

//    各种网络环境下的P2P通信解决方法：
//   （1）如果通信双方在同一个局域网内，这种情况下可以不借助任何外力直接通过内网地址通信即可；
//   （2）如果通信双方都在有独立的公网地址，这种情况下当然可以不借助任何外力直接通信即可；
//   （3）如果通信双方一方拥有独立的公网地址另一方在NAT后面，那么可以由位于NAT后面的一方主动发起通信请求；
//   （4）如果通信双方都位于NAT后面，且双方的NAT类型都是cone NAT，那么可以通过一个STUN服务器发现自己的NAT类型以及内网和外网传输地址映射信息，然后通过Signaling(信令服务器，实现了SIP协议的主机)交换彼此的NAT类型及内网和外网传输地址映射信息，然后通过UDP打洞的方式建立通信连接；
//   （5）如果通信双方有一方的NAT类型是Symmetric NAT，则无法直接建立P2P连接，这个时候就需要借助TURN(Traversal Using Relay NAT)即转发服务器来实现间接通信；

    //PC端是否在线
    public static boolean isPcOnLine = false;

    //是否与PC端成功通信
    public static boolean isPcConnecting = false;

    // 双方 约束的打洞 标记
    public static String HOLETAG = "Hole";
    // 心跳
    public static String HEARTBEAT = "Heartbeat package";


    public static long NEICONNECTTIME = 60;

    //    心跳包的频率 秒
    public static long HEARTBEATPERIOD = 30;

    public static long HEARTOUTTIMING = 60;

    //    局域网环境下,UDP包大小为1024*8,速度达到2M/s,丢包情况理想.
//    外网环境下,UDP包大小为548,速度理想,丢包情况理想.
    public static int PACKSIZE = 1024 * 10;

    // 数据在 byte数据中 开始的位置（命令字 结束的位置）


    public static final int offset = 4;


    //视频路径
    public static final String VIDEOPATH = "video_path";

    //网页路径
    public static final String WEBURL = "weburl";


    /**
     * 选中密盾和磁盘
     */
    public static final SysContants SYS_CONTANTS_BEAN = new SysContants();

    /**
     * 磁盘管理页面当前文件夹路径
     */
    public static String DISK_NOW_PAGE_PATH = "";


    public static class SysContants {
        public final ObservableField<String> SelectedMiDun = new ObservableField<>("");
        public final ObservableField<String> SelectedDisk = new ObservableField<>("");
        public final ObservableField<String> SelectedDiskId = new ObservableField<>("");
        public final ObservableField<String> SeleectedMiDunId = new ObservableField<>("");
        public final ObservableField<Boolean> SelectedDiskIsOnLine = new ObservableField<>(false);
    }


}
