package com.handongkeji.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handongkeji.impactlib.R;


/**
 * Created by Administrator on 2016/12/28.
 */
public class MyTitleLayout extends RelativeLayout implements View.OnClickListener {

    private TextView title;
    private ImageView backImage;
    private TextView tvRight;
    private Space space;
    private View.OnClickListener backClickListener;
    private View.OnClickListener rightClickListener;

    private CharSequence titleText;

    public MyTitleLayout(Context context) {
        super(context);
    }

    public MyTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTitleLayout);
        titleText = typedArray.getString(R.styleable.MyTitleLayout_title_text);
        boolean rightTextVisible = typedArray.getBoolean(R.styleable.MyTitleLayout_right_text_visible, true);
        String rightText = typedArray.getString(R.styleable.MyTitleLayout_right_text);
        Drawable drawable = typedArray.getDrawable(R.styleable.MyTitleLayout_right_drawable);
        int dimensionPixelSize = typedArray.getDimensionPixelSize(R.styleable.MyTitleLayout_right_drawable_padding, 0);
        boolean backImageVisible = typedArray.getBoolean(R.styleable.MyTitleLayout_backImageVisible, true);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_my_title, this);
        title = findViewById(R.id.title);
        backImage = findViewById(R.id.back_image);
        tvRight = findViewById(R.id.tv_right);

        backImage.setVisibility(backImageVisible ? View.VISIBLE : View.GONE);
        backImage.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        title.setText(titleText);
        title.requestFocus();
        tvRight.setVisibility(rightTextVisible ? View.VISIBLE : View.GONE);
        tvRight.setText(rightText);

        if (drawable != null) {
            tvRight.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        tvRight.setCompoundDrawablePadding(dimensionPixelSize);

        float p = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        setPadding(0, (int) (p + 0.5), 0, 0);
        setBackgroundResource(R.drawable.daohang_bg);
    }

    public void setRightText(CharSequence text, View.OnClickListener listener) {
        tvRight.setText(text);
        if (listener != null) {
            tvRight.setOnClickListener(listener);
        }
    }

    public void setRightTextVisibility(int visibility) {
        tvRight.setVisibility(visibility);
    }

    public void setRightClickListener(View.OnClickListener rightClickListener) {
        this.rightClickListener = rightClickListener;
    }

    public void setTitle(CharSequence titleText) {
        title.setText(titleText);
    }

    public void setRightText(CharSequence text) {
        tvRight.setText(text);
        tvRight.setVisibility(View.VISIBLE);
    }

    public void setRightText(int textId) {
        tvRight.setText(textId);
    }


    public void setRightTextOnClickListener(View.OnClickListener listener) {
        tvRight.setOnClickListener(listener);
    }

    public void setBackClickListener(View.OnClickListener listener) {
        this.backClickListener = listener;
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        rightClickListener = l;
    }

    //  隐藏返回按钮
    public void hideBackImage() {
        backImage.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_image) {
            if (backClickListener != null) {
                backClickListener.onClick(view);
            } else {
                ((Activity) getContext()).onBackPressed();
            }
        } else if (id == R.id.tv_right) {
            if (rightClickListener != null) {
                rightClickListener.onClick(this);
            }
        }
    }
}
