package com.handongkeji.utils;

import android.os.Environment;
import android.util.Log;

import com.handongkeji.common.Constants;
import com.handongkeji.common.MD5Encoder;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 缓存工具类
 *
 * @author naibo-liao
 * @时间： 2013-1-4下午02:30:52
 */
public class ConfigCacheUtil {

    private static final String TAG = "ConfigCacheUtil";

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
    public static final int CONFIG_CACHE_ML_TIMEOUT = 1000 * 60 * 60 * 24 * 1; // 1天

    /**
     * 最大缓存时间
     */
    public static final int CONFIG_CACHE_MAX_TIMEOUT = 1000 * 60 * 60 * 24 * 7; // 7天

    public static List<File> fileList = new ArrayList<File>();

    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值

    private static File file;

    public static double fileSizeLong = 0;

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

    /**
     * 获取缓存
     *
     * @param url 访问网络的URL
     * @return 缓存数据
     */
    public static String getUrlCache(String url, ConfigCacheModel model) {
        if (url == null) {
            return null;
        }

        String result = null;
        String path = Constants.ENVIROMENT_DIR_CACHE + MD5Encoder.encode(url) + ".json";
        file = new File(path);
        fileList.add(file);
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

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {

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
    public static void setUrlCache(String url, String data) {
        if (Constants.ENVIROMENT_DIR_CACHE == null) {
            return;
        }
        File dir = new File(Constants.ENVIROMENT_DIR_CACHE);

        if (!dir.exists() && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir.mkdirs();
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
//        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
//            return;
//        }
        String path = Constants.ENVIROMENT_DIR_CACHE + MD5Encoder.encode(url) + ".json";
        File file = new File(path);
        try {
            // 创建缓存数据到磁盘，就是创建文件
            FileUtil.writeFile(file, data);
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e);
            e.printStackTrace();
        }
    }

    /**
     * 删除历史缓存文件 思路:在本缓存累类中用list集合将缓存的文件存起来， 在清除缓存时遍历此list集合，
     * 将文件取出后调用clearCache(cacheFile)进行删除
     */
    public static void clearCache() {
        File cacheFile = null;
        for (int i = 0; i < fileList.size(); i++) {
            cacheFile = fileList.get(i);
            clearCache(cacheFile);
        }

    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#0.0");
        switch (sizeType) {
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
        }
        return fileSizeLong;
    }


    /**
     * 删除历史缓存文件执行
     *
     * @param cacheFile
     */
    public static void clearCache(File cacheFile) {
        if (cacheFile == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                try {
                    File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/hulutan/cache/");
                    if (cacheDir.exists()) {
                        clearCache(cacheDir);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (cacheFile.isFile()) {
            cacheFile.delete();
        } else if (cacheFile.isDirectory()) {
            File[] childFiles = cacheFile.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                clearCache(childFiles[i]);
            }
        }
    }
}
