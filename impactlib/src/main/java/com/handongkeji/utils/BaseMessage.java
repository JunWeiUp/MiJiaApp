package com.handongkeji.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BaseMessage {
	
	private String returnCode;
	private String message;
	private String list;
	private String total;
	private String info;
	private Map<String, BaseModel> resultMap;
	private Map<String, ArrayList<? extends BaseModel>> resultList;
	
	public BaseMessage () {
		this.resultMap = new HashMap<String, BaseModel>();
		this.resultList = new HashMap<String, ArrayList<? extends BaseModel>>();
	}
	
	@Override
	public String toString () {
		return returnCode + " | " + message + " | " + list;
	}
	
	
	
	public Object getResult (String modelName) throws Exception {
		Object model = this.resultMap.get(modelName);
		// catch null exception
		if (model == null) {
			throw new Exception("Message data is empty");
		}
		return model;
	}
	
	public ArrayList<? extends BaseModel> getResultList (String modelName) throws Exception {
		ArrayList<? extends BaseModel> modelList = this.resultList.get(modelName);
		// catch null exception
		if (modelList == null || modelList.size() == 0) {
			throw new Exception("Message data list is empty");
		}
		return modelList;
	}
	
	@SuppressWarnings("unchecked")
	public void setResult (String result) throws Exception {
		this.list = result;
		if (result.length() > 0) {
			JSONObject jsonObject = null;
			jsonObject = new JSONObject(result);
			Iterator<String> it = jsonObject.keys();
			while (it.hasNext()) {
				// initialize
				String jsonKey = it.next();
				String modelName = getModelName(jsonKey);
				String modelClassName = "com.apa.kt56.model." + modelName;
				JSONArray modelJsonArray = jsonObject.optJSONArray(jsonKey);
				// JSONObject
				if (modelJsonArray == null) {
					JSONObject modelJsonObject = jsonObject.optJSONObject(jsonKey);
					if (modelJsonObject == null) {
						throw new Exception("Message result is invalid");
					}
					this.resultMap.put(modelName, json2model(modelClassName, modelJsonObject));
				// JSONArray
				} else {
					ArrayList<BaseModel> modelList = new ArrayList<BaseModel>();
					for (int i = 0; i < modelJsonArray.length(); i++) {
						JSONObject modelJsonObject = modelJsonArray.optJSONObject(i);
						modelList.add(json2model(modelClassName, modelJsonObject));
					}
					this.resultList.put(modelName, modelList);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private BaseModel json2model (String modelClassName, JSONObject modelJsonObject) throws Exception {
		// auto-load model class
		BaseModel modelObj = (BaseModel) Class.forName(modelClassName).newInstance();
		Class<? extends BaseModel> modelClass = modelObj.getClass();
		// auto-setting model fields
		Iterator<String> it = modelJsonObject.keys();
		while (it.hasNext()) {
			String varField = it.next();
			String varValue = modelJsonObject.getString(varField);
			Field field = modelClass.getDeclaredField(varField);
			field.setAccessible(true); // have private to be accessable
			field.set(modelObj, varValue);
		}
		return modelObj;
	}
	
	private String getModelName (String str) {
		String[] strArr = str.split("\\W");
		if (strArr.length > 0) {
			str = strArr[0];
		}
		return AppUtil.ucfirst(str);
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}