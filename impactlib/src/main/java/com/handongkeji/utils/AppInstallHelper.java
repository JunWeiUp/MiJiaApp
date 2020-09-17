package com.handongkeji.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * APP是否安装工具类
 *
 * @ClassName:AppInstallHelper

 * @PackageName:com.xiaoweiqiye.utils

 * @Create On 2017/4/28 0028   11:24

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/4/28 0028 handongkeji All rights reserved.
 */

public class AppInstallHelper {

    /**
     * appshi否安装
     * @param context context
     * @param packageName：app包名
     * @return
     */
    public static boolean isAppInstalled(Context context,String packageName){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo!=null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 是否安装qq
     * 手机qq包名 com.tencent.mobileqq
     * @param context context
     * @return true 已安装反之
     */
    public static boolean isQQInstalled(Context context){
        return isAppInstalled(context,"com.tencent.mobileqq");
    }


    /**
     * 是否安装微信
     * 微信包名 "com.tencent.mm"
     * @param context
     * @return
     */
    public static boolean isWeiXinInstalled(Context context){
        return isAppInstalled(context,"com.tencent.mm");
    }


    /**
     * 是否安装支付宝
     * @param context
     * @return
     */
    public static boolean isZhifubaoInstalled(Context context){
        return isAppInstalled(context,"com.eg.android.AlipayGphone");
    }
}
