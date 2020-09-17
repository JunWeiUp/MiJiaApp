package com.handong.framework.gson;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.handong.framework.base.ResponseBean;

import java.io.IOException;

public class BaseBeanTypeAdapter<T,K> extends TypeAdapter<ResponseBean<T>> {

    private Gson gson;
    private BaseBeanTypeAdapterFactory factory;
    private TypeToken<K> type;

    public BaseBeanTypeAdapter(Gson gson, BaseBeanTypeAdapterFactory factory,TypeToken<K> type) {
        this.gson = gson;
        this.factory = factory;
        this.type = type;

    }

    @Override
    public void write(JsonWriter out, ResponseBean<T> value) throws IOException {
        TypeAdapter<ResponseBean<T>> delegateAdapter = (TypeAdapter<ResponseBean<T>>)gson.getDelegateAdapter(factory, type);
        delegateAdapter.write(out, value);
    }

    @Override
    public ResponseBean<T> read(JsonReader in) throws IOException {

        TypeAdapter<JsonObject> adapter = gson.getAdapter(JsonObject.class);
        JsonObject jsonObject = adapter.read(in);
        int code = jsonObject.getAsJsonPrimitive("code").getAsInt();
        if (code != 200 && code != 1) {
            jsonObject.remove("data");
        }
        Log.i("Response ","-------------"+jsonObject.toString());
        TypeAdapter<ResponseBean<T>> delegateAdapter = (TypeAdapter<ResponseBean<T>>)gson.getDelegateAdapter(factory, type);
        return delegateAdapter.fromJson(jsonObject.toString());
    }
}
