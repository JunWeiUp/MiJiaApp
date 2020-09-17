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
import com.mijia.app.databinding.DialogDelTipsBinding;

/**
 * Created by Administrator on 2019/6/13.
 */

public class DeleteTipsDialog extends Dialog {

    private DialogDelTipsBinding mBinding;

    public DeleteTipsDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_del_tips, null);
        setContentView(view);
        mBinding = DataBindingUtil.bind(view);
        mBinding.tvCancel.setOnClickListener(v -> dismiss());
        mBinding.tvSure.setOnClickListener(v -> {
            if (mOnClickDelListener!=null) {
                mOnClickDelListener.onDel();
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

    private onClickDelListener mOnClickDelListener;

    public void setOnClickDelListener(onClickDelListener onClickDelListener) {
        mOnClickDelListener = onClickDelListener;
    }

    public interface onClickDelListener{
        void onDel();
    }
}
