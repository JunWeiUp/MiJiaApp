package com.mijia.app.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.api.Api;
import com.handong.framework.api.DataReduceLiveData;
import com.mijia.app.bean.HeartBeatBean;
import com.mijia.app.constants.DataService;
import com.mijia.app.utils.NetworkUtils;

import java.net.SocketException;

public class HeartBeatViewModel extends ViewModel {


    public final DataReduceLiveData<HeartBeatBean> heartBeatBean = new DataReduceLiveData<>();


    public void heartBeat(String port) {
        String ip = "";
        try {
            ip = NetworkUtils.getLocalIPAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Api.getApiService(DataService.class)
                .heartBeat(StringUtils.isEmpty(AccountHelper.getUserId())?"a620ea2c0d010e43987fb0dd59a0832a":AccountHelper.getUserId(), ip, port).subscribe(heartBeatBean);
    }


}
