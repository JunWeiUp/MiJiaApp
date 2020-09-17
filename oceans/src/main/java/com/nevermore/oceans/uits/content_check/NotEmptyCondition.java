package com.nevermore.oceans.uits.content_check;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class NotEmptyCondition implements IContentChecker.Condition {
    private Context context;
    private String tipText;

    public NotEmptyCondition(Context context, String tipText) {
        this.context = context;
        this.tipText = tipText;
    }

    public NotEmptyCondition(Context context) {
        this.context = context;
    }

    @Override
    public boolean match(IContentChecker.Body body) {
        return !TextUtils.isEmpty(body.getContent());
    }

    @Override
    public void showTips(IContentChecker.Body body) {
        CharSequence text = tipText;
        text = TextUtils.isEmpty(text) ? body.getName() + "" + context.getString(R.string.must_not_null) : text;
        ToastUtils.showShort(text);
    }
}
