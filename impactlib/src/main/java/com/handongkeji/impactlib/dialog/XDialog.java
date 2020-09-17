package com.handongkeji.impactlib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.handongkeji.utils.DisplayUtil;


/**
 *
 * @ClassName:XDialog

 * @PackageName:com.handongkeji.impactlib.dialog

 * @Create On 2017/4/20 0020   17:23

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/4/20 0020 handongkeji All rights reserved.
 */
public class XDialog extends Dialog {


    private final Window window;
    protected final Context mContext;

    public XDialog(Context context, int layoutid) {
        super(context,android.R.style.Theme_Holo_Dialog);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        mContext = context;
        window = getWindow();
        this.setContentView(layoutid);

    }

    public void setDetaultSize(){
        int windowWidth = DisplayUtil.getWindowWidth(getContext());
        setWidthAndHeight((int) (0.8*windowWidth),WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public XDialog setText(int textViewid,String msg){
        TextView textView= (TextView) findViewById(textViewid);
        textView.setText(msg);
        return this;
    }

    public XDialog setOnClickListener(int viewId, View.OnClickListener l){
        findViewById(viewId).setOnClickListener(l);
        return this;
    }


    public void setWidthAndHeight(int width,int height){
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }

    public <T extends View> T getView(int viewid){
       return (T) findViewById(viewid);
    }


    protected void toast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void startActivity(Class<? extends Activity> clazz){
        getContext().startActivity(new Intent(getContext(),clazz));
    }

}
