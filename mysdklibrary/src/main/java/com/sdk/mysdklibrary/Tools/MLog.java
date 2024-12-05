package com.sdk.mysdklibrary.Tools;

import android.util.Log;

import com.sdk.mysdklibrary.MyGamesImpl;

public class MLog {
	private static String TAG = "MyZTSDK";
	private static boolean isLog = false;
	private static boolean debug = false;
	private static boolean error = true;
	private static boolean forceDebug = false;
	static {
		try{
			forceDebug = MyGamesImpl.getSharedPreferences().getBoolean("isOpenDebug",false);
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("MLog---:"+e.getMessage());
		}
	}
	public static void setDebug(boolean debug) {
		MLog.debug = debug;
		MLog.isLog = debug;
	}

	private static boolean isDebug(){
		return isLog||debug||forceDebug;
	}

	public static void d(String tag, String msg) {
		if (isDebug()) {
			Log.i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (isDebug()) {
			Log.i(TAG, msg);
		}
	}

	public static void a(String str) {
		if (isDebug()) {
			Log.i(TAG,str);
		}
	}
	
	public static void a(String tag,String str) {
		if (isDebug()) {
			Log.i(TAG, str);
		}
	}

	public static void s(String str) {
		if (isDebug()) {
			Log.i(TAG,str);
		}
	}

	public static void e(String tag, String str) {
		if (error) {
			Log.e(TAG, str);
		}
	}

	public static void b(Object str) {
		if (isDebug()) {
			System.out.println("NONONO " + str.toString());
		}
	}

	public static void err(Object str) {
		if (isDebug()) {
			System.err.println(str.toString());
		}
	}
}
