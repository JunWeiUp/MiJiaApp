package com.nevermore.oceans.uits.content_check;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

/**
 * 内容长度检查条件
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/2 0002
 * @github https://github.com/XuNeverMore
 */

public class LengthCondition implements IContentChecker.Condition{


    private int length;//最小长度

    public LengthCondition(int length) {
        this.length = length;
    }

    @Override
    public boolean match(IContentChecker.Body body) {
        return !TextUtils.isEmpty(body.getContent())&&body.getContent().length()>=length;
    }

    @Override
    public void showTips(IContentChecker.Body body) {

        ToastUtils.showShort(body.getName()+"长度不能少于"+length+"位");
    }
}
