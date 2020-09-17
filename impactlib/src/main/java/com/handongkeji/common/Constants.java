/**
 * ClassName: Constants.java
 * created on 2013-1-24
 * Copyrights 2013-1-24 hjgang All rights reserved.
 * site: http://t.qq.com/hjgang2012
 * email: hjgang@yahoo.cn
 */
package com.handongkeji.common;

import android.content.Context;

import java.io.File;

/**
 * @author chaiqingshan
 * @category 全局变量
 * @日期 2015-11-24
 * @时间 下午2:05:25
 * @年份 2015
 */
public final class Constants {

    public static String CONNECT_SERVER_FAILED = "连接服务器失败，请稍后重试...";
    public static final String JSON_EXCEPTION = "json异常";
    public static final String MESSAGE_LOADING = "加载中...";


    public static final String ERROR_PHONE_NUMBER = "手机号错误！";
    public static final String ERROR_IDENTIFY_CODE = "验证码错误！";

    public static final String DESIGNER_ID = "designerid";
    public static final String COMMODITY_ID = "commodityid";


    public static final String token = "token";
    public static final String status = "status";
    public static final String message = "message";


    public static final String ISPLAY="isplay";

    /**
     * 本地缓存目录
     */
    public static String CACHE_DIR;
    /**
     * 图片缓存目录
     */
    public static String CACHE_DIR_IMAGE;
    /**
     * 待上传图片缓存目录
     */
    public static String CACHE_DIR_UPLOADING_IMG;
    /**
     * 图片目录
     */
    public static String CACHE_IMAGE;
    /**
     * 图片名称
     */
    public static final String PHOTO_PATH = "handongkeji_android_photo";
    /**
     * 语音缓存目录
     */
    public static String CACHE_VOICE;

    public static void init(Context context) {
        CACHE_DIR = context.getCacheDir().getAbsolutePath();
        File file = new File(CACHE_DIR, "image");
        file.mkdirs();
        CACHE_IMAGE = file.getAbsolutePath();
        CACHE_DIR_IMAGE = CACHE_IMAGE;
        file = new File(CACHE_DIR, "temp");
        file.mkdirs();
        CACHE_DIR_UPLOADING_IMG = file.getAbsolutePath();
        file = new File(CACHE_DIR, "voice");
        file.mkdirs();
        CACHE_VOICE = file.getAbsolutePath();

        file = new File(CACHE_DIR, "html");
        file.mkdirs();
        ENVIROMENT_DIR_CACHE = file.getAbsolutePath();
    }

    public static String ENVIROMENT_DIR_CACHE;

    private Constants() {

    }



}
