package com.nevermore.oceans.http.cache;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;


/**
 * 缓存工具类
 *
 * @author naibo-liao
 * @时间： 2013-1-4下午02:30:52
 */
public class CacheUtil {

    private static String CACHE_DIR = null;

    //初始化缓存目录

    /**
     * 缓存目录权限问题查看下面文章
     * https://segmentfault.com/q/1010000007780108/a-1020000007788861
     *
     * @param context
     */
    public static void initCacheDir(Context context) {
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            CACHE_DIR = context.getExternalCacheDir().getAbsolutePath();
//            CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+context.getPackageName()+File.separator+"cache";
        } else {
            CACHE_DIR = context.getCacheDir().getAbsolutePath();
        }
        Log.i(TAG, "initCacheDir: " + CACHE_DIR);
    }

    private static final String TAG = "CacheUtil";

    /**
     * 30秒超时时间
     */
    public static final int CONFIG_CACHE_SO_SHORT_TIMEOUT = (int) (1000 * 60 * 0.5); // 30s

    /**
     * 1秒超时时间
     */
    public static final int CONFIG_CACHE_SHORT_TIMEOUT = 1000 * 60 * 5; // 5 分钟

    /**
     * 5分钟超时时间
     */
    public static final int CONFIG_CACHE_MEDIUM_TIMEOUT = 1000 * 3600 * 2; // 2小时

    /**
     * 中长缓存时间
     */
    public static final int CONFIG_CACHE_ML_TIMEOUT = 1000 * 60 * 60 * 24; // 1天

    /**
     * 最大缓存时间
     */
    public static final int CONFIG_CACHE_MAX_TIMEOUT = 1000 * 60 * 60 * 24 * 7; // 7天


    private static File file;


    /**
     * CONFIG_CACHE_MODEL_LONG : 长时间(7天)缓存模式 <br>
     * CONFIG_CACHE_MODEL_ML : 中长时间(12小时)缓存模式<br>
     * CONFIG_CACHE_MODEL_MEDIUM: 中等时间(2小时)缓存模式 <br>
     * CONFIG_CACHE_MODEL_SHORT : 短时间(5分钟)缓存模式 <br>
     * CONFIG_CACHE_MODEL_SO_SHORT :短时间(30s)缓存模式
     */
    public enum ConfigCacheModel {
        CONFIG_CACHE_MODEL_SHORT, CONFIG_CACHE_MODEL_MEDIUM, CONFIG_CACHE_MODEL_ML, CONFIG_CACHE_MODEL_LONG, CONFIG_CACHE_MODEL_SO_SHORT;
    }


    public static String getCacheJson(@CacheType.CacheInt int cacheType, String url) {
        String json = null;
        if (cacheType == CacheType.CACHE_FOR_NO_NET) {
            json = getUrlCache(MD5Encoder.encode(url), ConfigCacheModel.CONFIG_CACHE_MODEL_LONG);
        }
        return json;
    }

    public static void saveJson(String url, String json) {
        setUrlCache(MD5Encoder.encode(url), json);
    }

    /**
     * 获取缓存
     *
     * @param url 访问网络的URL
     * @return 缓存数据
     */
    private static String getUrlCache(String url, ConfigCacheModel model) {
        if (url == null) {
            return null;
        }

        String result = null;
        String path = CACHE_DIR + File.separator + MD5Encoder.encode(url);
        file = new File(path);
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
            // 1。如果系统时间是不正确的
            // 2。当网络是无效的,你只能读缓存
            if (model == ConfigCacheModel.CONFIG_CACHE_MODEL_SO_SHORT) {
                if (expiredTime < CONFIG_CACHE_SO_SHORT_TIMEOUT) {
                    result = FileUtil.readTextFile(file);
                    return result;
                }
            } else if (model == ConfigCacheModel.CONFIG_CACHE_MODEL_SHORT) {
                if (expiredTime > CONFIG_CACHE_SHORT_TIMEOUT) {
                    return null;
                }
            } else if (model == ConfigCacheModel.CONFIG_CACHE_MODEL_MEDIUM) {
                if (expiredTime > CONFIG_CACHE_MEDIUM_TIMEOUT) {
                    return null;
                }
            } else if (model == ConfigCacheModel.CONFIG_CACHE_MODEL_ML) {
                if (expiredTime > CONFIG_CACHE_ML_TIMEOUT) {
                    return null;
                }
            } else if (model == ConfigCacheModel.CONFIG_CACHE_MODEL_LONG) {
                if (expiredTime > CONFIG_CACHE_MAX_TIMEOUT) {
                    return null;
                }
            } else {
                if (expiredTime > CONFIG_CACHE_MAX_TIMEOUT) {
                    return null;
                }
            }

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                result = FileUtil.readTextFile(file);
            }
        } else {
            Log.i(TAG, "无缓存");
        }
        return result;
    }

    /**
     * 设置缓存
     *
     * @param data
     * @param url
     */
    private static void setUrlCache(String url, String json) {
        if (CACHE_DIR == null) {
            return;
        }
        File dir = new File(CACHE_DIR);

        if (!dir.exists() && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            boolean mkdirs = dir.mkdirs();
        }

        if (!TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            return;
        }
        String path = CACHE_DIR + File.separator + MD5Encoder.encode(url);
        File file = new File(path);
        try {
            // 创建缓存数据到磁盘，就是创建文件
            FileUtil.writeFile(file, json);
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e);
            e.printStackTrace();
        }
    }


    public static String getCacheSize() {
        if (CACHE_DIR == null) {
            return "0B";
        }
        long dirLength = FileUtils.getDirLength(CACHE_DIR);
        if(dirLength<0){
            return "0B";
        }

        return Formatter.formatFileSize(Utils.getApp(), dirLength);
    }


    /**
     * 删除历史缓存文件执行
     */
    public static void clearCache() {
        if (CACHE_DIR == null) {
            return;
        }
        FileUtils.deleteDir(CACHE_DIR);
    }
}
