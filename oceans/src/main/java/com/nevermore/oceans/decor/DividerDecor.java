package com.nevermore.oceans.decor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/12/29 0029.
 */

public class DividerDecor extends RecyclerView.ItemDecoration {


    private float dividerHeight = 2;
    private float marginLeft = 0;
    private float marginRight = 0;
    private int dividerColor = 0xffdfdfdf;


    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DividerDecor(float dividerHeight, float marginLeft, float marginRight, int dividerColor) {
        this.dividerHeight = dividerHeight;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.dividerColor = dividerColor;
    }

    public DividerDecor() {

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {


        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            float top = childAt.getBottom();
            float left = i == childCount - 1 ? 0 : marginLeft;
            float right = i == childCount - 1 ? childAt.getRight():childAt.getRight() - marginRight ;
            float bottom = top + dividerHeight;
            paint.setColor(0xffffffff);
            if (i!=(childCount-1)) {
                c.drawRect(0,top,childAt.getRight(),bottom,paint);
            }
            paint.setColor(dividerColor);
            if (i!=(childCount-1)) {
                c.drawRect(new RectF(left, top, right, bottom), paint);
            }

        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, (int) dividerHeight);
    }
}
