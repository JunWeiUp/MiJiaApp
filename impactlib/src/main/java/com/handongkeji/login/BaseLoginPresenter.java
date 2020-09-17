package com.handongkeji.login;

/**
 * Created by Administrator on 2017/10/19 0019.
 */

public abstract class BaseLoginPresenter {


    protected ILoginView mLoginView;

    protected ILoginType mLoginType;

    public BaseLoginPresenter(ILoginView loginView) {
        this.mLoginView = loginView;
        mLoginType = initLoginType();
    }

    protected  ILoginType initLoginType(){
        return new PhoneLoginType();
    }


    public void setLoginType(ILoginType loginType) {
        this.mLoginType = loginType;
    }

    public abstract void login(String account, String password);


    protected boolean checkAccountPassword(String account,String password){
        return mLoginType.checkAccountPassword(account,password,mLoginView);
    }

}
