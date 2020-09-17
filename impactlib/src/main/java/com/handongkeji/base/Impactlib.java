package com.handongkeji.base;

import android.content.Context;

import java.util.concurrent.atomic.AtomicReference;

public class Impactlib {

    Context context;
    private static AtomicReference<Impactlib> INSTANCE = new AtomicReference<>();

    public Impactlib() {
    }

    public static Impactlib getInstance(){
        for (;;){
            Impactlib impactlib = INSTANCE.get();
            if (impactlib != null) {
                return impactlib;
            }
            impactlib = new Impactlib();
            if (INSTANCE.compareAndSet(null,impactlib)) {
                return impactlib;
            }
        }
    }

    private void setContext(Context context){
        this.context = context;
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public static void init(Context context){
        getInstance().setContext(context);
    }

}
