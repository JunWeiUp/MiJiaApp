package com.nevermore.oceans.uits.content_check;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

public class EquelsCondition implements IContentChecker.Condition{

    private CharSequence text;
    private CharSequence tips;

    public EquelsCondition(CharSequence text, CharSequence tips) {
        this.text = text;
        this.tips = tips;
    }

    @Override
    public boolean match(IContentChecker.Body body) {
        return TextUtils.equals(text,body.getContent());
    }

    @Override
    public void showTips(IContentChecker.Body body) {
        ToastUtils.showShort(tips);
    }
}
