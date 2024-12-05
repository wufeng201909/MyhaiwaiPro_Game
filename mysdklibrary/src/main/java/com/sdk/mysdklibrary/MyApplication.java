package com.sdk.mysdklibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.oaid.AdjustOaid;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.localbeans.GameArgs;
import com.sdk.mysdklibrary.localbeans.OrderInfo;
import com.sdk.mysdklibrary.othersdk.SkipUtil;

public class MyApplication extends Application {

	public static Context context;
	private static MyApplication myself;
	/** 游戏信息 */
	private GameArgs gameargs = null;
	private OrderInfo orderinfo = null;

	public void setOrderinfo(OrderInfo orderinfo) {
		this.orderinfo = orderinfo;
	}

	public OrderInfo getOrderinfo() {
		return orderinfo;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		context = this;
		SkipUtil.APPAttachBaseContext(this,base);
	}

	@Override
	public void onCreate() {
		System.out.println("111111111111111111111");
		MyCrashHandler mCrashHandler=MyCrashHandler.getInstance();
		mCrashHandler.init(this);
		super.onCreate();

		myself = this;
		checkSD();

		SkipUtil.APPOnCreate(this);

		//初始化Adjust,//adjust专用--------------------------------------------------
		if(!Configs.setAdjustParam(this)) return;
		//华为专用
		try{
			AdjustOaid.readOaid(this);
		}catch (Error e){
			System.out.println("no-AdjustOaid");
		}

		String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;//develop;test:AdjustConfig.ENVIRONMENT_SANDBOX
		AdjustConfig config = new AdjustConfig(this, Configs.getAppToken(), environment);

		config.setLogLevel(LogLevel.DEBUG);

		// Set event success tracking delegate.
		config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener() {
			@Override
			public void onFinishedEventTrackingSucceeded(AdjustEventSuccess eventSuccessResponseData) {
				Log.d("MySDK", "Event success callback called!");
				Log.d("MySDK", "Event success data: " + eventSuccessResponseData.toString());
			}
		});
		// Set event failure tracking delegate.
		config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener() {
			@Override
			public void onFinishedEventTrackingFailed(AdjustEventFailure eventFailureResponseData) {
				Log.d("MySDK", "Event failure callback called!");
				Log.d("MySDK", "Event failure data: " + eventFailureResponseData.toString());
			}
		});
		config.setSendInBackground(true);
		config.setProcessName(this.getPackageName());
		Adjust.onCreate(config);
		registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());

		//初始化Adjust,//adjust专用--------------------------------------------------
    }

	//adjust专用----------------------------------------------
	private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
		@Override
		public void onActivityCreated(Activity activity, Bundle bundle) {

		}

		@Override
		public void onActivityStarted(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {
			Adjust.onResume();
		}

		@Override
		public void onActivityPaused(Activity activity) {
			Adjust.onPause();
		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {

		}

		//...
	}
	//adjust专用----------------------------------------------

	/**
	 * 加载SD卡相关信息
	 */
	public void checkSD() {
		FilesTool.ExistSDCard();
		System.out.println("Configs.ASDKROOT----------------->"+ Configs.ASDKROOT);
	}

	/**
	 * 得到当前全局游戏实体
	 * 
	 * @return
	 */
	public GameArgs getGameArgs() {
		return gameargs;
	}

	/**
	 * 设置当前全局游戏实体
	 * 
	 * @param gameargs
	 */
	public void setGameArgs(GameArgs gameargs) {
		this.gameargs = gameargs;
	}

	public static MyApplication getAppContext() {
		if(myself == null)
			myself = new MyApplication();
		return myself;
	}

}
