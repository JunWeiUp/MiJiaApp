package com.nevermore.oceans.uits;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Administrator on 2017/11/6 0006.
 */

//作者：知乎用户
//        链接：https://www.zhihu.com/question/44269936/answer/193946701
//        来源：知乎
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public class KeyboardStatusDetector {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 100;
    private KeyboardVisibilityListener mVisibilityListener;

    boolean keyboardVisible = false;

    public KeyboardStatusDetector registerFragment(Fragment f) {
         registerView(f.getView());
        return this;
    }

    public KeyboardStatusDetector registerActivity(Activity a) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
        return this;
    }

    public KeyboardStatusDetector registerView(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                v.getWindowVisibleDisplayFrame(r);

                int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                    if (!keyboardVisible) {
                        keyboardVisible = true;
                        if (mVisibilityListener != null) {
                            mVisibilityListener.onVisibilityChanged(true);
                        }
                    }
                } else {
                    if (keyboardVisible) {
                        keyboardVisible = false;
                        if (mVisibilityListener != null) {
                            mVisibilityListener.onVisibilityChanged(false);
                        }
                    }
                }
            }
        });

        return this;
    }

    public KeyboardStatusDetector setmVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
        return this;
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }

}