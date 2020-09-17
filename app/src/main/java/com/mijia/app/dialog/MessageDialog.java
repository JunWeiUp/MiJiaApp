package com.mijia.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mijia.app.R;

public class MessageDialog extends Dialog {
    private TextView tvMessage;
    private TextView btnCancle;
    private TextView tvSure;
    private View view;

    public MessageDialog(Context context) {
        super(context);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20000000")));
        this.setContentView(R.layout.dialog_message);
        tvMessage = findViewById(R.id.tv_message);
        btnCancle = findViewById(R.id.tv_cancle);
        tvSure = findViewById(R.id.tv_sure);
        view = findViewById(R.id.view);
        btnCancle.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClick(tvSure);
            }
        });
        tvSure.setOnClickListener(v -> {
            dismiss();
            if (listener1 != null) {
                listener1.onClick(tvSure);
            }
        });
    }

    public MessageDialog setBtnCancleHint(boolean isHineCancle) {
        if (isHineCancle) {
            btnCancle.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 设置对话框的消息文本
     */
    public MessageDialog setMessage(String title) {
        tvMessage.setText(title);
        return this;
    }

    public MessageDialog setTvSure(String sure) {
        tvSure.setText(sure);
        return this;
    }

    public MessageDialog setTvCancle(String cancle) {
        btnCancle.setText(cancle);
        return this;
    }

    private View.OnClickListener listener1;

    public MessageDialog setSureListener(View.OnClickListener listener) {
        listener1 = listener;
        return this;
    }

    private View.OnClickListener listener;

    public MessageDialog setCancleListener(View.OnClickListener listener) {
        this.listener = listener;
        return this;
    }
}
