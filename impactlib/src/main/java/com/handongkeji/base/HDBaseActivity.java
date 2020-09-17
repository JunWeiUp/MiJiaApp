package com.handongkeji.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Administrator on 2017/10/11 0011.
 */

public class HDBaseActivity extends AppCompatActivity {

    protected void goActivity(Class<?extends Activity> activityClass){
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    protected void goActivity(Class<?extends Activity> activityClass, Bundle bundle){
        Intent intent = new Intent(this, activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    protected String getText(TextView textView){
        return textView.getText().toString().trim();
    }
}
