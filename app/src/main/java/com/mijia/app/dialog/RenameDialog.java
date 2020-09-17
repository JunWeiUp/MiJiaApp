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
import com.blankj.utilcode.util.ToastUtils;
import com.mijia.app.R;
import com.mijia.app.databinding.DialogRenameBinding;

public class RenameDialog extends Dialog {

    private DialogRenameBinding mBinding;

    public RenameDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rename, null);
        setContentView(view);
        mBinding = DataBindingUtil.bind(view);
        mBinding.tvCancel.setOnClickListener(v -> dismiss());
        mBinding.tvSure.setOnClickListener(v -> {
            String content = mBinding.etFloderName.getText().toString();
            if (StringUtils.isEmpty(content)) {
                ToastUtils.showShort("请输入文件名称！");
                return;
            }
            if (content.length()>8) {
                ToastUtils.showShort("名称不能超过8位");
                return;
            }
            if (mOnClickSureListener != null) {
                mOnClickSureListener.sure(content);
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

    private OnClickSureListener mOnClickSureListener;


    public void setOnClickSureListener(OnClickSureListener onClickSureListener) {
        mOnClickSureListener = onClickSureListener;
    }

    public interface OnClickSureListener {
        void sure(String name);
    }

}
