package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.handong.framework.account.AccountHelper;
import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.handong.framework.base.BaseBean;
import com.mijia.app.bean.TransOrderBean;
import com.mijia.app.bean.TransOrderResultBean;
import com.mijia.app.constants.DataService;

public class ServerTransOrderViewModel extends ViewModel {

    public final DataReduceLiveData<TransOrderBean> transOrderBean = new DataReduceLiveData<>();


    /**
     * 服务器转发指令
     * @param order
     */
    public void serverTransOrder(String order) {
        Api.getApiService(DataService.class).serverTranOrder(AccountHelper.getUserId(), order).subscribe(transOrderBean);
    }

    public final DataReduceLiveData<TransOrderResultBean> queryOrderResult = new DataReduceLiveData<>();

    /**
     * 查询指令转发结果
     * @param orderId
     */
    public void queryOrderResult(String orderId) {
        Api.getApiService(DataService.class).queryResult(orderId).subscribe(queryOrderResult);
    }


}
