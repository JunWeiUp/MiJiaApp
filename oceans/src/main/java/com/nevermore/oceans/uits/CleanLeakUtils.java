package com.nevermore.oceans.uits;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

public class CleanLeakUtils {

    /**
     * 修复InputMethodManager引起的内存泄漏  http://blog.csdn.net/sodino/article/details/32188809
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }
        InputMethodManager im = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im == null) {
            return;
        }
        String[] viewArray = {"mCurRootView", "mServedView", "mNextServedView"};
        Field field;
        Object fieldObject;
        for (String view : viewArray) {
            try {
                field = im.getClass().getDeclaredField(view);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fieldObject = field.get(im);
                if (fieldObject != null && fieldObject instanceof View) {
                    View v = (View) fieldObject;
                    if (v.getContext() == destContext) {
                        field.set(im, null);
                    } else {
                        break;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
