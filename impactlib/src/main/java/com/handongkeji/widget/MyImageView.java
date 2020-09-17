package com.handongkeji.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/8/12.
 */
public class MyImageView extends ImageView {

	int l = -1, t = -1, r = -1, b = -1;

	public MyImageView(Context context) {
		super(context);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected boolean setFrame(int l, int t, int r, int b) {

		this.l = l;
		this.r = r;

		if (this.t == -1) {
			this.t = t;

		}
		if (this.b == -1) {
			this.b = b;
		}

		Log.i("chengs", "执行啦  t=" + t + "   b=" + b);
		return super.setFrame(l, t, r, b + 200);

	}

	public void setSize(int t2, int b2) {
		Log.v("chengs", "setSize  b=" + b + "   b2=" + b2);
		setFrame(l, t + t2, r, b + b2);
	}

}
