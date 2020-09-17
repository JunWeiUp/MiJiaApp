package com.nevermore.oceans.uits.content_check;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

/**
 * 非空条件
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/2 0002
 * @github https://github.com/XuNeverMore
 */

public class NonNullCondition extends BaseCondition {

    @Override
    public boolean match(IContentChecker.Body body) {
        return !TextUtils.isEmpty(body.getContent());
    }

    @Override
    public void showTips(IContentChecker.Body body) {
        //因为这里取得是body.getName()所以手机号或密码为空的提示，该类都适用
        ToastUtils.showShort(body.getName()+"不能为空！");
    }
}
