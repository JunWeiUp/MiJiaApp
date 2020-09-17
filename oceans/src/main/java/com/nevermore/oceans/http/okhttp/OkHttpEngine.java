package com.nevermore.oceans.http.okhttp;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.nevermore.oceans.http.HttpEngine;
import com.nevermore.oceans.http.ParamsUtil;
import com.nevermore.oceans.http.ResponseCallback;
import com.nevermore.oceans.http.cache.CacheType;
import com.nevermore.oceans.http.cache.CacheUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class OkHttpEngine implements HttpEngine {


    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private JsonFilter filter;


    public OkHttpEngine() {
        okHttpClient = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public void setJsonFilter(JsonFilter filter) {
        this.filter = filter;
    }

    @Override
    public <T> void get(String url, Map<String, Object> params, Class<T> clazz, ResponseCallback<T> callback, int cacheType) {

    }


    @Override
    public <T> void post(final String url, final Map<String, Object> params, final Class<T> clazz, final ResponseCallback<T> callback, final int cacheType) {

        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                String urlComplete = ParamsUtil.getCompleteUrl(url, params);
                LogUtils.d(urlComplete);

                String json = null;
                boolean connected = NetworkUtils.isConnected();
                if (!connected && cacheType == CacheType.CACHE_FOR_NO_NET) {
                    //无网络的时候获取缓存
                    json = CacheUtil.getCacheJson(cacheType, urlComplete);
                } else {
                    FormBody.Builder builder = new FormBody.Builder(Charset.forName("UTF-8"));
                    if (params != null) {
                        for (String key : params.keySet()) {
                            builder.add(key, params.get(key).toString());
                        }
                    }
                    FormBody body = builder.build();
                    //请求
                    Request request = new Request.Builder().post(body).url(url).build();
                    final Call call = okHttpClient.newCall(request);

                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        json = response.body().string();
                        //缓存json
                        if (cacheType == CacheType.CACHE_FOR_NO_NET) {
                            CacheUtil.saveJson(urlComplete, json);
                        }
                    }
                }

                LogUtils.d(json);
                if (clazz == null) {
                    return;
                }
                //过滤异常状态
                if (filter != null && filter.handle(json)) {
                    return;
                }

                if (TextUtils.isEmpty(json) || TextUtils.equals("null", json)) {
                    e.onError(new Throwable(""));
                } else {
                    try {
                        T t = gson.fromJson(json, clazz);
                        e.onNext(t);

                    } catch (Exception e1) {
                        LogUtils.d("解析错误");
                        e1.printStackTrace();
                    }
                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(T value) {
                        if (callback != null) {
                            callback.onSuccess(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        if (callback != null) {
                            callback.onFailed(0, e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public <T> void uploadFile(final String url, final Map<String, Object> params, final List<File> files, final Class<T> clazz, final ResponseCallback<List<T>> callback) {


        Observable.create(new ObservableOnSubscribe<List<T>>() {
            @Override
            public void subscribe(ObservableEmitter<List<T>> e) throws Exception {

                //文件参数
                MediaType fileType = MediaType.parse("application/octet-stream");
                List<T> list = new ArrayList<>();
                for (File f : files) {

                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    if (params != null) {
                        for (String key : params.keySet()) {
                            multipartBuilder.addFormDataPart(key, params.get(key).toString());
                        }
                    }
                    String completeUrl = ParamsUtil.getCompleteUrl(url, params);
                    LogUtils.d(completeUrl);
                    RequestBody requestBody = RequestBody.create(fileType, f);
                    multipartBuilder.addFormDataPart("file", f.getName(), requestBody);
                    Request.Builder builder = new Request.Builder();
                    final Request request = builder.url(url)
                            .post(multipartBuilder.build())
                            .build();
                    final Call call = okHttpClient.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        LogUtils.d(json);
                        //过滤异常状态
                        if (filter != null && filter.handle(json)) {
                            e.onError(new Throwable("connect server failed"));
                            return;
                        }

                        if (clazz == null) {
                            return;
                        }
                        T t = gson.fromJson(json, clazz);
                        list.add(t);
                    } else {
                        if (filter != null && filter.handle(null)) {
                            return;
                        }
                    }
                }
                e.onNext(list);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<T> value) {
                        if (callback != null) {
                            callback.onSuccess(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
