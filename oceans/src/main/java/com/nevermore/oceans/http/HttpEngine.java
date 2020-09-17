package com.nevermore.oceans.http;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public interface HttpEngine {


    /**
     * 每个接口的json回调
     * 用于过滤一些状态，如异地登录、token无效等
     */

    public interface JsonFilter{
        /**
         *
         * @param json 被处理的json
         * @return 是否回调
         */
        boolean handle(String json);
    }

    public void setJsonFilter(JsonFilter filter);


    public <T> void get(String url, Map<String, Object> params, Class<T> clazz, ResponseCallback<T> callback, int cacheType);


    public <T> void post(String url, Map<String, Object> params, Class<T> clazz, ResponseCallback<T> callback, int cacheType);


    public <T> void uploadFile(String url, Map<String, Object> params, List<File> files, final Class<T> clazz, ResponseCallback<List<T>> callback);

}
