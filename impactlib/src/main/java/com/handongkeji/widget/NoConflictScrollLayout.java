package com.handongkeji.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class NoConflictScrollLayout extends RelativeLayout {
	
	public NoConflictScrollLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public NoConflictScrollLayout(Context context, AttributeSet attributes) {
		super(context, attributes);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch(ev.getAction()){
			case MotionEvent.ACTION_DOWN:
				getParent().requestDisallowInterceptTouchEvent(true);
				break; 
			case MotionEvent.ACTION_UP:
				int y = (int)ev.getRawY();
				
				int height = getMeasuredHeight() + 100;
				
				//Log.e("TAG", "moveX:" + x + " moveY:" + y + " height:" + height);
				
				if(y > height){
					return false;
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				int y2 = (int)ev.getRawY();
				
				int height2 = getMeasuredHeight() + 100;
				
				//Log.e("TAG", "moveX:" + x2 + " moveY:" + y2 + " height:" + height2);
				
				if(y2 > height2){
					return false;
				}
				
				break;
			case MotionEvent.ACTION_CANCEL:
				getParent().requestDisallowInterceptTouchEvent(false);
				break;
		}
				
		return super.dispatchTouchEvent(ev);
	}
}
