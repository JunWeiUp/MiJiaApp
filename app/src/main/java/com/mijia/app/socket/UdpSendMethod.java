package com.mijia.app.socket;

public class UdpSendMethod {


    //获取用户数据信息
    public static void getHomeData() {
        String json = "{\"userName\": \"test_user\", \"userId\": \"aaaaaaaaaa\" }";
        String testStr = UdpDataUtils.getData((byte) 0x01, 0, 0, json);
    }

    //获取文件列表信息
    public static void getFlieListByFloder() {
        String json = "{\"userName\": \"test_user\",\"userId\": \"aaaaaaaaaa\",\"gsName\": \"test_gs\", \"gsId\": \"bbbbbbbbbbbbbb\",\"diskName\": \"test_disk\",\"diskId\": \"cccccccccccc\", \"path\": \"\" }";
        String testStr = UdpDataUtils.getData((byte) 0x02, 0, 0, json);
    }

    //上传文件
    public static void uploadFile() {
        String json = "{\"userName\": \"test_user\",\"userId\": \"aaaaaaaaaa\",\"gsName\": \"test_gs\",\"gsId\": \"bbbbbbbbbbbbbb\",\"diskName\": \"test_disk\",\"diskId\": \"cccccccccccc\",\"fullpath\": \"/1.txt\",\"file\":\"......\"}";
        String testStr = UdpDataUtils.getData((byte) 0x03, 0, 0, json);
    }

    //下载文件
    public static void downLoadFule() {
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsName\": \"test_gs\",\n" +
                "\t\"gsId\": \"bbbbbbbbbbbbbb\",\n" +
                "\t\"diskName\": \"test_disk\",\n" +
                "\t\"diskId\": \"cccccccccccc\",\n" +
                "\t\"fullpath\": \"/1.txt\",\t\t\t//存储在密夹中的全路径名\n" +
                "\"index\":\"3\"\t\t\t\t//APP要获取的包序号,从1开始\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x04, 0, 0, json);
    }


    //获取通讯录列表
    public static void getContactList() {
        String json = "{\n" +
                "\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsId\": \"bbbbbbbbb\",\n" +
                "\t\"gsName\": \"test_gs\",\n" +
                "\t\"diskName\": \"test_disk\",\n" +
                "\t\"diskId\": \"ccccccccc\",\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x05, 0, 0, json);
    }

    //通讯录备份
    public static void contactBackUp() {
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsName\": \"test_gs\",\n" +
                "\t\"gsId\": \"bbbbbbbbbbbbbb\",\n" +
                "\t\"diskName\": \"test_disk\",\n" +
                "\t\"diskId\": \"cccccccccccc\",\n" +
                "\"file\":\"......\"\t\t\t\t//文件数据\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x06, 0, 0, json);
    }

    //通讯录下载
    public static void contactDownload() {
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsName\": \"test_gs\",\n" +
                "\t\"gsId\": \"bbbbbbbbbbbbbb\",\n" +
                "\t\"diskName\": \"test_disk\",\n" +
                "\t\"diskId\": \"cccccccccccc\",\n" +
                "\t\"fullpath\": \"/1.txt\",\t\t\t//存储在密夹中的全路径名\n" +
                "\"index\":\"3\"\t\t\t\t//APP要获取的包序号,从1开始\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x07, 0, 0, json);
    }


    //远程指令退出
    public static void quitMidun() {
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\"\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x08, 0, 0, json);
    }

    //密夹重命名
    public static void mijiaRename(){
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsId\": \"bbbbbbbbb\",\n" +
                "\t\"gsName\": \"test_gs\"\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x09, 0, 0, json);
    }

    //磁盘打开关闭
    public static void diskOpenClose(){
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsId\": \"bbbbbbbbb\",\n" +
                "\t\"gsName\": \"test_gs\"，\n" +
                "\t\"diskName\":\"test_disk\",\n" +
                "\t\"diskId\":\"ccccccccccc\",\n" +
                "\"cmdType\": \"open\"\t\t//命令类型   \"open\" or \"close\"\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x0A, 0, 0, json);
    }

    //文件操作
    public static void fileManager(){
        String json = "{\n" +
                "\t\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\",\n" +
                "\t\"gsId\": \"bbbbbbbbb\",\n" +
                "\t\"gsName\": \"test_gs\",\n" +
                "\t\"fileinfo\": {\n" +
                "\t\t\"fileCmd\": \"move\",\t//copy、rename、delete、new\n" +
                "\t\t\"objType\": \"file\",\t//folder\n" +
                "\t\t\"srcDisk\": \"disk1\",\n" +
                "\t\t\"srcDiskId\": \"ccccccccc\",\n" +
                "\t\t\"srcObj\": \"/local/test.txt\",\n" +
                "\t\t\"dstDisk\": \"disk2\",\t//delete、new无dst\n" +
                "\t\t\"dstDiskId\": \"dddddddd\",\n" +
                "\t\t\"dstObj\": \"test.txt\"\n" +
                "\t}\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x0B, 0, 0, json);
    }

    //心跳
    public static void heartData(){
        String json = "{\n" +
                "\"userName\": \"test_user\",\n" +
                "\t\"userId\": \"aaaaaaaaaa\"，\n" +
                "\"ip\":\"192.168.1.100\",\t\t//app的内网ip\n" +
                "\"port\":\"8888\"\t\t\t\t//app的内网port\n" +
                "}";
        String testStr = UdpDataUtils.getData((byte) 0x0C, 0, 0, json);
    }


}
