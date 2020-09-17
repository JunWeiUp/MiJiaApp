package com.mijia.app.ui.disk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mijia.app.R;
import com.mijia.app.databinding.DialogCreateTimePingBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noman.weekcalendar.wheelview.WheelView;

/**
 * Created by Administrator on 2019/6/6.
 */

public class CreateTimePingDialog extends Dialog {
    private DialogCreateTimePingBinding mBinding;
    private OnSureListener mOnSureListener;

    public CreateTimePingDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_create_time_ping, null);
        setContentView(view);
        mBinding = DataBindingUtil.bind(view);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(i + "");
        }
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list1.add(i < 10 ? "0" + i : i + "");
        }
        mBinding.dayWheelview.setItems(list, 0);
        mBinding.dayWheelview.setOnItemSelectedListener((selectedIndex, item) -> {
            if (item.equals("7")) {
                mBinding.hourWheelview.setItems(Arrays.asList("00"), 0);
            }else{
                mBinding.hourWheelview.setItems(list1, 0);
            }
        });

        mBinding.hourWheelview.setItems(list1, 0);
        mBinding.hourWheelview.setOnItemSelectedListener((selectedIndex, item) -> {

        });

        mBinding.tvClose.setOnClickListener(v -> {
            if (mOnSureListener != null) {
                mOnSureListener.sure("0", "0");
            }
            dismiss();
        });
        mBinding.tvSure.setOnClickListener(v -> {
            if (mOnSureListener != null) {
                mOnSureListener.sure(mBinding.dayWheelview.getSelectedItem(), mBinding.hourWheelview.getSelectedItem());
            }
            dismiss();
        });
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        getWindow().setWindowAnimations(R.style.AnimBottom);
        getWindow().setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    public void setOnSureListener(OnSureListener onSureListener) {
        mOnSureListener = onSureListener;
    }

    public interface OnSureListener {
        void sure(String day, String hour);
    }
}
