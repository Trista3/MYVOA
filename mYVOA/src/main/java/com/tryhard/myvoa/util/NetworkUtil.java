package com.tryhard.myvoa.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtil {
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int NET_SERVICE_STATE_APPERROR = 0x10;
	public static final int NET_SERVICE_STATE_SERVERDOWN = 0x11;
	public static final int NET_SERVICE_STATE_UNCONNET = 0x12;

	private Context appContext;
	private ConnectivityManager mConnectivityManager;
	private static NetworkUtil mNetworkUtil;

	private NetworkUtil(Context context) {
		super();
		this.appContext = context;
		this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static NetworkUtil getInstance(Context context) {
		if (mNetworkUtil == null) {
			mNetworkUtil = new NetworkUtil(context);
		}
		return mNetworkUtil;
	}

	public boolean isNetworkConnected() {
		NetworkInfo mWifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
		return mWifi!=null&&mWifi.isConnected()&&ni != null && ni.isConnectedOrConnecting();
	}


	public boolean checkNetworkState() {
		if (isNetworkConnected())
			return true;
		else {
			showState(NET_SERVICE_STATE_UNCONNET);
			return false;
		}
	}

	public int getNetworkType() {
		int netType = 0;
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo.length() > 0) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	public void showState(int state) {
		switch (state) {
		case NET_SERVICE_STATE_APPERROR: {
			Toast.makeText(appContext, "程序出错，请重新打开", Toast.LENGTH_SHORT).show();
			break;
		}
		case NET_SERVICE_STATE_UNCONNET: {
			Toast.makeText(appContext, "您的网络连接已断开", Toast.LENGTH_SHORT).show();
			break;
		}
		case NET_SERVICE_STATE_SERVERDOWN: {
			Toast.makeText(appContext, "服务器开挂，请稍后再试", Toast.LENGTH_SHORT).show();
			break;
		}
		default:Toast.makeText(appContext, "网络有问题，请稍后再试", Toast.LENGTH_SHORT).show();
		}
	}

}
