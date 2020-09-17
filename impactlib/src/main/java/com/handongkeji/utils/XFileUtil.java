package com.handongkeji.utils;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import com.handongkeji.interfaces.DeleteFileCallback;
import com.handongkeji.interfaces.ICallback;
import com.handongkeji.interfaces.Stringable;

import java.io.File;

/**
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2017/7/20 0020
 * @github https://github.com/XuNeverMore
 */

public class XFileUtil {
    private static final String TAG = "XFileUtil";

    private static long fileSize = 0;


    /**
     * 获取目录大小
     *
     * @param file
     * @return
     */
    public static long getSizeOfDirectory(File file) {
        fileSize = 0;
        return getDirSize(file);
    }

    private static long getDirSize(File file) {

        if (file == null) {
            return 0;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                getDirSize(f);
            }
        } else {
            fileSize += file.length();
            Log.i(TAG, "size: " + file.length() + "\npath" + file.getAbsolutePath());
        }

        return fileSize;
    }


    /**
     * 格式化文件大小
     *
     * @param context
     * @param fileSize
     * @return
     */
    public static String formatFileSize(Context context, long fileSize) {
        return Formatter.formatFileSize(context, fileSize);
    }


    /**
     * 删除目录下的所有文件及文件夹下的文件
     *
     * @param directoryFile
     * @param callback
     */
    public static void delFilsInDir(File directoryFile, DeleteFileCallback callback) {
        deleteFilesInDirectory(directoryFile, callback);
        if (callback != null) {
            callback.deleteFinish();
        }
    }


    private static void deleteFilesInDirectory(File directoryFile, DeleteFileCallback callback) {

        if (directoryFile == null) {
            if (callback != null) {
                callback.deleteFinish();
            }
        }
        if (directoryFile.isDirectory()) {
            File[] files = directoryFile.listFiles();
            for (File file : files) {
                deleteFilesInDirectory(file, callback);
            }
        } else {
            if (directoryFile.canWrite()) {

                if (callback != null) {
                    callback.delete(directoryFile.getAbsolutePath());
                }
                directoryFile.delete();
            }
        }

    }

}
