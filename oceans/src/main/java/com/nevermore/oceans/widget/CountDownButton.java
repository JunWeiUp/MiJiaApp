package com.nevermore.oceans.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/10/11 0011.
 */

public class CountDownButton extends AppCompatTextView {

    private long countDownInterval;
    private long millisInFuture;
    private MyCount timer;
    private OnCountListener mListener;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.CountDownButton);
        countDownInterval = typedArray.getInteger(R.styleable.CountDownButton_countDownInterval, 1) * 1000;
        millisInFuture = typedArray.getInteger(R.styleable.CountDownButton_millisInFuture, 60) * 1000;
        typedArray.recycle();

    }

    public void setTotalSecond(int second) {
        this.millisInFuture = second * 1000;
    }

    public void startCountDown() {
        if (timer != null && !timer.isFinished()) {
            return;
        }
        if (timer == null) {
            timer = new MyCount(millisInFuture, countDownInterval);
        }
        timer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    public void cancel() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setOnCountListener(OnCountListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountListener {
        void onTick(CountDownButton button, String second);

        void onFinish(CountDownButton button);
    }

    private class MyCount extends CountDownTimer {

        private static final String TAG = "MyCount";
        private boolean isFinished = true;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isFinished = false;
            String time = (millisUntilFinished / 1000) + "s";

            if (mListener != null) {
                mListener.onTick(CountDownButton.this, time);
            } else {
                CountDownButton.this.setText(time);
            }

        }

        @Override
        public void onFinish() {
            setEnabled(true);
            if (mListener != null) {
                mListener.onFinish(CountDownButton.this);
            } else {
                CountDownButton.this.setText(R.string.get_code);
            }
            isFinished = true;
        }

        public boolean isFinished(){
            return isFinished;
        }
    }


}