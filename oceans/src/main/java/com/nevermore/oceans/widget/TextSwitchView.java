package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class TextSwitchView extends FrameLayout {

    public TextView tvSubject;
    public Switch switchView;


    public TextSwitchView(@NonNull Context context) {
        this(context,null);
    }

    public TextSwitchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextSwitchView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.text_switch_layout,this);
        tvSubject = (TextView) findViewById(R.id.tv_subject);
        switchView = (Switch) findViewById(R.id.switch_view);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextSwitchView);
        String string = typedArray.getString(R.styleable.TextSwitchView_textSuject);

        int textSize = typedArray.getDimensionPixelSize(R.styleable.TextSwitchView_textSubSize,20);
        if(!TextUtils.isEmpty(string)){
            tvSubject.setText(string);
        }
        tvSubject.getPaint().setTextSize(textSize);

        typedArray.recycle();
    }

    public void setSubjectText(String text){
        tvSubject.setText(text);
    }
}
