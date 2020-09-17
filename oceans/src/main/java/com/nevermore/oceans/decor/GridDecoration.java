package com.nevermore.oceans.decor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/7/20 0020.
 */

public class GridDecoration extends RecyclerView.ItemDecoration {

    private int spanCount = 3;
    private int dividerSize = 5;
    private int dividerColor = 0xff999999;


    /**
     * {@link #setLastRowDivider(boolean)}
     */

    //showLastRowDivider = true有效最后一行是否画一整条分割线
    private boolean lastRowMathcParent = false;

    public GridDecoration(int spanCount, int dividerSize, int dividerColor) {
        this.spanCount = spanCount;
        this.dividerSize = dividerSize;
        this.dividerColor = dividerColor;
        if (spanCount <= 0) {
            throw new IllegalStateException("智障!spanCount可能<=0?");
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //vertical
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        //既然是Grid，分割线横竖都要画
        //如果item在最后一行则，不画横线
//            if (!isChildInLastRow(childAdapterPosition, spanCount, parent.getChildCount())) {
        outRect.bottom = dividerSize;
//            }

        //右边间隙
//        if (!isChildInLastColum(childAdapterPosition, spanCount)) {
            outRect.right = dividerSize;
//        }
    }

    /**
     * 盘算item是否在第一行
     *
     * @param childPosition
     * @param spanCount
     * @return
     */
    public static boolean isChildInFirstRow(int childPosition, int spanCount) {
        return childPosition / spanCount < 1;
    }


    /**
     * 设置最后一行分割线
     * @param lastRowMathcParent
     */
    public GridDecoration setLastRowDivider(boolean lastRowMathcParent) {
        this.lastRowMathcParent = lastRowMathcParent;
        return this;
    }

    /**
     * 判断item是否在最后一行
     *
     * @param childPosition
     * @param spanCount
     * @param totalCount
     * @return
     */
    public static boolean isChildInLastRow(int childPosition, int spanCount, int totalCount) {
        int last = totalCount % spanCount;
        int rowCount = totalCount / spanCount + (last == 0 ? 0 : 1);

        //求出最后一行之前所有item总数，如果比这个数小则不在最后一行
        int lastRowBeforCount = spanCount * (rowCount - 1);
        if (childPosition < lastRowBeforCount) {
            return false;
        }
        return true;
    }

    /**
     * 判断item是否在最后一列
     *
     * @param childPosition
     * @param spanCount
     * @return
     */
    public static boolean isChildInLastColum(int childPosition, int spanCount) {
        return childPosition % spanCount == spanCount - 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();

        Paint paint = new Paint();
        paint.setColor(dividerColor);
        for (int i = 0; i < childCount; i++) {
            //画横线
            View childAt = parent.getChildAt(i);
            if (true) {
                int left = childAt.getLeft();
                int right = childAt.getRight() + dividerSize;
                int top = childAt.getBottom();
                int bottom = top + dividerSize;
                Rect rect = new Rect(left, top, right, bottom);
                c.drawRect(rect, paint);
            }

            //画竖线
            if (true) {

                int left = childAt.getRight();
                int right = left + dividerSize;
                int top = childAt.getTop();
                int bottom = childAt.getBottom();
                Rect rect = new Rect(left, top, right, bottom);
                c.drawRect(rect, paint);
            }


            //最后一行画一整条分割线
            if (lastRowMathcParent ) {
                int left = parent.getLeft();
                int right = parent.getRight();

                int top = childAt.getBottom();
                int bottom = top + dividerSize;
                Rect rect = new Rect(left, top, right, bottom);
                c.drawRect(rect, paint);
            }
        }
    }

}