package com.handongkeji.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WeatherUtil {
	public static String findId(Context c, String cityname){
		if(null==cityname||"".equals(cityname)) 
			return null;
	      try { 
              InputStreamReader inputReader = new InputStreamReader(c.getResources().getAssets().open("citycode.txt") ); 
              BufferedReader bufReader = new BufferedReader(inputReader);
              String line="";
              String[] str = new String[2];
              while((line = bufReader.readLine()) != null){
              	  str = line.split("=");
              	  if(str.length==2&&null!=str[1]&&!"".equals(str[1])&&cityname.equals(str[1])){
              		  //���ض�Ӧ���
              		  return str[0];
              	  }
              }
          } catch (Exception e) { 
              e.printStackTrace(); 
              return null;
          }
          return null;
	}
}
