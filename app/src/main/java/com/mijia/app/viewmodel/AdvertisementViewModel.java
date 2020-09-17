package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.mijia.app.bean.AdvertisementBean;
import com.mijia.app.constants.DataService;

public class AdvertisementViewModel extends ViewModel {

    public DataReduceLiveData<AdvertisementBean> mReduceLiveData = new DataReduceLiveData<>();


    public void getAdvertisementData(){
        Api.getApiService(DataService.class).getAdvertisementPhoto().subscribe(mReduceLiveData);
    }

}
