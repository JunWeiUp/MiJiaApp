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
import com.mijia.app.databinding.DialogUploadFileBinding;

/**
 * Created by Administrator on 2019/6/5.
 */

public class UploadFileDialog extends Dialog {

    private DialogUploadFileBinding mBinding;
    private OnClickListener mOnClickListener;

    public final static int CREATE_FOLDER = 0;
    public final static int UPLOAD_PHOTO = 1;
    public final static int UPLOAD_VIDEO = 2;
    public final static int UPLOAD_FILE = 3;
    public final static int UPLOAD_VOICE = 4;
    public final static int UPLOAD_TOTHER = 5;

    public UploadFileDialog(@NonNull Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_upload_file, null);
        setContentView(view);
        mBinding = DataBindingUtil.bind(view);
        mBinding.setHandler(this);
    }

    public void onClick(View view) {
        if (mOnClickListener == null) {
            dismiss();
            return;
        }
        switch (view.getId()) {
            case R.id.tv_create_folder:
                mOnClickListener.onClick(CREATE_FOLDER);
                break;
            case R.id.tv_photo:
                mOnClickListener.onClick(UPLOAD_PHOTO);
                break;
            case R.id.tv_video:
                mOnClickListener.onClick(UPLOAD_VIDEO);
                break;
            case R.id.tv_file:
                mOnClickListener.onClick(UPLOAD_FILE);
                break;
            case R.id.tv_voice:
                mOnClickListener.onClick(UPLOAD_VOICE);
                break;
            case R.id.tv_other:
                mOnClickListener.onClick(UPLOAD_TOTHER);
                break;
            case R.id.iv_close:
                break;
            default:
                break;
        }
        dismiss();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(int type);
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
}
