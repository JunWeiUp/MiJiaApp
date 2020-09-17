package com.handongkeji.login;

import android.text.TextUtils;

import com.handongkeji.base.IBaseView;
import com.handongkeji.utils.RegexUtils;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class IdCardLoginType implements ILoginType{

    @Override
    public boolean checkAccountPassword(String account, String password, IBaseView baseView) {


        if(TextUtils.isEmpty(account)){
            baseView.toastWarning("请输入身份证号");
            return false;
        }

        if(!RegexUtils.checkIdCard(account)){
            baseView.toastWarning("身份证号不正确");
            return false;
        }

        if(TextUtils.isEmpty(password)){
            baseView.toastWarning("密码不能为空");
            return false;
        }


        return true;
    }
}
