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
import com.mijia.app.bean.FileBean;
import com.mijia.app.databinding.DialogTipsBinding;

import java.util.ArrayList;
import java.util.List;

public class TipReplaceDialog extends Dialog {

    private DialogTipsBinding mTipsBinding;
    private List<FileBean> sureList = new ArrayList<>();
    private int position = 0;

    public TipReplaceDialog(@NonNull Context context, List<FileBean> list) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
        setContentView(view);
        mTipsBinding = DataBindingUtil.bind(view);

        mTipsBinding.tvTips.setText("目标文件夹中已存在名为" + list.get(0).getName() + "的文件，是否替换？");

        mTipsBinding.tvCancel.setOnClickListener(v -> {
            if (position == list.size() - 1) {
                if (mClickListener != null) {
                    mClickListener.sure(sureList);
                }
                dismiss();
            } else {
                position++;
                if (position  == list.size()) {
                    if (mClickListener != null) {
                        mClickListener.sure(sureList);
                    }
                    dismiss();
                }else{
                    mTipsBinding.tvTips.setText("目标文件夹中已存在名为" + list.get(position).getName() + "的文件，是否替换？");
                }
            }
        });
        mTipsBinding.tvSure.setOnClickListener(v -> {

            if (position == list.size() - 1) {
                sureList.add(list.get(position));
                if (mClickListener != null) {
                    mClickListener.sure(sureList);
                }
                dismiss();
            } else {
                sureList.add(list.get(position));
                position++;
                if (position  == list.size()) {
                    if (mClickListener != null) {
                        mClickListener.sure(sureList);
                    }
                    dismiss();
                }else{
                    mTipsBinding.tvTips.setText("目标文件夹中已存在名为" + list.get(position).getName() + "的文件，是否替换？");
                }

            }


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
        void sure(List<FileBean> list);

        void cancel();
    }
}
