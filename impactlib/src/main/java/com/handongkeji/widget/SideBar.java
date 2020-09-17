package com.handongkeji.widget;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.handongkeji.utils.CommonUtils;


public class SideBar extends View {
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private int choose = -1;// ѡ��
	private Paint paint = new Paint();

	private TextView mTextDialog;
	private int textSize;
	private int distance;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SideBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		textSize = CommonUtils.sp2px(context, 13);
		distance = CommonUtils.dip2px(context, 32);
	}
	
	/**
	 * ��д�������
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// ��ȡ����ı䱳����ɫ.
		int height = getHeight()-distance;// ��ȡ��Ӧ�߶�
		int width = getWidth(); // ��ȡ��Ӧ���
		int singleHeight = height / b.length;// ��ȡÿһ����ĸ�ĸ߶�

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#54CB90"));  //默认为绿色
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(textSize);
			// ѡ�е�״̬
			if (i == choose) {
				paint.setColor(Color.parseColor("#000000"));//选中为黑色
				paint.setFakeBoldText(true);
			}
			// x��������м�-�ַ�����ȵ�һ��.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight+distance/2;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();// ���û���
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// ���y����
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);// ���y������ռ�ܸ߶ȵı���*b����ĳ��Ⱦ͵��ڵ��b�еĸ���.

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
//			setBackgroundResource(R.drawable.sidebar_background);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(b[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}

					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	/**
	 * ���⹫���ķ���
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * �ӿ�
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}