package com.nevermore.oceans.decor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/8/22 0022.
 */

public class HLinearDecoration extends RecyclerView.ItemDecoration {

    private int dividerSize = 5;
    private int dividerColor = 0xff999999;

    /*orientation {@link #HORIZONTAL} or {@link #VERTICAL}*/
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation = 1;


    public HLinearDecoration(int dividerSize, @ColorInt int dividerColor) {
        this.dividerSize = dividerSize;
        this.dividerColor = dividerColor;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int childAdapterPosition = parent.getChildAdapterPosition(view);
        outRect.set(dividerSize, 0, 0, 0);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        Paint paint = new Paint();
        paint.setColor(dividerColor);

        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            int right = childAt.getLeft();
            int left = right - dividerSize;
            int top = childAt.getTop();
            int bottom = childAt.getBottom();
            Rect rect = new Rect(left, right, top, bottom);
            c.drawRect(rect, paint);
        }

//        View lastChild = parent.getChildAt(childCount - 1);
//        int top = lastChild.getTop();
//        int bottom = lastChild.getBottom();
//        int left = lastChild.getLeft()+lastChild.getMeasuredWidth();
//        int right = left+dividerSize;
//        Rect rect = new Rect(left, right, top, bottom);
//        c.drawRect(rect, paint);

    }
}
