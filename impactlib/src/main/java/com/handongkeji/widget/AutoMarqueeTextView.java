package com.handongkeji.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class AutoMarqueeTextView extends TextView {

	public AutoMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AutoMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoMarqueeTextView(Context context) {
		super(context);
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}
}
