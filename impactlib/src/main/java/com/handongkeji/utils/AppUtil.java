package com.handongkeji.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUtil {
	
	/* md5 加密 */
	static public String md5 (String str) {
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//Logger.w(TAG, e.getMessage());
		}
		if (algorithm != null) {
			algorithm.reset();
			algorithm.update(str.getBytes());
			byte[] bytes = algorithm.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				hexString.append(Integer.toHexString(0xFF & b));
			}
			return hexString.toString();
		}
		return "";
	}
	
	/* 首字母大�? */
	@SuppressLint("DefaultLocale")
	static public String ucfirst (String str) {
		if (str != null && str != "") {
			str  = str.substring(0,1).toUpperCase()+str.substring(1);
		}
		return str;
	}

	public static SharedPreferences getSharedPreferences (Context ctx) {
		return ctx.getSharedPreferences("com.app.demos.sp.global", Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getSharedPreferences (Service service) {
		return service.getSharedPreferences("com.app.demos.sp.global", Context.MODE_PRIVATE);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// 业务逻辑
	/* 获取 Message */
	static public BaseMessage getMessage (String jsonStr) {
		BaseMessage message = new BaseMessage();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonStr);
			if (jsonObject != null) {
				message.setReturnCode("returnCode");
				message.setInfo(jsonObject.getString("info"));
				message.setList(jsonObject.getString("list"));
				message.setTotal(jsonObject.getString("total"));
			}
		} catch (JSONException e) {
			return message;
			//throw new Exception("Json format error");
		} catch (Exception e) {
			//Logger.w(TAG, e.getMessage());
		}
		return message;
	}
	
	/* Model 数组转化�? Map 列表 */
	static public List<? extends Map<String,?>> dataToList (List<? extends BaseModel> data, String[] fields) {
		ArrayList<HashMap<String,?>> list = new ArrayList<HashMap<String,?>>();
		for (BaseModel item : data) {
			list.add((HashMap<String, ?>) dataToMap(item, fields));
		}
		return list;
	}
	
	/* Model 转化�? Map */
	static public Map<String,?> dataToMap (BaseModel data, String[] fields) {
		HashMap<String,Object> map = new HashMap<String,Object>();
		try {
			for (String fieldName : fields) {
				Field field = data.getClass().getDeclaredField(fieldName);
				field.setAccessible(true); // have private to be accessable
				map.put(fieldName, field.get(data));
			}
		} catch (Exception e) {
			//Logger.w(TAG, e.getMessage());
		}
		return map;
	}
	
	/* 判断int是否为空 */
	@SuppressLint("UseValueOf")
	static public boolean isEmptyInt (int v) {
		Integer t = new Integer(v);
		return t == null ? true : false;
	}
	
	/* 获取毫秒�? */
	public static long getTimeMillis () {
		return System.currentTimeMillis();
	}
	
	/* 获取耗费内存 */
	public static long getUsedMemory () {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		return total - free;
	}


	/**
	 * 返回手机home界面
	 * @param context
	 */
	public static void openHome(Context context){
		Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

		mHomeIntent.addCategory(Intent.CATEGORY_HOME);
		mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(mHomeIntent);
	}
}