package com.handongkeji.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author chengs
 * @PackageName: com.yidianxinxi.widget
 * @Create 2016/2/17   9:06
 * @Site http://www.handongkeji.com
 * @Copyrights 2016/1/28 handongkeji All rights reserved.
 */
public class DrawableRightTextView extends TextView {
    public DrawableRightTextView(Context context) {
        super(context);
    }

    public DrawableRightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableRightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableRight = drawables[2];
            if (drawableRight != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableRight.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
