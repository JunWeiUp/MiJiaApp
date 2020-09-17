package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.handong.framework.account.AccountHelper;
import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.mijia.app.bean.EmpowerListBean;
import com.mijia.app.constants.DataService;

public class EmpowerListViewModel extends ViewModel {


    public DataReduceLiveData<EmpowerListBean> mEporwerListBean = new DataReduceLiveData<>();

    public void getEmpowerList() {
        Api.getApiService(DataService.class).getEmpowerlist(AccountHelper.getUserId()).subscribe(mEporwerListBean);
    }


}
