package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.handong.framework.base.BaseBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.constants.DataService;

public class HomeDataViewModel extends ViewModel {


    public DataReduceLiveData<HomeDataBean> mReduceLiveData = new DataReduceLiveData<>();

    public void getHomeData() {
        Api.getApiService(DataService.class).getHomeData(StringUtils.isEmpty(AccountHelper.getUserId())?"a620ea2c0d010e43987fb0dd59a0832a":AccountHelper.getUserId()).subscribe(mReduceLiveData);
    }


}
