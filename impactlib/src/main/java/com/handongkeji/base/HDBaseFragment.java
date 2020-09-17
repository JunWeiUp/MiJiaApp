package com.handongkeji.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/10/12 0012.
 */

public class HDBaseFragment extends Fragment {

    protected void goActivity(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        getContext().startActivity(intent);
    }

    private boolean isDestoryed = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isDestoryed = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDestoryed = true;
    }

    protected void toast(String message) {
        if (!isDestoryed) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean isDestoryed(){
        return isDestoryed;
    }
}
