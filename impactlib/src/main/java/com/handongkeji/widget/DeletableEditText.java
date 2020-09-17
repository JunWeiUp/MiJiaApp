package com.handongkeji.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class DeletableEditText extends EditText {
	private Drawable dLeft;
	private Drawable dRight;
	private Rect rBounds;

	// 构�?�器
	public DeletableEditText(Context paramContext) {
		super(paramContext);
		initEditText();
	}

	public DeletableEditText(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initEditText();
	}

	public DeletableEditText(Context paramContext, AttributeSet paramAttributeSet,
                             int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		initEditText();
	}

	// 初始化edittext 控件
	private void initEditText() {
		setEditTextDrawable();
		addTextChangedListener(new TextWatcher() { // 对文本内容改变进行监�?
			public void afterTextChanged(Editable paramEditable) {
				
			}

			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
			}

			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				DeletableEditText.this.setEditTextDrawable();
			}
		});
	}

	// 控制图片的显�?
	private void setEditTextDrawable() {
		if (getText().toString().length() == 0) {
			setCompoundDrawables(this.dLeft, null, this.dRight, null);
		} else {
			setCompoundDrawables(this.dLeft, null, this.dRight, null);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.dRight = null;
		this.dLeft = null;
		this.rBounds = null;
	}

	// 添加触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		if ((this.dRight != null) && (paramMotionEvent.getAction() == 1)) {
			this.rBounds = this.dRight.getBounds();
			int i = (int) paramMotionEvent.getX();
			if (i > getRight() - 3 * this.rBounds.width()) {
				setText("");
				paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
			}
		}
		return super.onTouchEvent(paramMotionEvent);
	}

	// 设置显示的图片资�?
	@Override
	public void setCompoundDrawables(Drawable left,
                                     Drawable top, Drawable right,
                                     Drawable bottom) {
		if (right != null) {
			this.dRight = right;
			//TODO
			this.dRight.setBounds(1,1,36,36);
		}
		if(left != null){
			this.dLeft = left;
			//TODO
			this.dLeft.setBounds(1,1,36,36);
		}
		super.setCompoundDrawables(left, top,
				right, bottom);
	}
}
