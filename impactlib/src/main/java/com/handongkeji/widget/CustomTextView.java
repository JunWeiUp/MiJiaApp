package com.handongkeji.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
