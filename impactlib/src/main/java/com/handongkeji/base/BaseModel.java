package com.handongkeji.base;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/19 0019.
 */

public class BaseModel implements IBaseModel {


    private String url;
    private Map<String,String> params;

    public BaseModel() {
    }

    public BaseModel(String url, Map<String, String> params) {
        this.url = url;
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public <T> void request(IResponse<T> response) {

    }
}
