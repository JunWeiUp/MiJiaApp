package com.handongkeji.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class HttpUtil {

	static public int CMWAP_INT = 1;
	static public int NET_INT = 2;
	static public int WIFI_INT = 3;
	static public int NONET_INT = 4;
	static public int CTWAP_INT = 5;
	
	// 电信3G
	public static String APN_TYPE_CTNET = "ctnet";
	public static String APN_TYPE_CTWAP = "ctwap";
	// 移动接入�?
	public static String APN_TYPE_CMNET = "cmnet";
	public static String APN_TYPE_CMWAP = "cmwap";
	// 联�?�接入点
	public static String APN_TYPE_UNINET = "uninet";
	public static String APN_TYPE_UNIWAP = "uniwap";
	public static String APN_TYPE_3GNET = "3gnet";
	public static String APN_TYPE_3GWAP = "3gwap";// 代理方式
	public static final byte PROXY_TYPE_CM = 0;
	public static final byte PROXY_TYPE_CT = 1;//电信代理
	public static final String PROXY_CT_WAP = "10.0.0.200";
	public static final String PROXY_CM_WAP = "10.0.0.172";
	
	/*
	 * 获取接入网络的方�?
	 */
	static public int getNetType (Context ctx) {
		// has network
		ConnectivityManager conn = null;
		try {
			conn = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			
		}
		if (conn == null) {
			return HttpUtil.NONET_INT;
		}
		NetworkInfo info = conn.getActiveNetworkInfo();
		// 添加时间2014�?8�?6�? 10:18:20
		if(info == null) {
			return HttpUtil.NONET_INT;
		}
		boolean available = info.isAvailable();
		if (!available){
			return HttpUtil.NONET_INT;
		}
		// check use wifi
		int type = info.getType();
		String extraInfo = info.getExtraInfo();
		if(extraInfo != null) {
			extraInfo = extraInfo.trim();
		}
		if (type == ConnectivityManager.TYPE_WIFI) {
			return HttpUtil.WIFI_INT;
		} else if (extraInfo!=null&&APN_TYPE_3GNET.equalsIgnoreCase(extraInfo)
			|| APN_TYPE_UNINET.equalsIgnoreCase(extraInfo)
			|| APN_TYPE_CTNET.equalsIgnoreCase(extraInfo)
			|| APN_TYPE_CMNET.equalsIgnoreCase(extraInfo)) {
				return HttpUtil.NET_INT;
		} else {
			@SuppressWarnings("deprecation")
            String M_APN_PROXY = android.net.Proxy.getDefaultHost();
			if (M_APN_PROXY != null && !"".equals(M_APN_PROXY)) {
				// 判断代理接入方式
				if (PROXY_CT_WAP.equals(M_APN_PROXY)) {//是否电信
					return HttpUtil.CTWAP_INT;
				} else {
					return HttpUtil.CMWAP_INT;
				}
			} else {//net方式
				return HttpUtil.NET_INT;
			}
		}
	}
	
}