package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.mijia.app.bean.SettingBean;
import com.mijia.app.constants.DataService;

public class SettingViewModel extends ViewModel {

    public DataReduceLiveData<SettingBean> mSettingBean = new DataReduceLiveData<>();

    public void getSettingData(){
        Api.getApiService(DataService.class).settingData().subscribe(mSettingBean);
    }

}
