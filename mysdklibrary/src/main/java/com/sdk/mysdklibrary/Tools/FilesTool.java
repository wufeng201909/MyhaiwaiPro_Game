package com.sdk.mysdklibrary.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MySdkApi;

public class FilesTool {

	/**
	 * 获取制定文件里的内容
	 * 
	 * @return content
	 */
	private static String pub = "";
	public static String getPublisherStringContent(){
		pub = getPublisherString()[0];
		return pub==null?"":pub;
	}

	/**
	 * 得到渠道标识,表明用户来源(从哪个合作方而来)
	 * 
	 * @return Publisher
	 */

	public static String[] getPublisherString() {
		Context con = MyApplication.context==null? MySdkApi.getMact():MyApplication.context;
		MLog.a("FilesTool","context---------------->"+con);
		String pub = "";
		try {
			InputStream ins = con.getResources().getAssets().open("MythsPublisher.txt");
			pub = new BufferedReader(new InputStreamReader(ins)).readLine().trim();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String[] string = new String[10];
		string[0] = pub;

		return string;
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return
	 */
	public static boolean ExistSDCard() {
		Configs.ASDKROOT = File.separator+"data"+File.separator+"data" +File.separator+ MyApplication.context.getPackageName() + File.separator+"files"+File.separator;
		return false;
	}

	public static synchronized boolean isContainPackName(final Context mContext , String packName) {
		boolean isContainPack = false;
		try {
			PackageManager packageManager = mContext.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(packName , PackageManager.GET_ACTIVITIES);
			if(info != null){
				isContainPack = true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return isContainPack;
	}

	//获取本应用数据路径
	public static File getCacheDir(Context context) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		File dir = new File(cachePath);
		return dir;
	}

}
