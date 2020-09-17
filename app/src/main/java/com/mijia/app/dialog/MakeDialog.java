package com.mijia.app.dialog;

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

import com.blankj.utilcode.util.StringUtils;
import com.mijia.app.R;
import com.mijia.app.databinding.DialogMakeBinding;

public class MakeDialog extends Dialog {

    private DialogMakeBinding mMakeBinding;

    public MakeDialog(@NonNull Context context, String makeOne, String maketwo, String makeThree) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_make, null);
        setContentView(view);
        mMakeBinding = DataBindingUtil.bind(view);

        if (StringUtils.isEmpty(makeOne)) {
            mMakeBinding.tvBtnOne.setVisibility(View.GONE);
            mMakeBinding.lineOne.setVisibility(View.GONE);
        } else {
            mMakeBinding.tvBtnOne.setText(makeOne);
        }

        if (StringUtils.isEmpty(maketwo)) {
            mMakeBinding.tvBtnTwo.setVisibility(View.GONE);
            mMakeBinding.lineTwo.setVisibility(View.GONE);
        } else {
            mMakeBinding.tvBtnTwo.setText(maketwo);
        }

        if (StringUtils.isEmpty(makeThree)) {
            mMakeBinding.tvBtnThree.setVisibility(View.GONE);
            mMakeBinding.lineThree.setVisibility(View.GONE);
        } else {
            mMakeBinding.tvBtnThree.setText(makeThree);
        }

        mMakeBinding.tvBtnCancel.setOnClickListener(v -> {
            dismiss();
        });

        mMakeBinding.tvBtnOne.setOnClickListener(v -> {
            if (mMakeListener != null) {
                mMakeListener.make(1);
            }
            dismiss();
        });

        mMakeBinding.tvBtnTwo.setOnClickListener(v -> {
            if (mMakeListener != null) {
                mMakeListener.make(2);
            }
            dismiss();
        });

        mMakeBinding.tvBtnThree.setOnClickListener(v -> {
            if (mMakeListener != null) {
                mMakeListener.make(3);
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


    public interface OnClickMakeListener {
        void make(int index);
    }

    private OnClickMakeListener mMakeListener;

    public void setMakeListener(OnClickMakeListener makeListener) {
        mMakeListener = makeListener;
    }
}
