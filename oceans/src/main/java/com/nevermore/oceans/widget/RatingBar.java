package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nevermore.oceans.R;

/**
 * 评分条
 * @ClassName:RatingBar

 * @PackageName:com.nevermore.oceans.widget

 * @Create On 2017/10/16 0016   13:35

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/10/16 0016 handongkeji All rights reserved.
 */

public class RatingBar extends LinearLayout {

    private Drawable drawableChecked;
    private Drawable drawableNormal;
    private int curCount;
    private int maxCount;
    private int padding;
    private int size;

    public RatingBar(Context context) {
        this(context,null);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);

        drawableChecked = typedArray.getDrawable(R.styleable.RatingBar_starCheckedIcon);
        drawableNormal = typedArray.getDrawable(R.styleable.RatingBar_starNormalIcon);
        curCount = typedArray.getInt(R.styleable.RatingBar_starCurCount, 5);
        maxCount = typedArray.getInt(R.styleable.RatingBar_starMaxCount, 5);

        padding = (int) typedArray.getDimension(R.styleable.RatingBar_starPadding, 30);
        size = (int) typedArray.getDimension(R.styleable.RatingBar_starSize, 45);

        typedArray.recycle();

        initStar();
        refreshStar();
    }

    private void initStar() {
        for (int i = 0; i < maxCount; i++) {
            ImageView imageView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(size,size);
            params.setMargins(0,0,padding,0);
            addView(imageView,params);
        }
    }

    public void setCurrentStartCount(int count){
        if(count>maxCount){
            count=maxCount;
        }
        curCount = count;
        refreshStar();
    }

    private void refreshStar() {

        for (int i = 0; i < maxCount; i++) {
            ImageView child = (ImageView) getChildAt(i);
            if(i<curCount){
                child.setImageDrawable(drawableChecked);
            }else {
                child.setImageDrawable(drawableNormal);
            }
        }
    }
}
