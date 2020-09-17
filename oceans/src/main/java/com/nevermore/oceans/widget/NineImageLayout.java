package com.nevermore.oceans.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class NineImageLayout extends CellLayout implements CellLayout.OnNewLineCondition {


    public NineImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCondition(this);

    }


    @Override
    public boolean allowNewLine(int columCount, int childindex, int childCount) {
        if (columCount == 3 && childindex == 2 && childCount == 4) {
            return false;
        }
        return true;
    }

    @Override
    public boolean forceNewline(int columCount, int childindex, int childCount) {
        if (columCount == 3 && childindex == 1 && childCount == 4) {
            return true;
        }
        return false;
    }



    private static final String TAG = "NineImageLayout";




}
