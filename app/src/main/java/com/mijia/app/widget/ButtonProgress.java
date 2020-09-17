package com.mijia.app.widget;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mijia.app.R;

public class ButtonProgress extends FrameLayout {

    private DonutProgress mDonutProgress;
    private ImageView ivStatus;


    public ButtonProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.button_progress_layout, this);
        mDonutProgress = findViewById(R.id.progress);
        ivStatus = findViewById(R.id.iv_stutas);


    }

    public void setDonutProgress(float progress, float max) {
        mDonutProgress.setMax(max);
        mDonutProgress.setProgress(progress);

//        System.out.println("--------  " + progress + "  " + max);


    }

    public void setIvStatus(boolean isLoading) {
        if (isLoading) {
            ivStatus.setImageResource(R.drawable.cipan_jixu1);
        } else {
            ivStatus.setImageResource(R.drawable.cipan_zanting2);
        }
    }

}
