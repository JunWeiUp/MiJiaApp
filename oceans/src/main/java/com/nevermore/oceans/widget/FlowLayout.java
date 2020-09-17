package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class FlowLayout extends ViewGroup {
    private int horizontalmargin = 10;
    private int verticalmargin = 10;
    private int childMaxHeight;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontalmargin = (int) typedArray.getDimension(R.styleable.FlowLayout_horizontalSpacing, 10);
        verticalmargin = (int) typedArray.getDimension(R.styleable.FlowLayout_verticalSpacing, 10);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        //item最大高度
        childMaxHeight = 0;
        //行数
        int lineCount = 1;

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int padding = paddingLeft + paddingRight;

        //当前行宽
        int columWidth = padding;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            //测量子view宽高，才能使用child.getMeasuredWidth();
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();

            if (measuredHeight > childMaxHeight) {
                childMaxHeight = measuredHeight;
            }

            columWidth = columWidth + measuredWidth + horizontalmargin;
            if (columWidth >= width) {
                lineCount++;
                columWidth = padding + measuredWidth + horizontalmargin;
            }
        }

        //flowlayout总高度
        int height = lineCount * childMaxHeight + (lineCount - 1) * verticalmargin + getPaddingTop() + getPaddingBottom();

        height = Math.max(height, 0);
        setMeasuredDimension(width, height);
    }


    private static final String TAG = "FlowLayout";

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int measuredWidth = getMeasuredWidth();


        int start = getPaddingLeft();
        int paddingRight = getPaddingRight();

        //第几行
        int lineIndex = 0;
        //行宽
        int lineWidth = start;

        for (int i = 0; i < getChildCount(); i++) {

            View child = getChildAt(i);
            int childMeasuredWidth = child.getMeasuredWidth();
            int childMeasuredHeight = child.getMeasuredHeight();


            if (lineWidth + childMeasuredWidth > measuredWidth) {//超出一行宽度换行
                lineIndex++;
                lineWidth = start;
            }

            int top = lineIndex * childMaxHeight + verticalmargin * lineIndex + (childMaxHeight - childMeasuredHeight) / 2 + getPaddingTop();
            int left = lineWidth;

            //摆放子view
            child.layout(left, top, left + childMeasuredWidth, top + childMeasuredHeight);

            lineWidth = lineWidth + childMeasuredWidth + horizontalmargin;

        }


    }


    private BaseAdapter mAdapter = null;

    public void setAdapter(final BaseAdapter adapter) {
        if (adapter == null) {
            return;
        }

        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        removeAllViews();
        int count = adapter.getCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                View view = adapter.getView(i, null, null);
                addView(view);
            }
        }

        mAdapter = adapter;
        mAdapter.registerDataSetObserver(observer);
    }

    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            setAdapter(mAdapter);
        }
    };
}
