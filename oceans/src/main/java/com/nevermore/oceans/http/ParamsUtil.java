package com.nevermore.oceans.http;

import java.util.Map;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class ParamsUtil {

    public static String getCompleteUrl(String url, Map<String, Object> params) {

        StringBuilder sb = new StringBuilder(url);
        if(params!=null&&params.size()>0){
            sb.append("?");

            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key).toString()).append("&");
            }
        }
        return sb.toString();
    }

}
