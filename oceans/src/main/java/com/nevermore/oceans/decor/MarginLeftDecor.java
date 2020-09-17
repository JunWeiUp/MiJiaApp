package com.nevermore.oceans.decor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class MarginLeftDecor extends RecyclerView.ItemDecoration {

    private int left = 0;
    private int dividerHeight = 0;
    private final Paint paint;

    public MarginLeftDecor(int left, int dividerHeight, @ColorInt int color) {
        this.left = left;
        this.dividerHeight = dividerHeight;
        paint = new Paint();
        paint.setColor(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, dividerHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);

            int left = childAt.getLeft();
            int measuredWidth = childAt.getMeasuredWidth();
            int bottom = childAt.getBottom();
            Rect rect = new Rect(left + this.left, bottom - dividerHeight, left + measuredWidth, bottom);
            c.drawRect(rect, paint);
        }

    }
}
