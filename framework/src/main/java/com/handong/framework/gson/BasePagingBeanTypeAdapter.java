//package com.handong.framework.gson;
//
//import com.anxinxiaoyuan.teacher.app.base.BasePagingBean;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.TypeAdapter;
//import com.google.gson.reflect.TypeToken;
//import com.google.gson.stream.JsonReader;
//import com.google.gson.stream.JsonWriter;
//
//import java.io.IOException;
//
//public class BasePagingBeanTypeAdapter<T, K> extends TypeAdapter<BasePagingBean> {
//
//    private Gson gson;
//    private BaseBeanTypeAdapterFactory factory;
//    private TypeToken<K> type;
//
//    public BasePagingBeanTypeAdapter(Gson gson, BaseBeanTypeAdapterFactory factory, TypeToken<K> type) {
//        this.gson = gson;
//        this.factory = factory;
//        this.type = type;
//
//    }
//
//    @Override
//    public void write(JsonWriter out, BasePagingBean value) throws IOException {
//        TypeAdapter<BasePagingBean> delegateAdapter = (TypeAdapter<BasePagingBean>) gson.getDelegateAdapter(factory, type);
//        delegateAdapter.write(out, value);
//    }
//
//    @Override
//    public BasePagingBean read(JsonReader in) throws IOException {
//        TypeAdapter<JsonObject> adapter = gson.getAdapter(JsonObject.class);
//        JsonObject jsonObject = adapter.read(in);
//        try {
//            int code = jsonObject.getAsJsonPrimitive("code").getAsInt();
//            if (code != 200) {
//                jsonObject.remove("data");
//                jsonObject.remove("extend");
//            }
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
//        TypeAdapter<BasePagingBean> delegateAdapter = (TypeAdapter<BasePagingBean>)gson.getDelegateAdapter(factory, type);
//        return delegateAdapter.fromJson(jsonObject.toString());
//    }
//}
