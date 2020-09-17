package com.nevermore.oceans.dialog;

import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.SizeUtils;
import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2018/2/5 0005.
 */

public class ProgressDF extends BaseNiceDialog {


    private CharSequence message;

    public void setMessage(final CharSequence message) {
        this.message = message;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null){
            Window window = dialog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.dimAmount=0.5f;
        }
    }

    @Override
    public int intLayoutId() {
        setWidth(SizeUtils.dp2px(120));
        setHeight(SizeUtils.dp2px(120));
        return R.layout.dialog_progress;
    }

    @Override
    public void convertView(ViewHolder holder, BaseNiceDialog dialog) {
        if (message != null) {
            holder.setText(R.id.tv_message, message.toString());
        }
    }
}
