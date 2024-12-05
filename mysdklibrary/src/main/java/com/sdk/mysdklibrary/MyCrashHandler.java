package com.sdk.mysdklibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;


public class MyCrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "MyCrashHandler";

	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	// MyCrashHandler实例
	private static MyCrashHandler mCrashHandler;
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	private MyCrashHandler() {

	}

	/**
	 * 获取MyCrashHandler实例 ,单例模式
	 * 
	 * @return
	 */
	public static synchronized MyCrashHandler getInstance() {
		if (mCrashHandler == null) {
			mCrashHandler = new MyCrashHandler();
		}
		return mCrashHandler;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		this.mContext = context;
		// 获取系统默认的UncaughtException处理器
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置MyCrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String publisher = FilesTool.getPublisherStringContent();
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			System.exit(0);
		}

	}

	/**
	 * 自定义错误处理,收集错误信息 ,发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		} else {
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(mContext, "error",
							Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}.start();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			collectDeviceInfo(mContext);
			saveCrashInfoToFile(ex);
		}
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到指定文件中
	 * 
	 * @param ex
	 */
	private void saveCrashInfoToFile(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		String result = getErrorInfo(ex);
		sb.append(result);
		MLog.a("crash------------->"+sb.toString());
		PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"crash:"+sb.toString());
		try {
			String fileName = "crashInfo-file"+".log";
			File dir = new File(FilesTool.getCacheDir(mContext).getAbsolutePath() +File.separator+ fileName);
			if (!dir.exists()) {
				dir.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(dir);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param ex
	 * @return
	 */
	private String getErrorInfo(Throwable ex) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		return writer.toString();
	}
}
