package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.nevermore.oceans.R;


/**
 * 宽高固定比例
 *
 * @ClassName:PercentRelativeLayout
 * @PackageName:com.ixiangni.ui
 * @Create On 2017/7/26 0026   10:33
 * @Site:http://www.handongkeji.com
 * @author:xuchuanting
 * @Copyrights 2017/7/26 0026 handongkeji All rights reserved.
 */

public class PercentRelativeLayout extends RelativeLayout {

    private float widthSize;
    private float heightSize;
    private final int FIX_WIDTH = 0;
    private final int FIX_HEIGHT = 1;
    private int fix;

    public PercentRelativeLayout(Context context) {
        this(context, null);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PercentRelativeLayout);
        widthSize = typedArray.getFloat(R.styleable.PercentRelativeLayout_widthSize, 0);
        heightSize = typedArray.getFloat(R.styleable.PercentRelativeLayout_heightSize, 0);
        fix = typedArray.getInt(R.styleable.PercentRelativeLayout_fixFlag, 0);
        typedArray.recycle();
    }

    private static final String TAG = "PercentRelativeLayout";
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (widthSize != 0 && heightSize != 0) {
            if (fix == FIX_WIDTH) {
                heightMeasureSpec =
                        MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthMeasureSpec) * (heightSize / widthSize)), MeasureSpec.getMode(widthMeasureSpec));
            } else {
                widthMeasureSpec =
                        MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(heightMeasureSpec) * (widthSize / heightSize)), MeasureSpec.getMode(heightMeasureSpec));
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
