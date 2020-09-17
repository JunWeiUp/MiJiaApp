package com.nevermore.oceans.decor;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/10/16 0016.
 */

public class TopDivider extends RecyclerView.ItemDecoration {


    private int dividerSize;

    public TopDivider(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {


        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(0,dividerSize,0,0);
    }
}
