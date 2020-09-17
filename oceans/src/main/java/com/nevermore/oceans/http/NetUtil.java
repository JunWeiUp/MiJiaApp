package com.nevermore.oceans.http;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.nevermore.oceans.http.cache.CacheType;
import com.nevermore.oceans.http.okhttp.OkHttpEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工具类基于okhttp
 *
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/5 0005
 * @github https://github.com/XuNeverMore
 */

public class NetUtil {


    private static HttpEngine mHttpEngine;

    private String url;
    private Map<String, Object> params;
    private boolean cache;
    private int cacheType;
    private HttpEngine.JsonFilter mJsonFilter;

    private List<File> fileList;//上传文件


    public void setJsonFilter(HttpEngine.JsonFilter jsonFilter) {
        mJsonFilter = jsonFilter;
        mHttpEngine.setJsonFilter(jsonFilter);
    }

    private NetUtil() {

    }

    public static NetUtil getInstance() {
        if (mHttpEngine == null) {
            synchronized (NetUtil.class) {
                if (mHttpEngine == null) {
                    //初始化
                    init();
                }
            }
        }
        return new NetUtil();
    }

    private static void init() {
        mHttpEngine = new OkHttpEngine();
    }

    public NetUtil setCacheType(@CacheType.CacheInt int cacheType){
        this.cacheType = cacheType;
        return this;
    }


    public String getUrl() {
        return url;
    }

    public NetUtil setUrl(String url) {
        if(params!=null){
            params.clear();
        }
        setJsonFilterEnable(true);
        setCacheType(CacheType.CACHE_NONE);
        this.url = url;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * 设置参数
     * @param params
     */
    public NetUtil setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    /**
     * 添加参数
     * @param key
     * @param value
     * @return
     */
    public NetUtil addParams(String key, Object value){
        if(params==null){
            params = new HashMap<>();
        }
        if(TextUtils.isEmpty(url)){
            throw new NullPointerException("url is empty");
        }
        params.put(key,value);
        return this;
    }

    public NetUtil setJsonFilterEnable(boolean enable){
        if(!enable){
            mHttpEngine.setJsonFilter(null);
        }else {
            if(mJsonFilter!=null){
                mHttpEngine.setJsonFilter(mJsonFilter);
            }
        }
        return this;
    }

    /**
     * 添加上传的文件
     * @param file
     * @return
     */


    public NetUtil addFile(File file){
        if(fileList==null){
            fileList=new ArrayList<>();
        }
        LogUtils.d(file.length()+"B");
        fileList.add(file);
        return this;
    }

    public <T> void post(Class<T> clazz,ResponseCallback<T> callback){
        if(TextUtils.isEmpty(url)){
            throw new NullPointerException("url is empty");
        }
        mHttpEngine.post(url,params,clazz,callback,cacheType);
    }

    public <T> MutableLiveData<T> post(Class<T> clazz){
        if(TextUtils.isEmpty(url)){
            throw new NullPointerException("url is empty");
        }
        MutableLiveData<T> data = new MutableLiveData<>();
        BaseCallback<T> callback = new BaseCallback<>(data);
        mHttpEngine.post(url,params,clazz,callback,cacheType);
        return data;
    }


    public <T> void upload(Class<T> clazz,ResponseCallback<List<T>> callback){
        if(TextUtils.isEmpty(url)){
            throw new NullPointerException("url is empty");
        }
        mHttpEngine.uploadFile(url,params,fileList,clazz,callback);
    }

    public <T> MutableLiveData<List<T>> upload(Class<T> clazz){
        if(TextUtils.isEmpty(url)){
            throw new NullPointerException("url is empty");
        }
        MutableLiveData<List<T>> data = new MutableLiveData<>();
        BaseCallback<List<T>> callback = new BaseCallback<>(data);
        mHttpEngine.uploadFile(url,params,fileList,clazz,callback);
        return data;
    }
}
