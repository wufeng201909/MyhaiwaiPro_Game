package com.sdk.mysdklibrary.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Net.MyAsyncTask;
import com.sdk.mysdklibrary.customView.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneTool {
	public static String TAG = "PhoneTool";
	private static String AnId = "";

	/**
	 * IMSI：international mobiles subscriber
	 * identity国际移动用户号码标识，这个一般大家是不知道，GSM必须写在卡内相关文件中 MSISDN:mobile subscriber
	 * ISDN用户号码，这个是我们说的139，136那个号码； ICCID:ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的；
	 * IMEI：international mobile Equipment identity手机唯一标识码； TelephonyManager tm
	 * = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	 * String imei = tm.getDeviceId(); //取出IMEI String tel =
	 * tm.getLine1Number(); //取出MSISDN，很可能为空(電話號碼) String imei
	 * =tm.getSimSerialNumber(); //取出ICCID String imsi =tm.getSubscriberId();
	 * //取出IMEI
	 *
	 * @return
	 */

	public static String getIMEI(Context con) {
//		AnId = "".equals(AnId)?getAnId(con):AnId;
//		if(!isgetDeId(con)){
//			return "|"+AnId;
//		}
		String spDeviceID = MySdkApi.getMact().getSharedPreferences("user_info", 0).getString("device_id", "");
		if("".equals(spDeviceID)){
			spDeviceID = getDeviceId(con);
			MySdkApi.getMact().getSharedPreferences("user_info", 0).edit().putString("device_id", spDeviceID).commit();
		}
		return spDeviceID;
//		return spDeviceID+"|"+AnId;
	}


	@SuppressWarnings("deprecation")
	@SuppressLint("MissingPermission")
	private static String getDeviceId(Context context) {
        //读取保存的在sd卡中的唯一标识符
        String deviceId = readDeviceID(context);
        //判断是否已经生成过,有则直接返回
        if (deviceId != null && !"".equals(deviceId.trim()) && !deviceId.contains("00000000")) {
        	return deviceId;
        }
        //用于生成最终的唯一标识符
        StringBuffer s = new StringBuffer();
        try {
            //获取设备的ANDROID_ID+SERIAL硬件序列号
        	deviceId = getAndroidId(context);
        	String serial= getSERIAL();
			MLog.a("deviceId-->"+deviceId+";serial-->"+serial);
            s.append(deviceId).append(serial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
        if (TextUtils.isEmpty(deviceId.trim())||TextUtils.isEmpty(s.toString().trim())||s.toString().trim().length()<8) {
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString().replace("-", "");
            s.append(deviceId);
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
        String md5 = MD5Util.getMD5String(s.toString());
        if (s.length() > 0) {
            //持久化操作, 进行保存到SD卡中
            saveDeviceID(md5, context);
        }
        return md5;
	}

	@SuppressLint("HardwareIds")
	private static String getAndroidId(Context context) {
		try {
			return Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@SuppressLint("HardwareIds")
	private static String getSERIAL() {
		try {
			return Build.SERIAL;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	@SuppressLint("HardwareIds")
	private static String getDeviceUUID() {
		try{
			String dev = "359292" +
					Build.BOARD.length() % 10 +
					Build.BRAND.length() % 10 +
					Build.DEVICE.length() % 10 +
					Build.HARDWARE.length() % 10 +
					Build.ID.length() % 10 +
					Build.MODEL.length() % 10 +
					Build.PRODUCT.length() % 10 +
					Build.SERIAL.length() % 10;
			return new UUID(dev.hashCode(),
					Build.SERIAL.hashCode()).toString();
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}
	/**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     *
     * @param context
     * @return
     */
	private static String readDeviceID(Context context) {
        File file = getDevicesDir(context);
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(isr);
            String deviceID = in.readLine().trim();
            in.close();
            isr.close();
            fis.close();
            return deviceID;
        } catch (Exception e) {
            return null;
        }
	}
	//保存文件的路径
    private static final String CACHE_IMAGE_DIR = "aray/cache/devices";
    //保存的文件 采用隐藏文件的形式进行保存
    private static final String DEVICES_FILE_NAME = ".asdkDevice";
	/**
     * 统一处理设备唯一标识 保存的文件的地址
     * @param context
     * @return
     */
    private static File getDevicesDir(Context context) {
        File mCropFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cropdir = new File(Environment.getExternalStorageDirectory(), CACHE_IMAGE_DIR);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME); // 用当前时间给取得的图片命名
        } else {
            File cropdir = new File(context.getFilesDir(), CACHE_IMAGE_DIR);
            if (!cropdir.exists()) {
                cropdir.mkdirs();
            }
            mCropFile = new File(cropdir, DEVICES_FILE_NAME);
        }
        return mCropFile;
    }
    /**
     * 保存 内容到 SD卡中,  这里保存的就是 设备唯一标识符
     * @param str
     * @param context
     */
    private static void saveDeviceID(String str, Context context) {
        File file = getDevicesDir(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, "UTF-8");
            out.write(str);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //处理设备号
    public static void managerIMEI(final Activity activity){
    	//获取Androidid
//		AnId = getAnId(activity);
    	new Thread(new Runnable() {

			@Override
			public void run() {
				//sd卡读取
				String readDeviceID = readDeviceID(activity);
				//app缓存读取
				String spDeviceID = MySdkApi.getMact().getSharedPreferences("user_info", 0).getString("device_id", "");

                if (readDeviceID==null||"".equals(readDeviceID)||readDeviceID.contains("00000000")) {// sd卡缓存为空

                	if(spDeviceID==null||"".equals(spDeviceID)){//app缓存为空
                		readDeviceID = getDeviceId(activity);
						MySdkApi.getMact().getSharedPreferences("user_info", 0).edit().putString("device_id", readDeviceID).commit();
                	}else{////app缓存不为空
                		readDeviceID = spDeviceID;
                		saveDeviceID(readDeviceID, activity);
                	}
                }else{
					if(spDeviceID==null||"".equals(spDeviceID)) {//app缓存为空
						MySdkApi.getMact().getSharedPreferences("user_info", 0).edit().putString("device_id", readDeviceID).commit();
					}
				}

			}
		}).start();
    }
	// 取出IMSI
	@SuppressLint("MissingPermission")
	public static String getIMSI(Context con) {
		String imsi = "a1s2d3f4t5";
//		try {
//			TelephonyManager mTelephonyMgr = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
//			imsi = mTelephonyMgr.getSubscriberId();
//		} catch (Exception e) {
//			MLog.a(TAG,"好吧没得到IMSI");
//			return "a1s2d3f4t5";
//		}
//		if (("").equals(imsi) || ("null").equals(imsi) || imsi == null) {
//			return "a1s2d3f4t5";
//		}
		return imsi;
	}

	/**
	 * 加密后的IMEI
	 *
	 * @param imei
	 * @return
	 */
	public static String getIEMI(String imei) {
		StringBuilder ji = new StringBuilder();
		StringBuilder ou = new StringBuilder();
		imei = MD5Util.getMD5String(imei);
		for (int i = 0; i < imei.length(); i++) {
			if (i % 2 == 0) {
				ou.append(imei.charAt(i));
			} else {
				ji.append(imei.charAt(i));
			}
		}

		imei = MD5Util.getMD5String(ji + "jsk412lj21j5klj362dfanbvkc59874590asfk" + ou);
		return imei;
	}

	/**
	 * 判断有木有使用cmwap代理
	 *
	 * @return
	 */
	public static boolean isProxy(Context con) {
		ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni == null || (ni != null && !ni.isAvailable())) {// 未开启网络
				return false;
			} else { // 开启了网络
				if (ni.getTypeName().equals("WIFI")) {
					System.out.println("NET mode : " + "WIFI");
					return true;
				} else {
					NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					String netString = networkInfo.getExtraInfo();
					System.out.println("NET mode : " + netString);
					if (netString.contains("cmnet")) {
						System.out.println("NET mode : " + "cmnet");
						return true;
					} else if (netString.contains("cmwap")) {
						System.out.println("NET mode : " + "cmwap");
						return true;
					} else if (netString.contains("internet")) {
						System.out.println("NET mode : " + "internet");
						return true;
					} else {
						System.out.println("NET mode : " + "未知");
						return true;
					}
				}
			}
		} else {
			return false;
		}
	}

	/**
	 * 返回IP地址
	 *
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断网络连接是否打开
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}

		isProxy(context);
		System.out.println("IP address :" + getLocalIpAddress());
		return bisConnFlag;

	}

	/**
	 * Role:Telecom service providers获取手机服务商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 */
	@SuppressLint("MissingPermission")
	public static String getProvidersName(Context con) {
		// String ProvidersName = "未知";
		String gateway = "3";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
			// 返回唯一的用户ID;就是这张卡的编号神马的
			 String IMSI = mTelephonyMgr.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			// 后面还有10位，是不知的
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
				// ProvidersName = "中国移动";
				gateway = "0";
			} else if (IMSI.startsWith("46001")) {
				// ProvidersName = "中国联通";
				gateway = "1";
			} else if (IMSI.startsWith("46003")) {
				// ProvidersName = "中国电信";
				gateway = "2";
			}

		} catch (Exception e) {
			MLog.a(TAG,"是不是没有卡呢？");
			return gateway;

		}

		/*
		 * String operator = mTelephonyMgr.getSimOperator(); if
		 * (operator.equals("46000") || operator.equals("46002")) {
		 * ProvidersName = "中国移动"; } else if (operator.equals("46001")) {
		 * ProvidersName = "中国联通"; } else if (operator.equals("46003")) {
		 * ProvidersName = "中国电信"; }
		 *
		 * Configuration conf =
		 * AsdkActivity.asdk.getResources().getConfiguration();
		 * if(conf.mcc==460){//中国 if(conf.mnc==0 || conf.mnc==2){ ProvidersName
		 * = "中国移动"; }else if(conf.mnc==1){ ProvidersName = "中国联通"; }else
		 * if(conf.mnc==3){ ProvidersName = "中国电信"; } }
		 */

		// Configuration conf = con.getResources().getConfiguration();

		return gateway;
	}

	/**
	 * 获取网络信号类型
	 */
	@SuppressLint("MissingPermission")
	public static String getNetType(Context con) {
		TelephonyManager telephonyManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = 0;
		try{
			networkType = telephonyManager.getNetworkType();
		}catch (Exception e){

		}

		if (networkType == TelephonyManager.NETWORK_TYPE_UMTS || networkType == TelephonyManager.NETWORK_TYPE_HSDPA || networkType == TelephonyManager.NETWORK_TYPE_EVDO_0 || networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
			return "3G";
		} else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS || networkType == TelephonyManager.NETWORK_TYPE_EDGE || networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
			return "2G";
		}
		return networkType + "";
	}

	/**
	 * 获取手机方位是什么
	 */
	@SuppressLint("MissingPermission")
	public static String getCL(Context con) {
		String cl = "feizhou";
//		CellLocation cell = null;
//		try {
//			TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
//			cell = tm.getCellLocation(); // 手机号码
//			cl = String.valueOf(cell);
//		} catch (Exception e) {
//			MLog.a(TAG,"好吧没得到手机方位");
//			return "feizhou";
//		}
//		if (("").equals(String.valueOf(cell)) || ("null").equals(String.valueOf(cell)) || String.valueOf(cell) == null || String.valueOf(cell) == "null") {
//			return "feizhou";
//		}
		return cl;
	}

	/**
	 * 获取手机型号
	 */
	public static String getPT(Context con) {
		String mtyb = "copycat";
		String mtype = "brands";
		String version = "Android";
		try {
			TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
			mtyb = Build.BRAND;// 手机品牌
			mtype = Build.MODEL; // 手机型号
			version = version + Build.VERSION.RELEASE;// 版本
		} catch (Exception e) {
			// TODO: handle exception
			MLog.a(TAG,"好吧，没得到手机型号");
			return "copycat|brands";
		}
		return mtype + "|" + mtyb + "|" +version;
	}
	
	@SuppressLint({"MissingPermission", "HardwareIds"})
	public static String getMac(Context con){
		String mac = "";
		WifiManager wifi = (WifiManager) con.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info;
		try {
			info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		MLog.a(TAG, "mac:" + mac);
		return mac == null?"":mac;
		
	}

	/**
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		Builder builder = new Builder(context);

		builder.setTitle("notice").setMessage("Network connection unavailable, do you want to set it up?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;
				if (Build.VERSION.SDK_INT > 10) {// 判断手机系统的版本
															// 即API大于10
															// 就是3.0或以上版本
					intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				} else {
					intent = new Intent();
					ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
					intent.setComponent(component);
					intent.setAction("android.intent.action.VIEW");
				}
				context.startActivity(intent);
			}
		});

		builder.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
				}
				return true;// 自己消费，不劳上层费心
			}
		});

		builder.show();
	}

	private static Dialog mDialog = null;
	private static final Timer tim =new Timer();
	public static void onCreateDialog(final Activity activity, String title, String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mDialog!=null){
					try {
						mDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("pre---dismiss--null");
					}
				}
				mDialog = createLoadingDialog(activity);
				mDialog.show();
				tim.schedule(new TimerTask() {
					@Override
					public void run() {
						disDialog();
					}
				},3000);
			}
		});

	}

	public static void disDialog() {
		if(mDialog!=null ){
			MySdkApi.getMact().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						mDialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	}

	private static Dialog createLoadingDialog(Activity activity) {
		LinearLayout ll = new LinearLayout(activity);
		ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		ll.setGravity(Gravity.CENTER);

		LoadingView lview = new LoadingView(activity);
		ll.addView(lview);

		Dialog dialog = new Dialog(activity,ResourceUtil.getStyleId(activity,"mythssdk_customdialog_style"));
		dialog.setCancelable(false);//取消不可用
		dialog.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);// 全屏
		lp.alpha = 0.0f;// 透明度 0.0-1.0,0.0完全透明，1.0完全不透明
		lp.dimAmount = 0.0f;// 背景层 0.0-1.0，0.0背景完全可见，1.0背景全黑
		dialog.setContentView(ll, lp);

		return dialog;
	}
	
	/**
	 * 复制内容到剪切板
	 * 
	 * @param context
	 * @param number
	 */
	@SuppressWarnings("deprecation")
	public static void copy(Context context, String number) {
		if (Build.VERSION.SDK_INT < 11) {
			android.text.ClipboardManager clip = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(number);
		} else {
			ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(number);
		}
	}
	
	public static InputFilter[] inputFilter(int length){
		return new InputFilter[]{new InputFilter.LengthFilter(length)};
	}
	private static int autoLogin_time_milliseconds = -1;
	public static void setAutoLogin_time_milliseconds(
			int autoLogin_time_milliseconds) {
		PhoneTool.autoLogin_time_milliseconds = autoLogin_time_milliseconds;
	}
	public static void autoLogin(final Activity activity,int time_secends,final String type){
		autoLogin_time_milliseconds = time_secends;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (autoLogin_time_milliseconds>0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						autoLogin_time_milliseconds = -1;
						Thread.currentThread().interrupt();
					}
					autoLogin_time_milliseconds--;
				}
				if(autoLogin_time_milliseconds==0){//登录
					MySdkApi.getMact().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								activity.finish();
							}catch (Exception e){
							}
						}
					});
					if ("guest".equals(type)){
						HttpUtils.acclogin("guest");
					}else if("facebook".equals(type)) {
						String acc = MyGamesImpl.getSharedPreferences().getString("myths_fbid","");
						String token = MyGamesImpl.getSharedPreferences().getString("myths_input_token","");
						HttpUtils.fblogin_check(acc,token,"");
					}else if("oldacc".equals(type)){
						HttpUtils.acclogin("oldacc");
					}else if("google".equals(type)){
						String googleid = MyGamesImpl.getSharedPreferences().getString("myths_googleid","");
						String googleidtoken = MyGamesImpl.getSharedPreferences().getString("myths_googleidtoken","");
						HttpUtils.googlelogin_check(googleid,googleidtoken,"");
					}else if("apple".equals(type)){
						String appleid = MyGamesImpl.getSharedPreferences().getString("myths_appleid","");
						String appleemail = MyGamesImpl.getSharedPreferences().getString("myths_appleemail","");
						HttpUtils.applelogin_check(appleid,appleemail);
					}else if("wallet".equals(type)){
						String walletid = MyGamesImpl.getSharedPreferences().getString("myths_walletid","");
						String walletsign = MyGamesImpl.getSharedPreferences().getString("myths_walletsign","");
						String walletnonce = MyGamesImpl.getSharedPreferences().getString("myths_walletnonce","");
						HttpUtils.walletLogin_check(walletid,walletsign, walletnonce);
					}else if("wemix".equals(type)){
						String address = MyGamesImpl.getSharedPreferences().getString("myths_wemixaddress","");
						String accessToken = MyGamesImpl.getSharedPreferences().getString("myths_wemixaccesstoken","");
						long timestamp = MyGamesImpl.getSharedPreferences().getLong("myths_wemixtimestamp",0);
						long expires = MyGamesImpl.getSharedPreferences().getLong("myths_wemixexpires",0);
						HttpUtils.wemixLogin_check(address, accessToken,timestamp,expires);
					}else if("othersdk".equals(type)){
						MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),10);
					}else if("vk".equals(type)){
						String vk_uid = MyGamesImpl.getSharedPreferences().getString("myths_vk_uid","");;
						HttpUtils.vkLogin_check(vk_uid, "");
					}else if("email".equals(type)){
						String email = MyGamesImpl.getSharedPreferences().getString("myths_email","");
						String email_psd = MyGamesImpl.getSharedPreferences().getString("myths_email_psd","");
						HttpUtils.getEmailLoginByPsd(email,email_psd);
					}
				}
			}
		}).start();
	}

	@SuppressWarnings("deprecation")
	public static boolean isTopActivity(Activity act)  
    {  
        boolean isTop = false;  
        ActivityManager am = (ActivityManager)act.getSystemService(Context.ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        String name = act.getClass().getName();
        if (cn.getClassName().contains(name))  
        {  
            isTop = true;  
        }  
        return isTop;  
    }

	@SuppressLint("ObjectAnimatorBinding")
	public static void auto_Login_Animator(Object ob,float start,float end,long time){
		ObjectAnimator animator=ObjectAnimator.ofFloat(ob,"rotation",start,end);
		animator.setDuration(time);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.RESTART);
		animator.start();
	}

	//埋点上报
	private static HashMap<String, String> map = new HashMap<String, String>();

	static {
		map.put("1",	"A001");map.put("2",	"A002");map.put("3",	"A003");
		map.put("4",	"A004");map.put("5",	"A005");map.put("6",	"A006");
		map.put("7",	"A007");map.put("8",	"A008");map.put("9",	"A009");
		map.put("10",	"A010");map.put("11",	"A011");map.put("12",	"A012");
		map.put("13",	"A013");map.put("14",	"A014");map.put("15",	"A015");
		map.put("16",	"A016");map.put("17",	"B001");map.put("18",	"B002");
		map.put("19",	"A017");map.put("20",	"A018");
	}

	//上报埋点
	public static void submitSDKEvent(final String flag,final String msg){
		if( (!"1".equals(Configs.othersdkextdata2)) && (!"18".equals(flag)) ){//初始化扩展参数2为埋点上报开关，初始化失败(18)定会走上报
			MLog.a("not submit maidian->"+flag+":"+msg);
			return;
		}
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String maidian_value = map.get(flag);
				MLog.a("maidian_value-->"+maidian_value);
				if(maidian_value!=null){
					JSONObject param = new JSONObject();
					try {
						param.put("msg", msg);
						param.put("dh", maidian_value);
					} catch (JSONException e) {
						e.printStackTrace();
					}
//					LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();
//					ExecutorService exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, blockingQueue);
					MyAsyncTask myAsyncTask = new MyAsyncTask();
					myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Configs.getMaidianUrl(),param.toString());
				}
			}
		});
	}

	//错误搜集上报
	public static void submitErrorEvent(final String flag,final String msg){
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = new JSONObject();
				try {
					param.put("msg", msg);
					param.put("dh", flag);
					param.put("time", System.currentTimeMillis());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				MyAsyncTask myAsyncTask = new MyAsyncTask();
				myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Configs.getSubErrUrl(),param.toString());
			}
		});
	}

	private static String getVERSION(Context con) {
		String version = android.os.Build.VERSION.RELEASE;// 版本
		MLog.a("version:"+version);
		return version;
	}

	//---------------------网络情况------------------------
	//没有网络连接
	private static final String NETWORN_NONE = "无网络";
	//wifi连接
	private static final String NETWORN_WIFI = "wifi";
	//手机网络数据连接类型
	private static final String NETWORN_2G = "2g";
	private static final String NETWORN_3G = "3g";
	private static final String NETWORN_4G = "4g";
	private static final String NETWORN_5G = "5g";
	private static final String NETWORN_MOBILE = "未知";
	//---------------------网络情况------------------------
	/**
	 * 获取当前网络连接类型
	 *
	 * @param context
	 * @return
	 */
	public static String getNetworkState(Context context) {
		//获取系统的网络服务
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//如果当前没有网络
		if (null == connManager)
			return NETWORN_NONE;
		//获取当前网络类型，如果为空，返回无网络
		@SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
			return NETWORN_NONE;
		}
		// 判断是不是连接的是不是wifi
		@SuppressLint("MissingPermission") NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != wifiInfo) {
			NetworkInfo.State state = wifiInfo.getState();
			if (null != state)
				if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
					return NETWORN_WIFI;
				}
		}
		// 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
		@SuppressLint("MissingPermission") NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (null != networkInfo) {
			NetworkInfo.State state = networkInfo.getState();
			String strSubTypeName = networkInfo.getSubtypeName();
			if (null != state)
				if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
					switch (activeNetInfo.getSubtype()) {
						//如果是2g类型
						case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
						case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
						case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
						case TelephonyManager.NETWORK_TYPE_1xRTT:
						case TelephonyManager.NETWORK_TYPE_IDEN:
							return NETWORN_2G;
						//如果是3g类型
						case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
						case TelephonyManager.NETWORK_TYPE_UMTS:
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
						case TelephonyManager.NETWORK_TYPE_HSDPA:
						case TelephonyManager.NETWORK_TYPE_HSUPA:
						case TelephonyManager.NETWORK_TYPE_HSPA:
						case TelephonyManager.NETWORK_TYPE_EVDO_B:
						case TelephonyManager.NETWORK_TYPE_EHRPD:
						case TelephonyManager.NETWORK_TYPE_HSPAP:
							return NETWORN_3G;
						//如果是4g类型
						case TelephonyManager.NETWORK_TYPE_LTE:
							return NETWORN_4G;
						case TelephonyManager.NETWORK_TYPE_NR: //对应的20 只有依赖为android 10.0才有此属性
							return NETWORN_5G;
						default:
							//中国移动 联通 电信 三种3G制式
							if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
								return NETWORN_3G;
							} else {
								return NETWORN_MOBILE;
							}
					}
				}
		}
		return NETWORN_NONE;
	}

	//判断横竖屏
	private static int island = 0;
	public static boolean island(Activity act){
		if(island == 0){
			DisplayMetrics displayMetrics = new DisplayMetrics();
			act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int width = displayMetrics.widthPixels;
			int height = displayMetrics.heightPixels;
			island = width>height?1:2;
		}
		return island ==1?true:false;
	}

	private static Handler mHandler;
	public static void setHandler(Handler handler){
		mHandler = handler;
	}
	private static int time_seconds;
	public static int getTime_seconds(){
		return time_seconds;
	}
	public static void countdown(final Activity activity, int secends, Handler handler){
		setHandler(handler);
		if(time_seconds<=0)time_seconds = secends;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (time_seconds>0) {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = time_seconds;
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						time_seconds = -1;
						Thread.currentThread().interrupt();
					}
					time_seconds--;
				}
				mHandler.sendEmptyMessage(1);
			}
		}).start();
	}

	/**
	 * 隐藏状态栏，显示内容上移到状态栏
	 */
	public static void hideSystemBars(Window window) {
		//占满全屏，activity绘制将状态栏也加入绘制范围。
		//如此即使使用BEHAVIOR_SHOW_BARS_BY_SWIPE或BEHAVIOR_SHOW_BARS_BY_TOUCH
		//也不会因为状态栏的显示而导致activity的绘制区域出现变形
		//使用刘海屏也需要这一句进行全屏处理
		WindowCompat.setDecorFitsSystemWindows(window, false);
		//隐藏状态栏和导航栏 以及交互
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			//隐藏状态栏和导航栏
			//用于WindowInsetsCompat.Type.systemBars()隐藏两个系统栏
			//用于WindowInsetsCompat.Type.statusBars()仅隐藏状态栏
			//用于WindowInsetsCompat.Type.navigationBars()仅隐藏导航栏
			WindowInsetsControllerCompat contr = WindowCompat.getInsetsController(window,window.getDecorView());
			contr.hide(WindowInsetsCompat.Type.systemBars());
			contr.hide(WindowInsetsCompat.Type.navigationBars());
			//交互效果
			//BEHAVIOR_SHOW_BARS_BY_SWIPE 下拉状态栏操作可能会导致activity画面变形
			//BEHAVIOR_SHOW_BARS_BY_TOUCH 未测试到与BEHAVIOR_SHOW_BARS_BY_SWIPE的明显差异
			//BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE 下拉或上拉的屏幕交互操作会显示状态栏和导航栏
			contr.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
		}
	}
	public static void useSpecialScreen(Window window) {
		//允许window 的内容可以上移到刘海屏状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.layoutInDisplayCutoutMode =
					WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			window.setAttributes(lp);
		}
	}

}
