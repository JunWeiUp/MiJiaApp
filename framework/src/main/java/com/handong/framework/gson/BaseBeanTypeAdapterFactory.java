package com.handong.framework.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.handong.framework.base.ResponseBean;

public class BaseBeanTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class raw = type.getRawType();
        if (type.getRawType() == ResponseBean.class) {
            return (TypeAdapter<T>) new BaseBeanTypeAdapter(gson, this,type);
        }
//        else if (type.getRawType()== BasePagingBean.class){
//            return (TypeAdapter<T>) new BasePagingBeanTypeAdapter<>(gson, this,type);
//        }
        return null;
    }
}
