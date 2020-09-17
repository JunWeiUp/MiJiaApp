package com.mijia.app.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mijia.app.constants.Constants;

public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                int type2 = networkInfo.getType();
                String typeName = networkInfo.getTypeName();

                switch (type2) {
                    case 0://移动 网络    2G 3G 4G 都是一样的 实测 mix2s 联通卡
                        Constants.Net_Status=0;
                        break;
                    case 1: //wifi网络
                        Constants.Net_Status=1;
                        break;
                    case 9:  //网线连接
                        break;
                }
            } else {// 无网络
            }
        }

    }
}
