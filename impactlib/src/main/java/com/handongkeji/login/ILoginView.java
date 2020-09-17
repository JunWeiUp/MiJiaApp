package com.handongkeji.login;

import com.handongkeji.base.IBaseView;

/**
 * Created by Administrator on 2017/10/19 0019.
 */

public interface ILoginView extends IBaseView {

    void onLoginSuccess();

    void onAccountBlocked();

}
