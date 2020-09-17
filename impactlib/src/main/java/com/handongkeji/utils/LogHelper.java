package com.handongkeji.utils;

import android.util.Log;

/**
 * json太长 andorid studio 显示不全
 * 可用该类打印
 * @ClassName:LogHelper

 * @PackageName:com.handongkeji.utils

 * @Create On 2017/7/20 0020   15:22

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/7/20 0020 handongkeji All rights reserved.
 */

public class LogHelper {

    /**
     * 分段打印log
     * @param tag
     * @param message
     */
    public static void log(String tag,String message){
        if(CommonUtils.isStringNull(message)){
            return;
        }

        int index = message.length() / 1024;

        for (int i = 0; i < index; i++) {
            Log.i(tag, message.substring(i*1024,(i+1)*1024));
        }
        if(message.length()>index*1024){
            Log.i(tag, message.substring(index*1024,message.length()));
        }

    }
}
