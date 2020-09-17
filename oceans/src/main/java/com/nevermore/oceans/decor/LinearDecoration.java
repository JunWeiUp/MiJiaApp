package com.nevermore.oceans.decor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/7/20 0020.
 */
public class LinearDecoration extends RecyclerView.ItemDecoration {

    private int dividerSize = 5;
    private int dividerColor = 0xff999999;

    /*orientation {@link #HORIZONTAL} or {@link #VERTICAL}*/
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int orientation = 1;


    public LinearDecoration(int dividerSize, @ColorInt int dividerColor, int orientation) {
        this.dividerSize = dividerSize;
        this.dividerColor = dividerColor;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childAdapterPosition = parent.getChildAdapterPosition(view);

        if (childAdapterPosition >= starPoi) {
            if (orientation == VERTICAL) {
                outRect.top = dividerSize;
            } else {
                outRect.left = dividerSize;
            }
        }
    }

    private int starPoi = 1;

    public void setStarPoi(int starPoi) {
        this.starPoi = starPoi;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        Paint paint = new Paint();
        paint.setColor(dividerColor);
        if (childCount > 1) {
            if (orientation == VERTICAL) {

                for (int i = starPoi; i < childCount; i++) {
                    View childAt = parent.getChildAt(i);

                    int left = childAt.getLeft();
                    int measuredWidth = childAt.getMeasuredWidth();
                    int top = childAt.getTop();
//                    Rect rect = new Rect(left, top, left + measuredWidth, top+dividerSize);
                    Rect rect = new Rect(left, top-dividerSize, left + measuredWidth, top);
                    c.drawRect(rect, paint);
                }

            } else {

                for (int i = 1; i < childCount; i++) {
                    View childAt = parent.getChildAt(i);
                    int right = childAt.getLeft();
                    int left = right - dividerSize;
                    int top = childAt.getTop();
                    int bottom = childAt.getBottom();
                    Rect rect = new Rect(left, right, top, bottom);
                    c.drawRect(rect,paint);
                }
            }

        }

    }
}
