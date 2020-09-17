package com.handongkeji.impactlib.util;

import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
 *
 * @ClassName:ViewHelper

 * @PackageName:com.handongkeji.impactlib.util

 * @Create On 2017/4/20 0020   17:23

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/4/20 0020 handongkeji All rights reserved.
 */
public class ViewHelper {

    private View view;
    private SparseArray<View> views = new SparseArray<>();
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ViewHelper(View view) {
        this.view = view;
    }


    public <T extends View> T getView(int viewid) {
        View view = views.get(viewid);

        if (view == null) {
            view = this.view.findViewById(viewid);
            if (view != null) {
                views.put(viewid, view);
            }
        }
        return (T) view;
    }


    public void setText(int viewid, String text) {
        TextView tv = getView(viewid);
        tv.setText(text);
    }

    public String getText(int textViewid){
        String text = "";
        TextView tv = getView(textViewid);
        text = tv.getText().toString().trim();
        return text;
    }

    public void setOnClickListener(int viewid, View.OnClickListener listener){
        View view = getView(viewid);
        view.setOnClickListener(listener);
    }


    public void setEditText(int viewid, String text) {
        EditText edt = getView(viewid);
        edt.setText(text);
        edt.setEnabled(false);
    }



}
