package com.mijia.app.presenter;

public interface OnServerTransListener {

    void onTransSuccess(String order,byte[] data);


    void onFail(String order);

}
