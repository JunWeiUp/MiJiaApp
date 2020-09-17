package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.mijia.app.bean.LoginBean;
import com.mijia.app.constants.DataService;

public class LoginViewModel extends ViewModel {


    public DataReduceLiveData<LoginBean> mLoginBeanDataReduceLiveData = new DataReduceLiveData<>();

    public void login(String unioId) {
        Api.getApiService(DataService.class).login(unioId).subscribe(mLoginBeanDataReduceLiveData);
    }


}
