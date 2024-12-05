package com.sdk.mysdklibrary.Tools;

import android.content.Context;
import android.text.TextUtils;

public class Configs {
	/** 随版本发布的BV */
	public static String BV = "20240308";//20240226
	/** 随版本发布的BV */
	public static String SV = "20240308";//20240226
	/** use XPKG */
	public static boolean USEXPKG = true;
	/** 要解析数据头 */
	public static boolean ALLUSED = true;
	/** 数据包目录 */
	public static String ASDK = "outsea_sdk";
	/** 数据包目录 */
//	public static String ASDK = "out_sdk";
	/** SD是否存在 */
	public static boolean SDEXIST = false;
	/** SD剩余大小 */
	public static long SDSIZE = 0;
	/** ASDK根目录 */
	public static String ASDKROOT = null;
	/** 注册成功码 */
	public static int REGISTERSUCCESS = 100;
	/** 注册失败码 */
	public static int REGISTERFAILURE = 101;
	/** 登陆成功码 */
	public static int LOGINSUCCESS = 102;
	/** 登陆失败码 */
	public static int LOGINFAILURE = 103;
	/** 充值成功码 */
	public static int CHARGESUCCESS = 104;
	/** 充值失败码 */
	public static int CHARGEAILURE = 105;
	/** 8种基本数据类型 */
	public static Class<Double> tdouble = Double.TYPE;
	public static Class<Float> tfloat = Float.TYPE;
	public static Class<Long> tlong = Long.TYPE;
	public static Class<Integer> tint = Integer.TYPE;
	public static Class<Short> tshort = Short.TYPE;
	public static Class<Character> tchar = Character.TYPE;
	public static Class<Byte> tbyte = Byte.TYPE;
	public static Class<Boolean> tboolean = Boolean.TYPE;
	//俄罗斯
	//	public static String initurl1 ="http://update.meta-sensor.io/init.php";//正式
	public static String initurl1 ="http://updatenew.meta-sensor.io/init.php";//正式
	public static String initurl2 = "http://update.surfguild.io/init.php"; //备用
	//数据上报（旧的hiplay用过）
	final public static String dataReportUrl = "http://sdk.szmsbdmy.com:8084/outerinterface/zteventreport.php";//http://120.79.164.117:8084/outerinterface/zteventreport.php
	//tradplus数据上报
	final public static String tradPlusReportUrl = "http://sdk.szmsbdmy.com:8084/outerinterface/tpadinfocallback.php";
	//sdk自埋点上报
	final static String submit_maidian_url = "http://sdk.szmsbdmy.com:8083/azmd.php";
	public static String getMaidianUrl(){
		return submit_maidian_url;
	}
	//sdk错误搜集上报
	final static String submit_error_url = "http://md.szmsbdmy.com:8083/errormd.php";
	static String interfaceReqFailed = "A01";//请求接口失败
	static String appError = "A02";//应用层报错
	static String gameError = "B01";//游戏报错
	public static String getInReqFailCode(){
		return interfaceReqFailed;
	}
	public static String getAppErrorCode(){
		return appError;
	}
	public static String getGameErrorCode(){
		return gameError;
	}
	public static String getSubErrUrl(){
		return submit_error_url;
	}

	public static String initurl = initurl1;

	private static String appToken = "";
	private static String appToken_activation = "";
	private static String appToken_init = "";
	private static String appToken_login = "";
	private static String appToken_adReport = "";
	private static String appToken_paySuccess = "";

	public static String getAppToken(){
		return appToken;
	}

	public static String getAppToken_activation() {
		return appToken_activation;
	}
	public static String getAppToken_init() {
		return appToken_init;
	}

	public static String getAppToken_login() {
		return appToken_login;
	}

	public static String getAppToken_adReport() {
		return appToken_adReport;
	}

	public static String getAppToken_paySuccess() {
		return appToken_paySuccess;
	}

	private static String purchase_notVerified = "";
	private static String purchase_failed = "";
	private static String purchase_successful = "";
	private static String purchase_unknown = "";
	public static String getPurchase_notVerified() {
		return purchase_notVerified;
	}

	public static String getPurchase_failed() {
		return purchase_failed;
	}

	public static String getPurchase_successful() {
		return purchase_successful;
	}

	public static String getPurchase_unknown() {
		return purchase_unknown;
	}

	public static String accountserver="";
	public static String payserver="";
	public static String othersdkextdata1="";//渠道支付的验证地址
	public static String othersdkextdata2="";//埋点开关
	public static String othersdkextdata3="";//游客开关
	public static String othersdkextdata4="";//客服url
	public static String othersdkextdata5="";//登录方式开关json
	public static String gp_url="";
	public static String onestore_url="";
	public static String payermax_url="";
	public static String honor_url="";
	private static boolean testwallet = false;
	private static String payTo = "";
	private static String payData = "";
	public static String kfUrl = "kfUrl";//客服url
	public static String ggLogin = "ggLogin";//google登录开关
	public static String vkLogin = "vkLogin";//vk登录开关
	public static String eLogin = "emailLogin";//email登录开关

	public static String getItem(String key){
		return ResourceUtil.getJsonItem(Configs.othersdkextdata5,key);
	}


	public static String getPayTo() {
		return payTo;
	}
	public static void setPayTo(String payTo) {
		Configs.payTo = payTo;
	}
	public static String getPayData() {
		return payData;
	}
	public static void setPayData(String payData) {
		Configs.payData = payData;
	}
	public static boolean isTestwallet() {
		return testwallet;
	}
	public static void setTestwallet(boolean testwallet) {
		Configs.testwallet = testwallet;
	}

	public static boolean setAdjustParam(Context con) {
		appToken_activation = ResourceUtil.getString(con,"adjust_appTokenActivation");
		appToken_init = ResourceUtil.getString(con,"adjust_appTokenInit");
		appToken_login = ResourceUtil.getString(con,"adjust_appTokenLogin");
		appToken_adReport = ResourceUtil.getString(con,"adjust_appTokenAdReport");
		appToken_paySuccess = ResourceUtil.getString(con,"adjust_appTokenPaySuccess");
		appToken = ResourceUtil.getString(con,"adjust_appToken");
		return !TextUtils.isEmpty(appToken_activation);
	}
}
