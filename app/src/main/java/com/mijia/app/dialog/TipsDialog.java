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

import com.mijia.app.R;
import com.mijia.app.databinding.DialogTipsBinding;

public class TipsDialog extends Dialog {

    private DialogTipsBinding mTipsBinding;

    public TipsDialog(@NonNull Context context, String tips) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
        setContentView(view);
        mTipsBinding = DataBindingUtil.bind(view);
        mTipsBinding.tvTips.setText(tips);
        mTipsBinding.tvCancel.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.cancel();
            }
            dismiss();
        });
        mTipsBinding.tvSure.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.sure();
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
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        getWindow().setWindowAnimations(R.style.AnimBottom);
        getWindow().setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    private OnClickListener mClickListener;


    public void setClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface OnClickListener {
        void sure();

        void cancel();
    }
}
