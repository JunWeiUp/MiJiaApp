package com.handongkeji.login;

import android.text.TextUtils;

import com.handongkeji.base.IBaseView;
import com.handongkeji.utils.RegexUtils;

/**
 * 手机号登录检查
 *
 * @ClassName:PhoneLoginType

 * @PackageName:com.handongkeji.login

 * @Create On 2017/10/19 0019   10:31

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/10/19 0019 handongkeji All rights reserved.
 */

public class PhoneLoginType implements ILoginType {

    @Override
    public boolean checkAccountPassword(String account, String password, IBaseView baseView) {

        if(TextUtils.isEmpty(account)){
            baseView.toastWarning("手机号不能为空");
            return false;
        }

        if(!RegexUtils.checkMobile(account)){
            baseView.toastWarning("手机号不正确");
            return false;
        }

        if(TextUtils.isEmpty(password)){
            baseView.toastWarning("密码不能为空");
            return false;
        }


        return true;
    }
}
