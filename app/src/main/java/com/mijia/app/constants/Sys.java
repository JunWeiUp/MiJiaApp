package com.mijia.app.constants;

/**
 * Created by Administrator on 2019/6/4.
 */

public class Sys {
    /**
     * 首页导航index
     */
    public static final int HOME_PAGE = 0;
    public static final int DISK = 1;
    public static final int TRANS = 2;
    public static final int BACK_UP = 3;


    /**
     * 磁盘管理fragment跳转
     */
    public static final int PHOTO = 0;
    public static final int VIDEO = 1;
    public static final int FILE = 2;
    public static final int VOICE = 3;
    public static final int OTHER = 4;
    public static final int DISKHOME = 5;
    public static final int FLODER = 6;


    /**
     * 流量设置
     */
    public static final String FLOW_SHAREPREFERENCES = "flowsharepreferences";
    //是否同意流量备份通讯录
    public static final String IS_AGREE_BACKUP_CONTACTS = "isagreebackupcontacts";
    //是否同意流量回复通讯录
    public static final String IS_AGREE_DOWN_CONTACTS = "isagreedowncontacts";
    //是否同意流量下载文件
    public static final String IS_AGREE_DOWN_FILE = "isagreedownfile";
    //是否同意流量上传文件
    public static final String IS_AGREE_UPLOAD_FILE = "isagreeuploadfile";


    /**
     * 选择地址activity页面跳转 requseCode
     */
    public static final String SELECT_PATH = "selectedpath";
    public static final int SELECT_PATH_TO_COPY = 1; //复制地址
    public static final int SELECT_PATH_TO_MOVE = 2; //移动地址
    public static final int SELECT_PATH_TO_BACKUP_CONTACT = 3;//备份通讯录

}
