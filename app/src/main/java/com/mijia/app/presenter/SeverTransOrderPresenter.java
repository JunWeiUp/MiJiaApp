package com.mijia.app.presenter;

import android.util.Base64;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.handong.framework.account.AccountHelper;
import com.mijia.app.bean.TransOrderBean;
import com.mijia.app.bean.TransOrderResultBean;
import com.mijia.app.constants.Url;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SeverTransOrderPresenter {

    private OnServerTransListener onServerTransListener;

    public SeverTransOrderPresenter(OnServerTransListener onServerTransListener) {
        this.onServerTransListener = onServerTransListener;
    }
    int count = 0;
    public void orderTrans(String order, String data) {
        OkHttpClient.Builder client = new OkHttpClient().newBuilder();//创建OkHttpClient对象。
        data = data.replaceAll(" ","%20")
                .replaceAll("\\\"","%22")
                .replaceAll("#","%23")
                .replaceAll("%","%25")
                .replaceAll("&","%26")
                .replaceAll("\\(","%28")
                .replaceAll("\\)","%29")
                .replaceAll("\\+","%2B")
                .replaceAll(",","%2C")
                .replaceAll("/","%2F")
                .replaceAll(":","%3A")
                .replaceAll(";","%3B")
                .replaceAll("<","%3C")
                .replaceAll("=","%3D")
                .replaceAll(">","%3E")
                .replaceAll("\\?","%3F")
                .replaceAll("@","%40")
                .replaceAll("\\\\","%5C")
                .replaceAll("\\|","%7C");
        String url = Url.BASE_URL + Url.SERVERTRANSORDER + "?" + "userId=" + AccountHelper.getUserId() + "&order=" + data;
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(httpLoggingInterceptor);
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .get()//传递请求体
                .build();

        client.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onServerTransListener.onFail(order);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json1 = response.body().string();
                    Log.i("ServerTrans", "指令===" + order + "转发json===" + json1);
                    TransOrderBean transOrderBean = new Gson().fromJson(json1, TransOrderBean.class);
                    if (transOrderBean != null && !StringUtils.isEmpty(transOrderBean.getOrderId())) {
                        count = 0;
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (count==20) {
                                    onServerTransListener.onFail(order);
                                    onServerTransListener.onTransSuccess(order,null);
                                    cancel();
                                    return;
                                }
                                OkHttpClient.Builder resultClient = new OkHttpClient.Builder();
                                String resulturl = Url.BASE_URL + Url.QUERYORDERRESULT + "?orderId=" + transOrderBean.getOrderId();

                                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                                resultClient.addInterceptor(httpLoggingInterceptor);
                                Request request = new Request.Builder()//创建Request 对象。
                                        .url(resulturl)
                                        .get()//传递请求体
                                        .build();

                                resultClient.build().newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            String json2 = response.body().string();
                                            Log.i("ServerTrans", "指令===" + order + "转发结果json===" + json2);
                                            TransOrderResultBean transOrderResultBean = new Gson().fromJson(json2,TransOrderResultBean.class);
                                            if (transOrderResultBean!=null&& !StringUtils.isEmpty(transOrderResultBean.getJsonResult())) {
                                                cancel();
                                                byte[] dataBytes= Base64.decode(transOrderResultBean.getJsonResult().getBytes(),0);
                                                onServerTransListener.onTransSuccess(order,dataBytes);
                                            }else{
                                                count++;
                                            }
                                        }else{
                                            count++;
                                        }
                                    }
                                });
                            }
                        },0,1000);
                    }
                }
            }
        });
    }

}
