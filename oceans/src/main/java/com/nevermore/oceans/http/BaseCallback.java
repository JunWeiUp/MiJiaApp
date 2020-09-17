package com.nevermore.oceans.http;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by Administrator on 2018/4/20 0020.
 */

public class BaseCallback<T>  implements ResponseCallback<T> {


        private MutableLiveData<T> data ;

        public BaseCallback(MutableLiveData<T> data) {
            this.data = data;
        }

        @Override
        public void onSuccess(T t) {
            if(data!=null){
                data.setValue(t);
            }
        }

        @Override
        public void onFailed(int status, Throwable throwable) {

            if(data!=null){
                data.setValue(null);
            }
        }
    }
