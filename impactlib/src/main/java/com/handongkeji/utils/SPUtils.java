package com.handongkeji.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * 共享参数据工具类
 * @author apple
 *
 */
public class SPUtils {

	private static final String DEFAULT_SP="sys";


	public static SharedPreferences getDefaultSP(Context context){
		return getSharedPreferences(context,DEFAULT_SP);
	}

	public static SharedPreferences  getSharedPreferences(Context context,String name){
		SharedPreferences sp = context.getSharedPreferences(name,Context.MODE_PRIVATE);
		return sp;
	}

}
