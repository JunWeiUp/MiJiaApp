package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nevermore.oceans.R;


/**
 * Created by Administrator on 2017/9/23.
 */

public class XIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    protected ViewPager mViewPager;
    protected int position;
    protected float positionOffset;
    protected int positionOffsetPixels;
    protected int indicatorHeight = 10;
    protected Rect rect = new Rect();
    private Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private View lastSelectedTabView;

    private int colorTextNormal = Color.parseColor("#666666");
    private int colorTextSelected = Color.parseColor("#F5821F");


    //默认指示器为一条线
    private Indicatorface mIndicatorface = new Indicatorface() {
        @Override
        public void drawIndicator(Canvas canvas, Rect rect, Paint paint) {
            canvas.drawRect(rect, paint);
        }
    };

    protected TabViewDelegate tabViewDelegate = new TabViewDelegate() {
        @Override
        public View getTabView(int position) {
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
            textView.setTextSize(21);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setTextColor(colorTextNormal);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER);
            return textView;
        }

        @Override
        public int getTabTextViewId() {
            return android.R.id.text1;
        }
    };


    private int indicatorWidth;

    public XIndicator(Context context) {
        this(context, null);
    }

    public XIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XIndicator, 0, R.style.XIndicatorStyle);

        //指示器颜色
        int color = typedArray.getColor(R.styleable.XIndicator_indicatorColor, Color.parseColor("#FF222222"));
        indicatorPaint.setColor(color);

        //宽度比例
        indicatorWidth = (int) typedArray.getDimension(R.styleable.XIndicator_indicatorWidth, -1);

        //高度
        indicatorHeight = (int) typedArray.getDimension(R.styleable.XIndicator_indicatorHeight, 10);

        typedArray.recycle();

        setOrientation(HORIZONTAL);
    }


    public void setIndicatorface(Indicatorface mIndicatorface) {
        this.mIndicatorface = mIndicatorface;
    }

    public TabViewDelegate getTabViewDelegate() {
        return tabViewDelegate;
    }

    public void setTabViewDelegate(TabViewDelegate tabViewDelegate) {
        this.tabViewDelegate = tabViewDelegate;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        View child = getChildAt(position);
        if (child == null)
            return;


        int childMeasuredHeight = child.getMeasuredHeight();
        int measuredWidth = child.getMeasuredWidth();

        int left = (int) (child.getLeft() + measuredWidth * positionOffset);

        if (indicatorWidth == -1 || indicatorWidth > measuredWidth) {
            indicatorWidth = measuredWidth;
        }

        left += (measuredWidth - indicatorWidth) / 2;

        int top = childMeasuredHeight - indicatorHeight;
        int right = left + indicatorWidth;
        int bottom = childMeasuredHeight;

        rect.set(left, 0, right, bottom);
        //背景
        drawTabBg(canvas, rect, indicatorPaint);
        super.dispatchDraw(canvas);
        rect.set(left, top, right, bottom);

        //指示器
        drawIndicator(canvas, rect, indicatorPaint);

        Paint indicatorPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint1.setColor(Color.parseColor("#eeeeee"));
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), indicatorPaint1);//indicatorPaint
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
    }

    protected void drawIndicator(Canvas canvas, Rect rect, Paint paint) {

        if (mIndicatorface != null) {
            mIndicatorface.drawIndicator(canvas, rect, paint);
        }
    }

    protected void drawTabBg(Canvas canvas, Rect rect, Paint paint) {

    }

    public void setUpWithViewPager(ViewPager viewPager) {

        if (viewPager != null) {

            if (mViewPager != null) {
                mViewPager.removeOnPageChangeListener(this);
            }

            mViewPager = viewPager;
            mViewPager.addOnPageChangeListener(this);

            PagerAdapter adapter = mViewPager.getAdapter();
            if (adapter != null && tabViewDelegate != null) {
                removeAllViews();
                for (int i = 0; i < adapter.getCount(); i++) {
                    final int positon = i;
                    View tabView = tabViewDelegate.getTabView(i);
                    TextView tabTextView = (TextView) tabView.findViewById(tabViewDelegate.getTabTextViewId());
                    tabTextView.setText(adapter.getPageTitle(i));
                    tabTextView.setTextColor(colorTextNormal);
                    tabTextView.setPadding(0, 0, 0, 0);
                    tabView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mViewPager.setCurrentItem(positon);
                        }
                    });

                    LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    addView(tabView, params);
                }
                if (getChildAt(0) != null) {
                    View tabView = getChildAt(0);
                    TextView tabTextView = (TextView) tabView.findViewById(tabViewDelegate.getTabTextViewId());
                    tabTextView.setTextColor(colorTextSelected);
                    tabTextView.setTextSize(21);
                    tabView.setSelected(true);
                    lastSelectedTabView = tabView;
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.position = position;
        this.positionOffset = positionOffset;
        this.positionOffsetPixels = positionOffsetPixels;
        invalidate();

    }

    @Override
    public void onPageSelected(int position) {

        View childAt = getChildAt(position);
        if (childAt != null) {
            TextView tvSelected = (TextView) childAt.findViewById(tabViewDelegate.getTabTextViewId());
            tvSelected.setTextSize(21);
            tvSelected.setTextColor(colorTextSelected);
            childAt.setSelected(true);

            if (lastSelectedTabView != null) {
                lastSelectedTabView.setSelected(false);
                TextView tvUnSelected = (TextView) lastSelectedTabView.findViewById(tabViewDelegate.getTabTextViewId());
                tvUnSelected.setTextColor(colorTextNormal);
                tvUnSelected.setTextSize(21);
            }
            lastSelectedTabView = childAt;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface TabViewDelegate {

        View getTabView(int position);

        int getTabTextViewId();
    }


}
