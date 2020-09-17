package com.mijia.app.ui.other.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mijia.app.R;
import com.mijia.app.databinding.DialogCreateFolderBinding;

/**
 * Created by Administrator on 2019/6/10.
 */

public class CreateNewFolderDialog extends Dialog {

    private DialogCreateFolderBinding mBinding;

    public CreateNewFolderDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_create_folder, null);
        setContentView(view);
        mBinding = DataBindingUtil.bind(view);
        mBinding.tvSure.setOnClickListener(v ->{
            if (mOnClickSureListener!=null) {
                if (StringUtils.isEmpty(mBinding.etFloderName.getText().toString())) {
                    ToastUtils.showShort("请输入新建文件夹名称");
                    return;
                }
                mOnClickSureListener.sure(mBinding.etFloderName.getText().toString());
            }

            dismiss();
        } );


        mBinding.tvCancel.setOnClickListener(view1 -> {
            dismiss();
        });

        InputFilter filter1 = (source, start, end, dest, dstart, dend) -> {
            // 判断是否输入空格
            if ("/".equals(source)) {
                return "";
            }
            return null;
        };


        mBinding.etFloderName.setFilters(new InputFilter[]{filter1});

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

    private onClickSureListener mOnClickSureListener;

    public void setOnClickSureListener(onClickSureListener onClickSureListener) {
        mOnClickSureListener = onClickSureListener;
    }

    public interface onClickSureListener{
        void sure(String inputName);
    }
}
