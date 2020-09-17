package com.nevermore.oceans.pagingload;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public abstract class BasePagingLoadDelegate extends FrameLayout implements IPagingLoadDelegate {

    protected IEvent mEvent;

    public BasePagingLoadDelegate(@NonNull Context context) {
        super(context);
    }

    public BasePagingLoadDelegate(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePagingLoadDelegate(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasePagingLoadDelegate(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IEvent getmEvent() {
        return mEvent;
    }

    public void setEvent(IEvent mEvent) {
        this.mEvent = mEvent;
    }

    protected void checkSetEvent(){
        if(mEvent==null){
            throw new IllegalStateException("has not set Event");
        }
    }

    public abstract void onStart();

}
