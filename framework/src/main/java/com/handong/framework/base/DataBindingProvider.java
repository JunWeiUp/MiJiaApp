package com.handong.framework.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/1/26 0026.
 */

public interface DataBindingProvider {

     @LayoutRes
    int getLayoutRes();

     void initView(@Nullable Bundle savedInstanceState);

}
