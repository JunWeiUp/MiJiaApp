package com.nevermore.oceans.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/10/16 0016.
 */

public class SelAllDelLayout extends FrameLayout {

    public RelativeLayout rlSelectAll;
    public TextView tvDelete;

    public SelAllDelLayout(@NonNull Context context) {
        this(context,null);
    }

    public SelAllDelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelAllDelLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.select_all_delete,this);

        rlSelectAll = (RelativeLayout) findViewById(R.id.rl_select_all);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
    }
}
