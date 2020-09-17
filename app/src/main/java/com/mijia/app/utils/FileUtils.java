package com.mijia.app.utils;

import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FileUtils {


    public static void openFileReader(Context context, String pathName)
    {

        HashMap<String, String> params = new HashMap<>();
        params.put("local", "true");
        JSONObject Object = new JSONObject();
        try
        {
            Object.put("pkgName",context.getApplicationContext().getPackageName());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        params.put("menuData",Object.toString());
        QbSdk.getMiniQBVersion(context);
        int ret = QbSdk.openFileReader(context, pathName, params, s -> Log.d("test","onReceiveValue,val ="+s));

    }

}
