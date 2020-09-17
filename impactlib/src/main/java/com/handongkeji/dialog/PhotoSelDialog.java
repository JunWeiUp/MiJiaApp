package com.handongkeji.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handongkeji.impactlib.R;
import com.handongkeji.impactlib.dialog.XDialog;
import com.handongkeji.utils.DisplayUtil;


/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class PhotoSelDialog extends XDialog {
    LinearLayout llTake;
    TextView tvWoman;
    Button btnCancle;
    LinearLayout llOut;
    public enum Gravity{
         CENTER,BOTTOM;
    }

    public PhotoSelDialog(Context context,Gravity gravity) {
        super(context, R.layout.dialog_take_photo);

        if(Gravity.BOTTOM.equals(gravity)){
            getWindow().setWindowAnimations(R.style.AnimBottom);
            getWindow().setGravity(android.view.Gravity.BOTTOM);
        }
//        else {
//            int windowWidth = DisplayUtil.getWindowWidth(getContext());
//            setWidthAndHeight((int) (0.6*windowWidth), WindowManager.LayoutParams.WRAP_CONTENT);
//        }

        TextView tvTake = (TextView) findViewById(R.id.tv_take);
        TextView tvAblum = (TextView) findViewById(R.id.tv_album);
        TextView tvCancel = (TextView) findViewById(R.id.tv_cancel);

        tvTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.takePhoto();
                }
                dismiss();
            }
        });
        tvAblum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.selectPhoto();
                }
                dismiss();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    public interface OnSelectListener{
        void takePhoto();

        void selectPhoto();
    }

    private OnSelectListener listener;

    public void setClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
