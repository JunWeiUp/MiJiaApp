package com.nevermore.oceans.uits.content_check;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Created by Administrator on 2018/2/2 0002.
 */

public class PhoneNumCondition extends BaseCondition {


    @Override
    public boolean match(IContentChecker.Body body) {
        return RegexUtils.isMobileExact(body.getContent());
    }

    @Override
    public void showTips(IContentChecker.Body body) {
        ToastUtils.showShort("手机号不正确");
    }
}
