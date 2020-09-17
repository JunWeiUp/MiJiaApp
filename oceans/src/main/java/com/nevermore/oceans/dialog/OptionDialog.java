package com.nevermore.oceans.dialog;

import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nevermore.oceans.R;

import java.util.List;

/**
 * Created by Administrator on 2018/2/6 0006.
 */

public class OptionDialog extends NiceDialog {

    public interface OnItemClickListener{
        void onClick(OptionDialog dialog, int position);
    }


    public OptionDialog setItems(final List<String> options, final OnItemClickListener listener){

        setLayoutId(R.layout.dialog_options);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setShowBottom(true);
        setConvertListener(new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, final BaseNiceDialog dialog) {


                Button btnCancel = holder.getView(R.id.btn_cancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                LinearLayout ll = holder.getView(R.id.ll_options);
                for (int i = 0; i < options.size(); i++) {
                    Button button = new Button(getContext());
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                    final int position = i;
                    if(listener!=null){
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onClick(OptionDialog.this,position);
                                dismiss();
                            }
                        });
                    }

                    button.setBackgroundResource(R.drawable.rect_white_4_radius);
                    button.setText(options.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,0,2);
                    ll.addView(button,params);
                }
            }
        });
        return this;
    }
}
