package com.mijia.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.handong.framework.api.Api;
import com.handongkeji.utils.GlideImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.mijia.app.constants.Url;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

/**
 * Created by Administrator on 2019/6/3.
 */

public class MyApp extends Application {

    private BoxStore boxStore;
    private static MyApp instance;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) -> {
            layout.setPrimaryColorsId(R.color.white, R.color.colorPrimary);//全局设置主题颜色
            return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
    }

    private String TAG = MyApp.class.getSimpleName();


    public static MyApp getInstance() {
        return instance;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Api.config(Url.BASE_URL);
        instance = this;
        initImagePicker();//图片选择加载器初始化
        initDB();
        initFileViewer();
        initBugly();
        //解决9.0对非官方公开API 方法或接口有弹窗提示
        if (Build.VERSION.SDK_INT >= 28) {
            //你再调用。。。。。
            closeAndroidPDialog();
        }


    }


    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "ee8ef26587", false);
    }

    private void initFileViewer() {
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.e("apptbs", " onViewInitFinished is " + b);
            }
        };

        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public void initDB() {
        boxStore = MyObjectBox.builder().androidContext(this).build();
        if (BuildConfig.DEBUG) {
            boolean start = new AndroidObjectBrowser(boxStore).start(this);
            System.out.println("--start " + start);
        }
        System.out.println("--- 11 start ");
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }
}
