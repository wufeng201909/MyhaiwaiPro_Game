package com.sdk.mysdklibrary.Net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Locale;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Tools.AESSecurity;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;
import com.sdk.mysdklibrary.Tools.XfUtils;
import com.sdk.mysdklibrary.activity.AccLoginActivity;
import com.sdk.mysdklibrary.activity.AutoLoginActivity;
import com.sdk.mysdklibrary.activity.EmailLoginActivity;
import com.sdk.mysdklibrary.activity.PayActivity;
import com.sdk.mysdklibrary.interfaces.ApproveCallback;
import com.sdk.mysdklibrary.interfaces.BindPhoneCallBack;
import com.sdk.mysdklibrary.interfaces.ChangePasswordCallBack;
import com.sdk.mysdklibrary.interfaces.CheckCodeCallBack;
import com.sdk.mysdklibrary.interfaces.EmailCodeCallBack;
import com.sdk.mysdklibrary.interfaces.GetNonceCallBack;
import com.sdk.mysdklibrary.interfaces.GetorderCallBack;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;
import com.sdk.mysdklibrary.interfaces.PhoneCodeCallBack;
import com.sdk.mysdklibrary.interfaces.UnBindSDKCallBack;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;
import com.sdk.mysdklibrary.othersdk.SkipUtil;
import com.sdk.mysdklibrary.volley.Request.Method;
import com.sdk.mysdklibrary.volley.RequestQueue;
import com.sdk.mysdklibrary.volley.Response.ErrorListener;
import com.sdk.mysdklibrary.volley.Response.Listener;
import com.sdk.mysdklibrary.volley.VolleyError;
import com.sdk.mysdklibrary.volley.toolbox.JsonObjectRequest;
import com.sdk.mysdklibrary.volley.toolbox.JsonRequest;
import com.sdk.mysdklibrary.volley.toolbox.Volley;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class HttpUtils {
	/** 文件下载失败 */
	public final static int FILEDOWNERR = 1;
	/** 下载重连次数 */
	public final static int RECONNECTNUM = 5;
	/** 代理地址 */
	public static Proxy mProxy = null;
	public static String cookiestat = null;
	public final static String KEY = "48fhd5748sayuh12";
	private static String sTag = "HttpUtils";

	public static String mTips = "Warm tips";
	public static String mLoading = "loading......";

    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值
    //    private static final String KEY_STORE_CLIENT_PATH = "client.p12";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_TRUST_PATH = "mycg_server.crt";//客户端验证服务器端的证书库
    //    private static final String KEY_STORE_PASSWORD = "123456";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "changic2017";//服务器端证书库密码

	/**
	 * 封装请求头信息
	 * 
	 * @return
	 * @throws IOException
	 */
	public static URLConnection headMethod(String urls, int flag) {
		URL url = null;
		URLConnection urlConn = null;
		detectProxy();
		try {
			url = new URL(urls);
			if (mProxy != null) {
				urlConn = (HttpURLConnection) url.openConnection(mProxy);
			} else {
				urlConn = (HttpURLConnection) url.openConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
			PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),e.getMessage());
		}
		if (url != null && urlConn != null) {
			urlConn=addhttphead(urlConn);
		}
		return urlConn;
	}
	public static URLConnection addhttphead(URLConnection urlConn){
		String imei = PhoneTool.getIMEI(MyApplication.context);
		String op = PhoneTool.getProvidersName(MyApplication.context);
		String ua = PhoneTool.getPT(MyApplication.context);
		String pn = PhoneTool.getCL(MyApplication.context);
		String imsi = PhoneTool.getIMSI(MyApplication.context);
		String mac = PhoneTool.getMac(MyApplication.context);
		String cpid = MyApplication.getAppContext().getGameArgs().getCpid();
		String gameno = MyApplication.getAppContext().getGameArgs().getGameno();
		String moby_lang = Locale.getDefault().getCountry()+"_"+ Locale.getDefault().getLanguage();
		MLog.a("moby_auth："+PhoneTool.getIEMI(imei)
				+"\nmoby_imei："+imei
				+"\nmoby_op："+op
				+"\nmoby_ua："+ua
				+"\nmoby_pn："+pn
				+"\nmoby_imsi："+imsi
				+"\nmoby_mac："+mac
				+"\nmoby_gameid："+cpid+gameno
				+"\nmoby_bv："+Configs.BV
				+"\nmoby_sv："+Configs.SV
				+"\nmoby_language："+moby_lang
				+"\nmoby_pb："+MyApplication.getAppContext().getGameArgs().getPublisher()
				+"\nmoby_accid："+MyApplication.getAppContext().getGameArgs().getAccount_id()
				+"\nmoby_sessid："+MyApplication.getAppContext().getGameArgs().getSession_id());
		urlConn.addRequestProperty("moby_auth", PhoneTool.getIEMI(imei));//--参数签名,由moby_imei加密而来
		urlConn.addRequestProperty("moby_imei", imei);//--手机串号,在ios平台下似是mac地址
		urlConn.addRequestProperty("moby_sdk", "android");//--开发环境,表明用户手机操作系统的环境s60_5th/android/iphone
		urlConn.addRequestProperty("moby_op", op);//--用户运营商,表明用户网络的提供商
		urlConn.addRequestProperty("moby_ua", ua);//--用户代理,表明用户手机硬件/软件描述
		urlConn.addRequestProperty("moby_pn", pn);//手机方位
		urlConn.addRequestProperty("moby_imsi", imsi);//
		urlConn.addRequestProperty("moby_mac" , mac);
		urlConn.addRequestProperty("moby_gameid",cpid+gameno);
		urlConn.addRequestProperty("moby_bv", Configs.BV);//--平台版本,表明客户端的平台版本 MLog.s("SVSV -----> " +
		urlConn.addRequestProperty("moby_sv", Configs.SV);//包版本,表用用户上层lua的版本
		urlConn.addRequestProperty("moby_pb", MyApplication.getAppContext().getGameArgs().getPublisher());//--渠道标识,表明用户来源(从哪个合作方而来)
		urlConn.addRequestProperty("moby_accid",MyApplication.getAppContext().getGameArgs().getAccount_id()); //用户账号id,用户登录返回的用户账号,如果还未登录则为空
		urlConn.addRequestProperty("moby_sessid",MyApplication.getAppContext().getGameArgs().getSession_id());//登录会话id,全局唯一,表明用户登录的会话id
		urlConn.addRequestProperty("cpid",cpid);//商户ID
		urlConn.addRequestProperty("moby_language",moby_lang);//本机语言
		urlConn.setConnectTimeout(15000); urlConn.setReadTimeout(12000);
//		urlConn.addRequestProperty("Connection","close");
		return urlConn;
	}

	/*********************************************** GET *****************************************************/
	/**
	 * GET协议请求
	 */
	public static HttpURLConnection getMethod(String urls) {
		HttpURLConnection urlConn = null;
		MLog.s(urls);
		urlConn = (HttpURLConnection) headMethod(urls, 0);
		if (urlConn != null) {
			// 设置以GET方式
			try {
				urlConn.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);
		}
		return urlConn;
	}

	/**
	 * 检查代理，是否cnwap接入
	 */
	private static void detectProxy() {
		ConnectivityManager cm = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable() && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			MLog.s("proxyHost =========> " + proxyHost);
			MLog.s("proxyPort =========> " + port);
			if (proxyHost != null) {
				final InetSocketAddress sa = new InetSocketAddress(proxyHost, port);
				mProxy = new Proxy(Proxy.Type.HTTP, sa);
				return;
			}
		}
		mProxy = null;
	}

	/************************************************** POST ****************************************************/

	/**
	 * POST协议请求
	 */
	public synchronized static String postMethod(String urls, String params, String encode) {
		HttpURLConnection urlConn = null;
		MLog.a(sTag,"Start  POST------>  " + urls);
		MLog.a(sTag,"POST ARGS: ------>  " + params);
		// 判断是否使用以下3个协议
		boolean isconstant = false;
		if (urls.contains("update") || urls.contains("getaddr") || urls.contains("submitbug") || urls.contains("replace")) {
			isconstant = true;
		} else {
			isconstant = false;
		}

		StringBuffer buffer = new StringBuffer();
		try {
			urlConn = (HttpURLConnection) headMethod(urls, 0);
			// 因为这个是post请求,设立需要设置为true
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			// 设置以POST方式
			urlConn.setRequestMethod("POST");

			// Post 请求不能使用缓存
			urlConn.setUseCaches(false);
			// 是否跟随重定向
			urlConn.setInstanceFollowRedirects(true);

            if(urls.contains("https://")) {//https网络请求
                SSLContext sslContext = getSSLContextt(MyApplication.context);
                if(sslContext != null){
                    ((HttpsURLConnection)urlConn).setSSLSocketFactory(sslContext.getSocketFactory());
                }
            }

			/****************************** 提交配置 **************************************/
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			// urlConn.connect();
			// DataOutputStream流
			if (params != null && !params.equals("")) {
				if (isconstant) {
					params = AESSecurity.constantEncryptionResult(params, KEY);
				} else {
					params = AESSecurity.encryptionResult(params);
				}

				MLog.a(sTag,"POST ARGS: -----encode->  " + params);
				byte[] by = params.getBytes();
				// byte[] by = AESSecurity.encrypt(params,
				// MyApplication.getAppContext().getGameArgs().getKey()).getBytes();

				// 配置数据长度
				urlConn.setRequestProperty("Content-Length", String.valueOf(by.length));
				// 参数输出流
				DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
				// 将要上传的内容写入流中
				out.write(by);
				// 刷新、关闭
				out.flush();
				out.close();
			}

			int result = urlConn.getResponseCode();
			
			if (result == 200) {
				String temp = null;
				InputStream in = urlConn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
				}
				br.close();
				in.close();
			} else {
				PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exceptionCode:" + result);
				return "exception:" + result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
			return "exception:"+e.getMessage();
		}
		MLog.s("End Http Request");
		MLog.s("result："+buffer.toString());
		if (buffer.toString().contains("error") || buffer.toString().contains("exception") || buffer.toString().contains("Fatal")) {
			MLog.a(sTag,buffer.toString());
			return "exception Net:"+buffer.toString();
		}
		if (isconstant) {
			return AESSecurity.constantdecryptResult(buffer.toString(), KEY);
		}
		return AESSecurity.decryptResult(buffer.toString());
	}
	
	static RequestQueue requestQueue = null;
	/**
	 * http对外发送接口
	 * 
	 * @param urls
	 * @param params
	 * @param encode
	 */
	public static void startPost(final String urls, final String params, String encode) {
		MLog.s("Start Http Request");
		MLog.a("MySDK", urls);
		MLog.a("MySDK", params);

		// 判断是否使用以下3个协议
		boolean isconstant = false;
		if (urls.contains("update") || urls.contains("getaddr") || urls.contains("submitbug")) {
			isconstant = true;
		} else {
			isconstant = false;
		}
		String params_ =null;
		if (params != null && !params.equals("")) {
			if (isconstant) {
				params_ = AESSecurity.constantEncryptionResult(params, KEY);
			} else {
				params_ = AESSecurity.encryptionResult(params);
			}
		}

		requestQueue = (requestQueue ==null)? Volley.newRequestQueue(MyApplication.context):requestQueue;
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(isconstant,Method.POST, urls, params_, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				callback(urls,response.toString(),params);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exceptionResponse:"+error.getMessage());
				callback(urls,"exception",params);
			}
		});
		requestQueue.add(jsonRequest);
	}
	
	private static void callback(String urls, String response, String params) {
		String flag = null;
		if (urls.contains("=")) {
			int sh = urls.lastIndexOf("=");
			flag = urls.substring(sh + 1, urls.length());
		} else {
			flag = "httptest";
		}

		analyseData(flag, response, params);
	}

	private static void analyseData(String flag, String response, String params) {
		PhoneTool.disDialog();

		boolean errorweb=false;
		if (response==null||response.equals("")||response.contains("error")||response.contains("exception")||response.contains("Fatal")){
			errorweb = true;
		}
		if(errorweb){
			if (flag.contains("paylist")||flag.contains("asdkpay")){
				MySdkApi.getMpaycallBack().payFail("data_error");
				MySdkApi.getMact().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Activity act = MyGamesImpl.getInstance().getSdkact();
						if (act!=null){
							act.finish();
						}
					}
				});
			}else if(flag.equals("getwalletnonce")){
				nonceCallBack.callback(false,"");
			}
		}else if (flag.equals("paylist")){
			JSONObject data = null;
			try {
				data = new JSONObject(response);
				String code = data.getString("code");
				if (code.equals("0")){
					JSONArray result = data.getJSONArray("data");
					if(result.length()==0){
						MySdkApi.getMpaycallBack().payFail("paylist-length-empty");
					}else{
						gotoPayActivity(response);
					}
				}else if (code.equals("-1")){
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exceptionCode:"+data.getString("msg"));
					MySdkApi.getMpaycallBack().payFail(data.getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
				MySdkApi.getMpaycallBack().payFail(e.getMessage());
			}
		}else if (flag.equals("asdkpay")){
			JSONObject data = null;
			try {
				data = new JSONObject(response);
				String code = data.getString("code");
				if (code.equals("0")){
					JSONObject dataParam = data.getJSONObject("data");
					String feepoint = "",payconfirmurl = "";
					String wallet_nonce = "",wallet_authData = "",wallet_payData = "",wallet_authTo = "",wallet_payTo = "";
					String wemix_contract = "",wemix_column = "",wemix_fee = "",wemix_approvedhash = "",wemix_approveurl = "",wemix_limit = "";
					String orderid = ResourceUtil.getJsonItemByKey(dataParam,"orderid");
					feepoint = ResourceUtil.getJsonItemByKey(dataParam,"feepoint");
					payconfirmurl = ResourceUtil.getJsonItemByKey(dataParam,"url");
					wallet_authData = ResourceUtil.getJsonItemByKey(dataParam,"authData");
					wallet_payData = ResourceUtil.getJsonItemByKey(dataParam,"payData");
					wallet_authTo = ResourceUtil.getJsonItemByKey(dataParam,"authTo");
					wallet_payTo = ResourceUtil.getJsonItemByKey(dataParam,"payTo");
					wallet_nonce = ResourceUtil.getJsonItemByKey(dataParam,"nonce");
					wemix_contract = ResourceUtil.getJsonItemByKey(dataParam,"address");
					wemix_column = ResourceUtil.getJsonItemByKey(dataParam,"column");
					wemix_fee = ResourceUtil.getJsonItemByKey(dataParam,"fee");
					wemix_approvedhash = ResourceUtil.getJsonItemByKey(dataParam,"hash");
					wemix_approveurl = ResourceUtil.getJsonItemByKey(dataParam,"approveurl");
					wemix_limit = ResourceUtil.getJsonItemByKey(dataParam,"limit");
					HashMap<String,String> wemix_param = new HashMap<String,String>();
					wemix_param.put("wemix_contract",wemix_contract);
					wemix_param.put("wemix_column",wemix_column);
					wemix_param.put("wemix_fee",wemix_fee);
					wemix_param.put("wemix_approvedhash",wemix_approvedhash);
					wemix_param.put("wemix_approveurl",wemix_approveurl);
					wemix_param.put("wemix_limit",wemix_limit);
					m_callBack.callback(orderid,feepoint,payconfirmurl,
							wallet_authData,wallet_payData,wallet_authTo,wallet_payTo,wallet_nonce,wemix_param);
				}else {
					if(code.equals("400")){
//						int no_user = ResourceUtil.getStringId(MySdkApi.getMact(),"myths_no_user");
//						ToastUtils.Toast(MySdkApi.getMact().getResources().getString(no_user));
						ToastUtils.Toast(data.getString("msg"));
					}
					payFailed(data.getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
				payFailed(e.getMessage());
			}
		}else if(flag.equals("othersdkpay")){
			JSONObject data = null;
			try {
				data = new JSONObject(response);
				String code = data.getString("code");
				if (code.equals("0")){
					JSONObject dataParam = data.getJSONObject("data");
					String orderid = ResourceUtil.getJsonItemByKey(dataParam,"orderid");
					String payconfirmurl = ResourceUtil.getJsonItemByKey(dataParam,"paynotifyurl");
					String feepoint = ResourceUtil.getJsonItemByKey(dataParam,"extdata1");
					String extdata2 = ResourceUtil.getJsonItemByKey(dataParam,"extdata2");
					String extdata3 = ResourceUtil.getJsonItemByKey(dataParam,"extdata3");
					m_callBack.callback(orderid,feepoint,payconfirmurl,extdata2,extdata3,"","","",null);
				}else if (code.equals("-1")){
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exceptionCode:"+data.getString("msg"));
					payFailed(data.getString("msg"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
				payFailed(e.getMessage());
			}
		}else if(flag.equals("getwalletnonce")){
			JSONObject data = null;
			try {
				data = new JSONObject(response);
				String code = data.getString("code");
				if (code.equals("0")){
					String nonce = data.getString("nonce");
					nonceCallBack.callback(true,nonce);
				}else{
					nonceCallBack.callback(false,"");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				nonceCallBack.callback(false,"");
			}
		}
	}

	private static void payFailed(String msg){
		MySdkApi.getMpaycallBack().payFail(msg);
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Activity act = MyGamesImpl.getInstance().getSdkact();
				if (act!=null){
					act.finish();
				}
			}
		});
	}

	private static void gotoPayActivity(final String data){
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.disDialog();
				Intent itn = new Intent(MySdkApi.getMact(), PayActivity.class);
				itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				itn.putExtra("data",data);
				MySdkApi.getMact().startActivity(itn);
			}
		});

	}


	public static int getAppCode(){
		try {
			PackageInfo info = MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), 0);
			return info.versionCode; // 版本号
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
		
	}

	public static String getChannel()
	{
		String channel = "";
		PackageManager pm = MyApplication.context.getPackageManager();
		ApplicationInfo appInfo = null;
		try
		{
			appInfo = pm.getApplicationInfo(MyApplication.context.getPackageName(), PackageManager.GET_META_DATA);
			channel = appInfo.metaData.getString("DC_CHANNEL");
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return channel;
	}
	public static String m_AndriodID = null;
	public static String getAndroidID() 
    {
		if(m_AndriodID == null)
		{
			String szAndroidID = Secure.getString(MyApplication.context.getContentResolver(), Secure.ANDROID_ID);
			m_AndriodID = szAndroidID;
		}
		return m_AndriodID;
    }
	public static String getAppVersion() {
		String versionName = "";
		try {
			PackageManager pm = MyApplication.context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(MyApplication.context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "0.0.0";
			}
		} catch (Exception e) {
			Log.e("getAppVersion", "getAppVersion Exception", e);
		}
		return versionName;
	}

	/**
	 * 获取url中文件名
	 *
	 * @param urls
	 * @return
	 */
	public static String getUrlFileName(String urls) {
		String name = urls;
		if (name.contains("/")) {
			String[] arr = urls.split("/");
			name = arr[arr.length - 1];
		}
		if (name.contains("?")) {
			name = name.split("\\?")[0];
		}
		return name;
	}

	public static void checkupnet(final Activity context, final InitCallBack callBack) {
		//网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				int appcode= HttpUtils.getAppCode();
				JSONObject param = null;
				try {
					param = new JSONObject();
					param.put("version_appVersion", appcode+"");
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				String param_json = param.toString();
				MLog.a("init_param_json:"+param_json);
				Configs.initurl = Configs.initurl1;//初始化地址
				initZtsdk(context,param_json,Configs.initurl,callBack);
			}
		}).start();

	}

	private synchronized static void initZtsdk(Activity context,String param_json,String url,InitCallBack callBack){
		String checkupdata = HttpUtils.postMethod(url+"?gameparam=getaddr&safe=1", param_json, "utf-8");
		MLog.a("checkupdata:"+checkupdata);
		if(checkupdata!=null){
			if(checkupdata.contains("error")||checkupdata.contains("Fatal")||checkupdata.contains("exception")){
				if(url.contains("update")){
					String host = url.substring(url.indexOf("//")+2,url.lastIndexOf("/"));
					String host_ip = getip(host);
					url = url.replace(host,host_ip);
					initZtsdk(context,param_json,url,callBack);
				}else{
					if(!Configs.initurl.contains(Configs.initurl2)){
						Configs.initurl = Configs.initurl2;
						initZtsdk(context,param_json,Configs.initurl,callBack);
					}else{
						//初始化失败
						PhoneTool.submitSDKEvent("18",checkupdata);
						callBack.initSuccess(false,checkupdata);
					}
				}
			}else{
				try {
					JSONObject jo1 = new JSONObject(checkupdata);
					String code = jo1.getString("code");
					if ("0".equals(code)) {
						MLog.a("ztsdk init success");
						parseInitData(context,jo1,callBack);
					}else{
						//初始化失败
						PhoneTool.submitSDKEvent("18","code:"+code);
						callBack.initSuccess(false,"code"+code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					PhoneTool.submitSDKEvent("18","data-error:"+checkupdata);
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
					callBack.initSuccess(false,"data-error:"+checkupdata);
				}
			}
		}else{
			//初始化失败
			PhoneTool.submitSDKEvent("18","data-null");
			callBack.initSuccess(false,"data-null");
		}
	}

	private static void parseInitData(final Activity context,JSONObject initjs, final InitCallBack callBack) {
		try {
			Configs.accountserver = initjs.getJSONObject("data").getString("accsvrurl");
			Configs.payserver = initjs.getJSONObject("data").getString("payurl");

			Configs.othersdkextdata1=initjs.getJSONObject("data").getString("othersdkextdata1");
			Configs.othersdkextdata2=initjs.getJSONObject("data").getString("othersdkextdata2");
			Configs.othersdkextdata3=initjs.getJSONObject("data").getString("othersdkextdata3");
			Configs.othersdkextdata4=initjs.getJSONObject("data").getString("othersdkextdata4");
			Configs.othersdkextdata5=initjs.getJSONObject("data").getString("othersdkextdata5");

			callBack.initSuccess(true,"success");
			//初始化悬浮窗
			XfUtils.getInstance().openXf(context,Configs.getItem(Configs.kfUrl));
			//初始化成功上报
			PhoneTool.submitSDKEvent("17","init success");
			MyGamesImpl.getInstance().ADJSubmit(1,"");
		} catch (JSONException e) {
			e.printStackTrace();
			PhoneTool.submitSDKEvent("18","init-exception:"+e.getMessage());
			callBack.initSuccess(false,"init-exception:"+e.getMessage());
		}

	}

	public static void fastlogin(final Activity context) {
		PhoneTool.onCreateDialog(context,mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
				String name = sharedPreferences.getString("myths_youke_name","");
				if (!name.equals("")){
					gotoAutoLoginActivity("guest");
					PhoneTool.disDialog();
					return;
				}
				//向服务端获取当前设备对应的游客账号
				if (getguestaccount()){
					PhoneTool.disDialog();
					return;
				}
				String fastdata = HttpUtils.postMethod(Configs.accountserver+"gameparam=accautoreg", "", "utf-8");
				JSONObject fastdatajs = null;
				try {
					PhoneTool.disDialog();
					fastdatajs = new JSONObject(fastdata);
					if ("0".equals(fastdatajs.getString("code"))){
						String account=fastdatajs.getJSONObject("data").getString("account");
						String password=fastdatajs.getJSONObject("data").getString("password");

						sharedPreferences.edit().putString("myths_youke_name",account).apply();
						sharedPreferences.edit().putString("myths_youke_password",password).apply();

						acclogin("guest");

					}else {
						String msg=fastdatajs.getString("msg");
						ToastUtils.Toast(msg);
						//登录失败上报
						PhoneTool.submitSDKEvent("15",msg);
						MySdkApi.getLoginCallBack().loginFail(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					//登录失败上报
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"exception:"+e.getMessage());
					PhoneTool.submitSDKEvent("15","JSONException-data:"+fastdata);
					MySdkApi.getLoginCallBack().loginFail(fastdata);
				}

			}
		}).start();

	}

	private static boolean getguestaccount() {
		JSONObject param = null;
		String param_json=  "";
		try {
			param = new JSONObject();
			param.put("idfa", PhoneTool.getIMEI(MyApplication.context));
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		param_json = param.toString();
		String data = HttpUtils.postMethod(Configs.accountserver+"gameparam=getguestaccountlist", param_json, "utf-8");
		JSONObject datajs = null;
		try {
			datajs = new JSONObject(data);
			if ("0".equals(datajs.getString("code"))){
				SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
				String accountlist=datajs.getJSONObject("data").getString("accountlist");
				String accguest = accountlist.split(",")[0];
				if("".equals(accguest)){
					return false;
				}
				sharedPreferences.edit().putString("myths_youke_name",accguest).apply();
				sharedPreferences.edit().putString("myths_youke_password","").apply();

				acclogin("guest");
				return true;
			}else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param type//"guest"游客自动登录；"facebook"facebook自动登录
	 */
	public static void gotoAutoLoginActivity(final String type){
		MLog.a("gotoAutoLoginActivity--"+type);
		if ("facebook".equals(type)){
			GraphRequest request = GraphRequest.newMeRequest(
					AccessToken.getCurrentAccessToken(),
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject object, GraphResponse response) {
							// Insert your code here
							if (object!=null){
								MyGamesImpl.getInstance().setUserfbinfo(object.toString());
								MLog.a("GraphRequest-"+object.toString());
							}else{
								MLog.a("object-null");
							}
						}
					});

			Bundle parameters = new Bundle();
			parameters.putString("fields", "email,name");
			request.setParameters(parameters);
			request.executeAsync();
		}
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.disDialog();
				MLog.a("gotoAutoLoginActivity--begin");
				Intent itn = new Intent(MySdkApi.getMact(), AutoLoginActivity.class);
				itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				itn.putExtra("type",type);
				MySdkApi.getMact().startActivity(itn);
				MLog.a("gotoAutoLoginActivity--end");
			}
		});

	}

	//跳转老账号登录
	public static void gotoAccLoginActivity(){

		SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
		String name = sharedPreferences.getString("myths_oldacc_name","");
		if (!name.equals("")){
			gotoAutoLoginActivity("oldacc");
			return;
		}

		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.disDialog();
				MLog.a("gotoAccLoginActivity--begin");
				Intent itn = new Intent(MySdkApi.getMact(), AccLoginActivity.class);
				itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MySdkApi.getMact().startActivity(itn);
				MLog.a("gotoAccLoginActivity--end");
			}
		});
	}

	//游客及账号自动登录
	public static void acclogin(String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
				String name= "",password="";
				if("guest".equals(type)){
					name = MyGamesImpl.getSharedPreferences().getString("myths_youke_name","");
					password = MyGamesImpl.getSharedPreferences().getString("myths_youke_password","");
				}else if("oldacc".equals(type)){
					name = MyGamesImpl.getSharedPreferences().getString("myths_oldacc_name","");
					password = MyGamesImpl.getSharedPreferences().getString("myths_oldacc_password","");
				}

				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
					param.put("username", name);
					param.put("userpassword", password);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(Configs.accountserver+"gameparam=acclogin", param_json, "utf-8");
				JSONObject logindatajs = null;
				try {
					logindatajs = new JSONObject(logindata);
					if ("0".equals(logindatajs.getString("code"))){
						String accountid = logindatajs.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = logindatajs.getJSONObject("data").getJSONObject("account").getString("sessionid");

						//保存登录类型用于自动登录
						if("guest".equals(type)){
							//自动登录成功上报
							PhoneTool.submitSDKEvent("14","guest login success");
							MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid",accountid).apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","guest").apply();
						}else if("oldacc".equals(type)){
							MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_accountid",accountid).apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","oldacc").apply();
						}


						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"0","");
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).apply();
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						String msg=logindatajs.getString("msg");
						if("guest".equals(type)){
							MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").apply();
						}
						//登录失败上报
						PhoneTool.submitSDKEvent("15",msg);
						MySdkApi.getLoginCallBack().loginFail(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					//登录失败上报
					PhoneTool.submitSDKEvent("15","JSONException2-data:"+logindata);
					MySdkApi.getLoginCallBack().loginFail(logindata);
				}

				PhoneTool.disDialog();
			}
		}).start();

	}

	//老账号登录
	public static void acclogin(final Activity act,String name,String password) {
		PhoneTool.onCreateDialog(act,mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
					param.put("username", name);
					param.put("userpassword", password);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(Configs.accountserver+"gameparam=acclogin", param_json, "utf-8");
				JSONObject logindatajs = null;
				try {
					logindatajs = new JSONObject(logindata);
					if ("0".equals(logindatajs.getString("code"))){
						String accountid = logindatajs.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = logindatajs.getJSONObject("data").getJSONObject("account").getString("sessionid");

						//保存老账号id
						MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_accountid",accountid).commit();
						//保存老账号及密码
						MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_name",name).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_password",password).commit();
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","oldacc").commit();

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"0","");
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();
						MyGamesImpl.getInstance().ADJSubmit(2,"");

						PhoneTool.disDialog();
						return;

					}else{
						String msg=logindatajs.getString("msg");
						ToastUtils.Toast(msg);
						MySdkApi.getLoginCallBack().loginFail(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail(logindata);
				}
				PhoneTool.disDialog();
			}
		}).start();;

	}
	//fblogin
	public static void fblogin_check(final String uid ,final String token,final String type) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setfbaccount";
				String autotype = MyGamesImpl.getSharedPreferences().getString("myths_auto_type","");
				try {
					param = new JSONObject();
					param.put("fbid", uid);
					param.put("input_token", token);
					param.put("extend", MyGamesImpl.getInstance().getUserfbinfo());
					if ("bind".equals(type)||"gamebindfb".equals(type)){
						param.put("action", "bund");
						param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));
					}
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String fblogindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject fbloginjs = null;
				try {
					fbloginjs = new JSONObject(fblogindata);
					String code= fbloginjs.getString("code");
					if ("0".equals(code)||"1".equals(code)){//1表示已有facebook账号，绑定失败，直接登录facebook账号
						if ("bind".equals(type)||"gamebindfb".equals(type)){
							if ("1".equals(code)){
								String msg=fbloginjs.getString("msg");
								ToastUtils.Toast(msg);
								MySdkApi.getLoginCallBack().loginFail(code);
								PhoneTool.disDialog();
								return;
							}else{//绑定facebook成功，清除游客或老账号信息
								if("gamebindfb".equals(type)&&"oldacc".equals(autotype)){
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_accountid","").commit();
								}else{
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid","").commit();
								}
							}
						}

						//保存facebook登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_fbid",uid).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_input_token",token).commit();

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","facebook").commit();

						String accountid = fbloginjs.getJSONObject("data").getString("accountid");
						String sessionid = fbloginjs.getJSONObject("data").getString("sessionid");

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();
						String fbname=MyGamesImpl.getSharedPreferences().getString("myths_fbname","");
						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"facebook",fbname);
						if ("".equals(type)) {
							//FB登录成功上报
							PhoneTool.submitSDKEvent("20","");
							//保存登录信息
							MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
							MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						}
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					} else{
						String msg=fbloginjs.getString("msg");
						ToastUtils.Toast(msg);
						MySdkApi.getLoginCallBack().loginFail(code);
						if ("".equals(type)){
							//FB登录失败上报
							PhoneTool.submitSDKEvent("19",msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
					if ("".equals(type)){
						//FB登录失败上报
						PhoneTool.submitSDKEvent("19","JSONException");
					}
				}
				PhoneTool.disDialog();
			}
		}).start();
	}
	//google登录验证
	public static void googlelogin_check(String googleid,String googleidtoken) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setgoogleaccount";
				param = new JSONObject();
				try {
					param.put("idtoken", googleidtoken);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				param_json = param.toString();
				String googlelogindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject googleloginjs = null;
				try {
					googleloginjs = new JSONObject(googlelogindata);
					String code= googleloginjs.getString("code");
					if ("0".equals(code)){
						//保存google登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleid",googleid).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleidtoken",googleidtoken).commit();

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","google").commit();

						String accountid = googleloginjs.getJSONObject("data").getString("accountid");
						String sessionid = googleloginjs.getJSONObject("data").getString("sessionid");
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"google","");
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						//登录失败，清除google登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleid","").commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleidtoken","").commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").commit();

						String msg=googleloginjs.getString("msg");
						MySdkApi.getLoginCallBack().loginFail(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail(googlelogindata);
				}

				PhoneTool.disDialog();
			}
		}).start();
	}


	//google登录验证
	public static void googlelogin_check(String googleid,String googleidtoken,final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setgoogleaccount";
				String autotype = MyGamesImpl.getSharedPreferences().getString("myths_auto_type","");

				param = new JSONObject();
				try {
					param.put("idtoken", googleidtoken);
					if ("bind".equals(type)||"gamebindfb".equals(type)){
						param.put("action", "bund");
						param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				param_json = param.toString();
				String googlelogindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject googleloginjs = null;
				try {
					googleloginjs = new JSONObject(googlelogindata);
					String code= googleloginjs.getString("code");
					if ("0".equals(code)||"1".equals(code)){
						//1表示已有账号，绑定失败
						if ("bind".equals(type)||"gamebindgg".equals(type)){
							if ("1".equals(code)){
								String msg=googleloginjs.getString("msg");
								ToastUtils.Toast(msg);
								MySdkApi.getLoginCallBack().loginFail(code);
								PhoneTool.disDialog();
								return;
							}else{//绑定成功，清除游客或老账号信息
								if("gamebindgg".equals(type)&&"oldacc".equals(autotype)){
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_accountid","").commit();
								}else{
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid","").commit();
								}

							}
						}

						//保存google登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleid",googleid).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_googleidtoken",googleidtoken).commit();

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","google").commit();

						String accountid = googleloginjs.getJSONObject("data").getString("accountid");
						String sessionid = googleloginjs.getJSONObject("data").getString("sessionid");
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();
						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"google",googleid);
						if ("".equals(type)) {
							//保存登录信息
							MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
							MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						}
						PhoneTool.submitSDKEvent("8","google login success");
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						String msg=googleloginjs.getString("msg");
						ToastUtils.Toast(msg);
						MySdkApi.getLoginCallBack().loginFail(code);
						if ("".equals(type)){
							//登录失败，清除google登录信息
							MyGamesImpl.getSharedPreferences().edit().putString("myths_googleid","").commit();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_googleidtoken","").commit();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").commit();

							//登录失败上报
							PhoneTool.submitSDKEvent("9",msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
					if ("".equals(type)){
						// 登录失败上报
						PhoneTool.submitSDKEvent("9","JSONException-data:"+googlelogindata);
					}
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	//apple登录验证
    public static void applelogin_check(String appleid,String appleemail) {
        PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject param = null;
                String param_json=  "";
                String url = Configs.accountserver+"gameparam=setappleaccount";
                param = new JSONObject();
                try {
                    param.put("appleid", appleid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                param_json = param.toString();
                String applelogindata = HttpUtils.postMethod(url, param_json, "utf-8");
                JSONObject appleloginjs = null;
                try {
                    appleloginjs = new JSONObject(applelogindata);
                    String code= appleloginjs.getString("code");
                    if ("0".equals(code)){
                        //保存apple登录信息
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleid",appleid).commit();
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleemail",appleemail).commit();

                        //保存登录类型用于自动登录
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","apple").commit();

                        String accountid = appleloginjs.getJSONObject("data").getString("accountid");
                        String sessionid = appleloginjs.getJSONObject("data").getString("sessionid");
                        //保存登录信息
                        MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
                        MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);

                        MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"3","");
                        MyGamesImpl.getInstance().ADJSubmit(2,"");
                    }else{
                        //登录失败，清除apple登录信息
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleid","").commit();
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleemail","").commit();
                        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").commit();

                        String msg=appleloginjs.getString("msg");
                        MySdkApi.getLoginCallBack().loginFail(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    MySdkApi.getLoginCallBack().loginFail(applelogindata);
                }

                PhoneTool.disDialog();
            }
        }).start();
    }


	//apple登录验证
	public static void applelogin_check(String appleid,String appleemail,final String type) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setappleaccount";
				String autotype = MyGamesImpl.getSharedPreferences().getString("myths_auto_type","");
				MLog.a("Apple login--Success>---url="+url);
				param = new JSONObject();
				try {
					param.put("userID", appleid);
					param.put("extend", MyGamesImpl.getInstance().getUserfbinfo());
					if ("bind".equals(type)||"gamebindfb".equals(type)){
						url = Configs.accountserver+"gameparam=guesttoappleaccount";
						String name="",accountid="";
						if("gamebindfb".equals(type)&&"oldacc".equals(autotype)){
							name = MyGamesImpl.getSharedPreferences().getString("myths_oldacc_name","");
							accountid = MyGamesImpl.getSharedPreferences().getString("myths_oldacc_accountid","");
						}else{
							name = MyGamesImpl.getSharedPreferences().getString("myths_youke_name","");
							accountid = MyGamesImpl.getSharedPreferences().getString("myths_youke_accountid","");
						}

						param.put("accountid", accountid);
						param.put("guestusername", name);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				param_json = param.toString();
				String applelogindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject appleloginjs = null;
				try {
					appleloginjs = new JSONObject(applelogindata);
					String code= appleloginjs.getString("code");
					if ("0".equals(code)||"1".equals(code)){//1表示已有账号，绑定失败，直接登录账号

						if ("bind".equals(type)||"gamebindapple".equals(type)){
							if ("1".equals(code)){
								String msg=appleloginjs.getString("msg");
								ToastUtils.Toast(msg);
								if ("gamebindapple".equals(type)){//游戏内调用绑定返回失败
									MySdkApi.getBindcallBack().bindFail(msg);
									PhoneTool.disDialog();
									return;
								}
							}else{//绑定成功，清除游客或老账号信息
								if("gamebindapple".equals(type)&&"oldacc".equals(autotype)){
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_accountid","").commit();
								}else{
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_password","").commit();
									MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid","").commit();
								}

							}
						}

						//保存apple登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_appleid",appleid).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_appleemail",appleemail).commit();

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","apple").commit();


						if ("gamebindfb".equals(type)) {//游戏内调用绑定返回成功
							MySdkApi.getBindcallBack().bindSuccess(appleid);
						}else{
							//登录成功上报
							String accountid = appleloginjs.getJSONObject("data").getString("accountid");
							String sessionid = appleloginjs.getJSONObject("data").getString("sessionid");
							//保存登录信息
							MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
							MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);

							MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"3","");
							MLog.a("Apple login--Success>"+accountid+sessionid);
						}

						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						//登录失败，清除apple登录信息
						String msg=appleloginjs.getString("msg");
						ToastUtils.Toast(msg);
						if ("gamebindapple".equals(type)){//游戏内调用绑定返回失败
							MySdkApi.getBindcallBack().bindFail(msg);
						}else{

							MyGamesImpl.getSharedPreferences().edit().putString("myths_appleid","").commit();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_appleemail","").commit();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").commit();


							MySdkApi.getLoginCallBack().loginFail(msg);
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
					if ("gamebindfb".equals(type)){//游戏内调用绑定返回失败
						MySdkApi.getBindcallBack().bindFail("");
					}else{
						MySdkApi.getLoginCallBack().loginFail(applelogindata);
					}


				}

				PhoneTool.disDialog();
			}
		}).start();
	}




	//----已棄用-------------------------您必须对应用内商品发送消耗请求，然后 Google Play 才能允许再次购买
//	public static void consumePurchase(final IInAppBillingService mService,final String purchaseToken) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					int response = mService.consumePurchase(3, MyApplication.context.getPackageName(), purchaseToken);
//					MLog.a("consumePurchase------>"+response);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}

	public static void getPayList() {
		JSONObject param = null;
		String param_json=  "";
		OrderInfo orderInfo = MyApplication.getAppContext().getOrderinfo();
		try {
			param = new JSONObject();
			param.put("isAnyAmount", orderInfo.isAnyAmount());
			param.put("method", orderInfo.getUseCase());
			param.put("zdpaylist", orderInfo.getPayTypes());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		param_json = param.toString();
		HttpUtils.startPost(Configs.payserver+"gameparam=paylist",param_json,"utf-8");
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
	}

	static GetorderCallBack m_callBack;
	public static void getpayorder(String paytypeid, String walletaddress, String chainId, GetorderCallBack callBack) {
		m_callBack=callBack;
		JSONObject param = null;
		String param_json=  "";
		OrderInfo orderinfo=MyApplication.getAppContext().getOrderinfo();
		String wemixaccesstoken = MyGamesImpl.getSharedPreferences().getString("myths_wemixaccesstoken","");
		try {
			param = new JSONObject();
			param.put("sum", orderinfo.getAmount());
			param.put("callbackurl", orderinfo.getPayurl());
			param.put("paytypeid",paytypeid);
			param.put("approvedAddress",walletaddress);
			param.put("chainid",chainId);
			param.put("customorderid", orderinfo.getTransactionId());
			param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));

			param.put("custominfo", orderinfo.getExtraInfo());
			param.put("desc", orderinfo.getProductname());
			param.put("bundleid", MyApplication.context.getPackageName());
			param.put("myfeepoint", orderinfo.getFeepoint());
			param.put("access_token", wemixaccesstoken);
			param.put("method", MyApplication.getAppContext().getOrderinfo().getUseCase());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		param_json = param.toString();
		PhoneTool.onCreateDialog(MyGamesImpl.getInstance().getSdkact(),mTips,mLoading);
		HttpUtils.startPost(Configs.payserver+"gameparam=asdkpay",param_json,"utf-8");
	}

	//第三方支付下单
	public static void getOtherPayOrder(Activity act,String paytypeid, GetorderCallBack callBack) {
		m_callBack=callBack;
		JSONObject param = null;
		String param_json=  "";
		OrderInfo orderinfo=MyApplication.getAppContext().getOrderinfo();
		try {
			param = new JSONObject();
			param.put("sum", orderinfo.getAmount());
			param.put("callbackurl", orderinfo.getPayurl());
			param.put("paytypeid",paytypeid);
			param.put("customorderid", orderinfo.getTransactionId());
//			param.put("accountid", MyApplication.getAppContext().getGameArgs().getAccount_id());
			param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));

			param.put("custominfo", orderinfo.getExtraInfo());
			param.put("desc", orderinfo.getProductname());
			param.put("bundleid", MyApplication.context.getPackageName());
			param.put("myfeepoint", orderinfo.getFeepoint());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		param_json = param.toString();
		PhoneTool.onCreateDialog(act,mTips,mLoading);
		HttpUtils.startPost(Configs.payserver+"gameparam=othersdkpay",param_json,"utf-8");
	}


	//gp支付验证
	public static void consumePurchaseSDK(final String order, final String packageName, final String productId, final String token, final PayConsumeCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
//					param_js.put("signtureDataTemp", purchaseData);
//					param_js.put("signtureTemp", dataSignature);
					param_js.put("orderid", order);
					param_js.put("packageName", packageName);
					param_js.put("productId", productId);
					param_js.put("token", token);
//					param_js.put("purchase", purchase);


					String param = param_js.toString();
					String url = "".equals(Configs.gp_url)?MyGamesImpl.getSharedPreferences().getString("gp_url",""):Configs.gp_url;
					final String result = HttpUtils.postMethod(url, param, "utf-8");
					MLog.a(result);
					JSONObject js_result = new JSONObject(result);
					String code = js_result.getString("code");
					String money = ResourceUtil.getJsonItemByKey(js_result,"money");
					if(code.equals("0")){
						callback.result(true,money);
					}else{
						callback.result(false,"code:"+code);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.result(false,"Exception:"+e1.getMessage());
				}

			}
		}).start();;
	}

	//荣耀支付验证
	public static void honorPurchaseSDK(final String order,String token,final PayConsumeCallback callback){

		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
//					param_js.put("signtureDataTemp", purchaseData);
//					param_js.put("signtureTemp", dataSignature);
 				param_js.put("orderid", order);
//					param_js.put("packageName", packageName);
//					param_js.put("productId", productId);
					param_js.put("token", token);
//					param_js.put("purchase", purchase);


					String param = param_js.toString();
					String url = "".equals(Configs.honor_url)?MyGamesImpl.getSharedPreferences().getString("honor_url",""):Configs.honor_url;
					final String result = HttpUtils.postMethod(url, param, "utf-8");
					MLog.a(result);
					JSONObject js_result = new JSONObject(result);
					String code = js_result.getString("code");
					if(code.equals("0")){
						callback.result(true,"success");
					}else{
						callback.result(false,"code:"+code);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.result(false,"Exception:"+e1.getMessage());
				}

			}
		}).start();;
	}

	//华为支付验证
	public static void hwPurchaseComsume(String url,String orderId,String extend,String purchaseToken,String formattedTime){
		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("orderid", orderId);
					param_js.put("extend", extend);
					param_js.put("payBackTime", formattedTime);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				String param = param_js.toString();
				final String result = HttpUtils.postMethod(url, param, "utf-8");
				MLog.a(result);
				try {
					if (result.contains("exception")) {
						MLog.a("-----------huaweipayconfirm--exception-------------");
						if(MySdkApi.getMpaycallBack()!=null)
							MySdkApi.getMpaycallBack().payFail("huaweipayconfirm--exception");
					}else{
						JSONObject data = new JSONObject(result);
						if ("0".equals(data.getString("code"))) {
							MyGamesImpl.getInstance().ADJSubmit(4,orderId,ResourceUtil.getJsonItemByKey(data,"money"));
							//华为确认补单
							try {
								SkipUtil.consumeOwnedPurchase(purchaseToken);
							} catch (Error e1) {
								e1.printStackTrace();
							}
							if(MySdkApi.getMpaycallBack()!=null)
								MySdkApi.getMpaycallBack().payFinish();
						}else{
							PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"hwPurchaseComsume:"+result);
							if(MySdkApi.getMpaycallBack()!=null)
								MySdkApi.getMpaycallBack().payFail("huaweipayconfirm--faile");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	//第三方支付验证
	public static void consumePurchase(String url,String order, String productId, String token, PayConsumeCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("orderid", order);
					param_js.put("packageName", MyApplication.context.getPackageName());
					param_js.put("productId", productId);
					param_js.put("token", token);

					String param = param_js.toString();
					final String result = HttpUtils.postMethod(url, param, "utf-8");
					MLog.a(result);
					JSONObject js_result = new JSONObject(result);
					String code = js_result.getString("code");
					if(code.equals("0")){
						callback.result(true,ResourceUtil.getJsonItemByKey(js_result,"money"));
					}else{
						String pub = FilesTool.getPublisherStringContent().split("sdk_")[0];
						PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),pub+"-PurchaseComsume:"+result);
						callback.result(false,"code:"+code);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.result(false,"Exception:"+e1.getMessage());
				}
			}
		}).start();;
	}

//	//gp支付验证
//	public static void consumePurchaseSDK(final String order,final String packageName,final String productId, final String token,final PayConsumeCallback callback) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				JSONObject param_js = new JSONObject();
//				try {
////					param_js.put("signtureDataTemp", purchaseData);
////					param_js.put("signtureTemp", dataSignature);
//					param_js.put("orderid", order);
//					param_js.put("packageName", packageName);
//					param_js.put("productId", productId);
//					param_js.put("token", token);
//
//					String param = param_js.toString();
//					String url = "".equals(Configs.gp_url)?MyGamesImpl.getSharedPreferences().getString("gp_url",""):Configs.gp_url;
//					final String result = HttpUtils.postMethod(url, param, "utf-8");
//					MLog.a(result);
//					JSONObject js_result = new JSONObject(result);
//					String code = js_result.getString("code");
//					if(code.equals("0")){
//						callback.result(true,"success");
//					}else{
//						callback.result(false,"code:"+code);
//					}
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					callback.result(false,"Exception:"+e1.getMessage());
//				}
//
//			}
//		}).start();;
//	}
	//onestore支付验证
	public static void consumeOneStorePurchase(final String order, final String purchaseData,
											   final String dataSignature, PayConsumeCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("orderid", order);
					param_js.put("signtureDataTemp", purchaseData);
					param_js.put("signtureTemp", dataSignature);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String param = param_js.toString();
				String url = "".equals(Configs.onestore_url)?MyGamesImpl.getSharedPreferences().getString("onestore_url",""):Configs.onestore_url;
				final String result = HttpUtils.postMethod(url, param, "utf-8");
				MLog.a(result);
				try {
					JSONObject data = new JSONObject(result);
					if(data.getString("code").equals("0")){
						callback.result(true,"");
					}else{
						callback.result(false,data.getString("msg"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callback.result(false,e.getMessage());
				}
			}
		}).start();;
	}

	//上报角色信息
	public static void submitRoleData(final int operator, final GameRoleBean gameRoleBean) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("serverId", gameRoleBean.getGameZoneId());
					param_js.put("serverName", gameRoleBean.getGameZoneName());
					param_js.put("roleId", gameRoleBean.getRoleId());
					param_js.put("roleName", gameRoleBean.getRoleName());
					param_js.put("roleLevel", gameRoleBean.getRoleLevel());
					param_js.put("vipLevel", gameRoleBean.getVipLevel());
					param_js.put("RoleCTime", gameRoleBean.getRoleCTime());
					param_js.put("operator", operator);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=usergameinfo", param, "utf-8");
				MLog.a(result);
			}
		}).start();
	}

    public static SSLContext getSSLContextt(Context context) {
        try{
            // 生成SSLContext对象
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 从assets中加载证书
            InputStream inStream = context.getAssets().open(KEY_STORE_TRUST_PATH);

            // 证书工厂
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(inStream);

            // 密钥库
            KeyStore kStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            kStore.load(null, null);
            kStore.setCertificateEntry("trust", cer);// 加载证书到密钥库中

            // 密钥管理器
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(kStore, KEY_STORE_TRUST_PASSWORD.toCharArray());// 加载密钥库到管理器

            // 信任管理器
            TrustManagerFactory tFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tFactory.init(kStore);// 加载密钥库到信任管理器

            // 初始化
            sslContext.init(keyFactory.getKeyManagers(), tFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        }catch(Exception e){
            return null;
        }

    }

	private static String getip(String host){
		InetAddress inetAddress;
		String hostAddress = "";
		try {
			inetAddress = InetAddress.getByName(host);
			hostAddress = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostAddress;
	}

	public static void getPhoneCode(String areaCode,String num, PhoneCodeCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param_js = new JSONObject();
				try {

					param_js.put("quhao",areaCode);
					param_js.put("phone",num);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=sendphonecode", param, "utf-8");
				MLog.a(result);
				//{"code":"0","msg":"OK"}
				try {
					JSONObject data = new JSONObject(result);

					if(data.getString("code").equals("0")){

						String msg=data.getString("msg");
						callBack.getCodeSuccess(true,msg);
					}else{

						callBack.getCodeSuccess(false,data.getString("code"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
//					callback.result(false,e.getMessage());
					callBack.getCodeSuccess(false,"1005");
				}


			}
		}).start();
	}

	public static void getEmailCode(Activity context, String email, EmailCodeCallBack callBack) {
		PhoneTool.onCreateDialog(context,mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param_js = new JSONObject();
				try {
					param_js.put("email",email);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=sendemailcode", param, "utf-8");
				MLog.a(result);
				//{"code":"0","msg":"OK"}
				try {
					JSONObject data = new JSONObject(result);

					if(data.getString("code").equals("0")){
						callBack.getCodeResult(true,"success");
					}else{
						callBack.getCodeResult(false,data.getString("code"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callBack.getCodeResult(false,"exception-result->"+result);
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void getUnBindSDK(String areaCode,String phoneNum,String code, String loginType, UnBindSDKCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param_js = new JSONObject();
				try {
					param_js.put("quhao",areaCode);
					param_js.put("phone",phoneNum);
					param_js.put("verifycode",code);
					param_js.put("accountid",MyApplication.getAppContext().getGameArgs().getAccount_id());
					param_js.put("sessionid",MyApplication.getAppContext().getGameArgs().getSession_id());
					param_js.put("accounttype",loginType);


				} catch (JSONException e) {
					e.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=unbunddsfaccount", param, "utf-8");
				MLog.a(result);
				//{"code":"0","msg":"成功"}  {"code":"-1","msg":"验证码已过期"}
				try {
					JSONObject jsonObject=new JSONObject(result);
					String code=jsonObject.getString("code");

					if ("0".equals(code)){

						callBack.unBindSuccess(true,phoneNum);
					}else{
						callBack.unBindSuccess(false,code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callBack.unBindSuccess(false,"1005");
				}

			}
		}).start();
	}

	public static void getbindPhone(String type,String areaCode,String phoneNum, String code, String password, BindPhoneCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param_js = new JSONObject();
				try {
					param_js.put("quhao",areaCode);
					param_js.put("phone",phoneNum);
					param_js.put("verifycode",code);
					param_js.put("accountid",MyApplication.getAppContext().getGameArgs().getAccount_id());
					param_js.put("password",password);
					if(type.equals("change")){
						param_js.put("action","change");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=setphone", param, "utf-8");
				MLog.a(result);
				try {
					JSONObject jsonObject=new JSONObject(result);
					String code=jsonObject.getString("code");

					if ("0".equals(code)){


						MyGamesImpl.getSharedPreferences().edit().putString("myths_phone",phoneNum).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_passeword",password).commit();


						callBack.bindSuccess(jsonObject.getJSONObject("data").getString("phone"));

					}else{

						callBack.bindFail(code);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					callBack.bindFail("1005");
				}

			}
		}).start();
	}

	public static void getPhoneLogin(String areaCode,String phoneNum, String code, String password, LoginCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param_js = new JSONObject();
				try {
					param_js.put("quhao",areaCode);
					param_js.put("phone",phoneNum);
					param_js.put("verifycode",code);
					param_js.put("password",password);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=regbyphone", param, "utf-8");
				MLog.a(result);

				try {
					JSONObject phoneJson=new JSONObject(result);
					String code=phoneJson.getString("code");

					if ("0".equals(code)){
						//保存登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_phone",phoneNum).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_passeword",password).commit();

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","phone").commit();

//						String accountid = phoneJson.getJSONObject("data").getString("accountid");
//						String sessionid = phoneJson.getJSONObject("data").getString("sessionid");

						String accountid = phoneJson.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = phoneJson.getJSONObject("data").getJSONObject("account").getString("sessionid");


						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"phone",phoneNum);
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{

						MyGamesImpl.getSharedPreferences().edit().putString("myths_phone","").commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_passeword","").commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").commit();

//						String msg=phoneJson.getString("msg");
						MySdkApi.getLoginCallBack().loginFail(code);
					}

				} catch (JSONException e) {
					e.printStackTrace();

					MySdkApi.getLoginCallBack().loginFail("1005");
				}


			}
		}).start();
	}

	public static void getEmailLogin(Activity context, String email,String code,boolean isykBind) {
		PhoneTool.onCreateDialog(context,mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("email",email);
					param_js.put("verifycode",code);
					if(isykBind)param_js.put("action","bund");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String param = param_js.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=regbyemail", param, "utf-8");
				MLog.a(result);
				try {
					JSONObject dataJson=new JSONObject(result);
					String code=dataJson.getString("code");
					if ("0".equals(code)){
						//保存登录信息
						String psd = dataJson.getJSONObject("data").getString("password");
						MyGamesImpl.getSharedPreferences().edit().putString("myths_email",email).apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_email_psd",psd).apply();
						//保存登录类型
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","email").apply();
						if(isykBind){
							MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_password","").apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid","").apply();
						}else{
							String accountid = dataJson.getJSONObject("data").getString("accountid");
							String sessionid = dataJson.getJSONObject("data").getString("sessionid");
							//保存登录信息
							MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
							MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
							MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).apply();
							MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"email",email);
							MyGamesImpl.getInstance().ADJSubmit(2,"");
						}
						if(context instanceof EmailLoginActivity){
							context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									try {
										context.finish();
									}catch (Exception e){
									}
								}
							});
						}
					}else{
						if(isykBind){
							String msg=dataJson.getString("msg");
							ToastUtils.Toast(msg);
						}else{
							MyGamesImpl.getSharedPreferences().edit().putString("myths_email","").apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_email_psd","").apply();
							MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").apply();
							MySdkApi.getLoginCallBack().loginFail(code);
						}
						PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"email-failResult:" + result);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if(!isykBind) MySdkApi.getLoginCallBack().loginFail("1005");
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"email-JSONException:" + e.getMessage()+";result:"+result);
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void getEmailLoginByPsd(String email, String psd) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
					param.put("username", email);
					param.put("userpassword", psd);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(Configs.accountserver+"gameparam=acclogin", param_json, "utf-8");
				JSONObject logindatajs = null;
				try {
					logindatajs = new JSONObject(logindata);
					if ("0".equals(logindatajs.getString("code"))){
						String accountid = logindatajs.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = logindatajs.getJSONObject("data").getJSONObject("account").getString("sessionid");
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","email").apply();

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"email",email);
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).apply();

						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						MyGamesImpl.getSharedPreferences().edit().putString("myths_email","").apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_email_psd","").apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","").apply();
						MySdkApi.getLoginCallBack().loginFail(logindatajs.getString("code"));
						PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"email-psd-failResult:" + logindatajs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
					PhoneTool.submitErrorEvent(Configs.getInReqFailCode(),"email-psd-JSONException:" + e.getMessage()+";result:"+logindatajs);
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

    public static void getCheckPhoneCode(String areaCode,String phoneNum, String code, CheckCodeCallBack callBack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject=new JSONObject();
				try {
					jsonObject.put("quhao",areaCode);
					jsonObject.put("phone",phoneNum);
					jsonObject.put("verifycode",code);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String param = jsonObject.toString();
				final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=checkphonecode", param, "utf-8");
				MLog.a(result);

				try {
					JSONObject json=new JSONObject(result);
					String code = json.getString("code");

					if ("0".equals(code)){
						callBack.CheckSuccess(json.getString("msg"));
					}else{
						callBack.CheckFail(code);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					callBack.CheckFail("1005");
				}

			}
		}).start();

	}

	public static void getPhoneLogin2(String areaCode,String phoneNum, String password, LoginCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
		 			param.put("quhao",areaCode);
					param.put("username", phoneNum);
					param.put("userpassword", password);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(Configs.accountserver+"gameparam=acclogin", param_json, "utf-8");
				JSONObject logindatajs = null;
				try {
					logindatajs = new JSONObject(logindata);
					if ("0".equals(logindatajs.getString("code"))){

						String accountid = logindatajs.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = logindatajs.getJSONObject("data").getJSONObject("account").getString("sessionid");

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","phone").commit();

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"phone",phoneNum);
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();

						MyGamesImpl.getInstance().ADJSubmit(2,"");


					}else{
						MySdkApi.getLoginCallBack().loginFail(logindatajs.getString("code"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
				}

			}
		}).start();



	}

	public static void getChangePassword(String areaCode,String phoneNum, String oldPassword, String newPassword, ChangePasswordCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
					param.put("quhao", areaCode);
					param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));
					param.put("username", phoneNum);
					param.put("oldpassword", oldPassword);
					param.put("newuserpassword", newPassword);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=changepassword", param_json, "utf-8");
				MLog.a(result);

				try {
					JSONObject datajson=new JSONObject(result);
					String code=datajson.getString("code");

					if ("0".equals(code)){
						callBack.Success(datajson.getJSONObject("data").getString("accountid"));
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",datajson.getJSONObject("data").getString("accountid")).commit();
//						callBack.CheckSuccess(datajson.getString("msg"));
					}else{
						callBack.Fail(code);
//						callBack.CheckFail(datajson.getString("msg"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
					callBack.Fail("1005");
				}
			}
		}).start();

	}

	public static void forgetPassword(String areaCode, String phoneNum,String verifycode,String newPassword, ChangePasswordCallBack callBack) {

		new Thread(new Runnable() {
			@Override
			public void run() {


				JSONObject param = null;
				String param_json=  "";
				try {
					param = new JSONObject();
					param.put("quhao", areaCode);
//					param.put("accountid", MyApplication.getAppContext().getGameArgs().getAccount_id());
					param.put("phone", phoneNum);
					param.put("verifycode", verifycode);
					param.put("newuserpassword", newPassword);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=chongzhipassword", param_json, "utf-8");
				MLog.a(result);

				try {
					JSONObject datajson=new JSONObject(result);
					String code=datajson.getString("code");

					if ("0".equals(code)){
						callBack.Success(datajson.getJSONObject("data").getString("accountid"));

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",datajson.getJSONObject("data").getString("accountid")).commit();
//						callBack.CheckSuccess(datajson.getString("msg"));
					}else{
						callBack.Fail(code);
//						callBack.CheckFail(datajson.getString("msg"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
					callBack.Fail("1005");
				}

			}
		}).start();
	}

    public static void submitEventJson(String eventName,JSONObject jsonEvent) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=usergameinfo", jsonEvent.toString(), "utf-8");
				MLog.a(result);
//				JSONObject jsonObject=new JSONObject(result);
//				String code=jsonObject.getString("code");
//				if(){
//
//				}else {
//					String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=chongzhipassword", jsonEvent.toString(), "utf-8");
//				}

			}
		}).start();


    }

	public static void logdataReport(String eventName, JSONObject json) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				JSONObject js=null;
				try {
					js=new JSONObject();
					js.put("accountid",MyGamesImpl.getSharedPreferences().getString("accountid",""));
					js.put("data",json);
					js.put("event",eventName);

					String result = HttpUtils.postMethod(Configs.dataReportUrl, js.toString(), "utf-8");
					MLog.a(result);
					JSONObject data=new JSONObject(result);
					String code=data.getString("code");

					if("0".equals(code)){
						MLog.a("ataReport--success");
					}else {
						MLog.a("ataReport--faile--"+data.getString("msg"));
					}
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
			}
		}).start();
	}

	//tradPlus广告上报
	public static void tpAdReport(String eventName, JSONObject json) {
		MySdkApi.getMact().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject js=new JSONObject();
				try {
					js.put("data",json);
					js.put("event",eventName);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				MyAsyncTask myAsyncTask = new MyAsyncTask();
				myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,Configs.tradPlusReportUrl,js.toString());
			}
		});

	}

	//payerMax验证支付
	public static void purchasePayerMax(String orderId){
			new Thread(
					new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < 5; i++) {
								String result = HttpUtils.postMethod(Configs.accountserver + "gameparam=sec_confirmation", "{\"exorderno\":\"" + MyApplication.getAppContext().getOrderinfo().getTransactionId() + "\"}", "utf-8");
								MLog.a(result);
								JSONObject resultJson = null;
								try {
									resultJson = new JSONObject(result);
									String code = resultJson.getString("code");
									if ("0".equals(code)) {
										if ("1".equals(resultJson.getString("data"))) {
											OrderInfo orderInfo = MyApplication.getAppContext().getOrderinfo();
											String money = orderInfo.getAmount();
											MyGamesImpl.getInstance().ADJSubmit(4,orderId, money);
											MySdkApi.getMpaycallBack().payFinish();
										} else {
											MySdkApi.getMpaycallBack().payFail(code);
											MLog.a("purchasePayerMax--faile--"+resultJson.getString("msg"));
										}
										break;
									} else {
										MySdkApi.getMpaycallBack().payFail(code);
										MLog.a("purchasePayerMax--faile--"+resultJson.getString("msg"));
									}
								} catch (JSONException e) {
									e.printStackTrace();
									MySdkApi.getMpaycallBack().payFail(e.toString());
								}
							}
						}
					}
			).start();

	}

	public static void setfirebaseid(String firebaseid) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid", ""));
					jsonObject.put("firebaseid", firebaseid);
					String result = HttpUtils.postMethod(Configs.accountserver + "gameparam=setfirebaseid", jsonObject.toString(), "utf-8");

					MLog.a(result);
					JSONObject data = new JSONObject(result);
					String code = data.getString("code");

					if ("0".equals(code)) {
						MLog.a("setfirebaseid--success");
					} else {
						MLog.a("setfirebaseid--faile--" + data.getString("msg"));
						String result1 = HttpUtils.postMethod(Configs.dataReportUrl, jsonObject.toString(), "utf-8");
					}

				} catch (JSONException e) {
					e.printStackTrace();

				}
			}
		}).start();
	}

	public static void othersdkLogin(String openid, String token, String extend) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("username",openid);
					param_js.put("sessionid",token);
					param_js.put("extend",extend);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String param = param_js.toString();
				String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=othersdkloginvalid", param, "utf-8");
				MLog.a(result);

				JSONObject logindatajs = null;
				try {
					logindatajs = new JSONObject(result);
					if ("0".equals(logindatajs.getString("code"))){
						String accountid = logindatajs.getJSONObject("data").getJSONObject("account").getString("accountid");
						String sessionid = logindatajs.getJSONObject("data").getJSONObject("account").getString("sessionid");

						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","othersdk").apply();

						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"othersdk","");
						//保存登录信息
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).apply();
						String pub = FilesTool.getPublisherStringContent().split("sdk_")[0];
						PhoneTool.submitSDKEvent("12",pub+" login success");
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					}else{
						PhoneTool.submitSDKEvent("13",result);
						MySdkApi.getLoginCallBack().loginFail(logindatajs.getString("code"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
					PhoneTool.submitSDKEvent("13",result);
					MySdkApi.getLoginCallBack().loginFail("1005");
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void bindHwLogin(String token) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("token",token);
					param_js.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String param = param_js.toString();
				String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=bundhuawei", param, "utf-8");
				MLog.a(result);

				JSONObject datajs = null;
				try {
					datajs = new JSONObject(result);
					if ("0".equals(datajs.getString("code"))){
						MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_name","").apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_password","").apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_youke_accountid","").apply();
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","othersdk").apply();
					}else{
						String msg=datajs.getString("msg");
						ToastUtils.Toast(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

    public static void ptPay() {
		JSONObject param = null;
		try {
			param = new JSONObject();
			param.put("accountid", MyGamesImpl.getSharedPreferences().getString("accountid",""));
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		String param_json = param.toString();
		PhoneTool.onCreateDialog(MyGamesImpl.getInstance().getSdkact(),mTips,mLoading);
		HttpUtils.startPost(Configs.payserver+"gameparam=ptdbpay",param_json,"utf-8");
    }

	static GetNonceCallBack nonceCallBack;
	public static void getLoginNonce(String walletaddress,String chainId, GetNonceCallBack callBack) {
		nonceCallBack=callBack;
		JSONObject param = null;
		String param_json=  "";
		OrderInfo orderinfo=MyApplication.getAppContext().getOrderinfo();
		try {
			param = new JSONObject();
			param.put("chainid", chainId);
			param.put("address", walletaddress);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		param_json = param.toString();
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		HttpUtils.startPost(Configs.accountserver+"gameparam=getwalletnonce",param_json,"utf-8");
	}
	public static void walletLogin_check(final String walletaddress, final String sign, String m_nonce) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setwalletaccount";
				try {
					param = new JSONObject();
					param.put("address", walletaddress);
					param.put("sign", sign);
					param.put("nonce", m_nonce);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject loginjs = null;
				try {
					loginjs = new JSONObject(logindata);
					String code= loginjs.getString("code");
					if ("0".equals(code)){
						//保存登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_walletid",walletaddress).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_walletsign",sign).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_walletnonce",m_nonce).commit();
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","wallet").commit();

						String accountid = loginjs.getJSONObject("data").getString("accountid");
						String sessionid = loginjs.getJSONObject("data").getString("sessionid");

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();
						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"wallet",walletaddress);

						ToastUtils.Toast("Login succeeded");
						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					} else{
						String msg=loginjs.getString("msg");
						ToastUtils.Toast(msg);
						MySdkApi.getLoginCallBack().loginFail(code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
				}
				MyGamesImpl.getInstance().closeConnect();//登录后关闭钱包连接
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void wemixLogin_check(String address, String accessToken, long timestamp, long expires) {
		PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setwemixaccount";
				try {
					param = new JSONObject();
					param.put("address", address);
					param.put("access_token", accessToken);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject loginjs = null;
				try {
					loginjs = new JSONObject(logindata);
					String code= loginjs.getString("code");
					if ("0".equals(code)){
						//保存登录信息
						MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaddress",address).commit();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaccesstoken",accessToken).commit();
						MyGamesImpl.getSharedPreferences().edit().putLong("myths_wemixtimestamp",timestamp).commit();
						MyGamesImpl.getSharedPreferences().edit().putLong("myths_wemixexpires",expires).commit();
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","wemix").commit();

						String accountid = loginjs.getJSONObject("data").getString("accountid");
						String sessionid = loginjs.getJSONObject("data").getString("sessionid");

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).commit();
						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"wemix",address);

						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					} else{
						String msg=loginjs.getString("msg");
						ToastUtils.Toast(msg);
						MySdkApi.getLoginCallBack().loginFail(code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					MySdkApi.getLoginCallBack().loginFail("1005");
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void handleWemixPayParam(String url,String sign,String contract,String useraddress,String nonce,String column,String fee,String orderid,PayConsumeCallback callback) {
		PhoneTool.onCreateDialog(MyGamesImpl.getInstance().getSdkact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String accesstoken = MyGamesImpl.getSharedPreferences().getString("myths_wemixaccesstoken","");
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("signature", sign);
					param_js.put("contract_address", contract);
					param_js.put("user_address", useraddress);
					param_js.put("nonce", nonce);
					param_js.put("access_token", accesstoken);
					param_js.put("column", column);
					param_js.put("fee", fee);
					param_js.put("orderid", orderid);
					param_js.put("extradata", MyApplication.getAppContext().getOrderinfo().getExtraInfo());
					param_js.put("method",MyApplication.getAppContext().getOrderinfo().getUseCase());
					param_js.put("amount", MyApplication.getAppContext().getOrderinfo().getAmount());
					String param = param_js.toString();
					String result = HttpUtils.postMethod(url, param, "utf-8");
					MLog.a(result);
					JSONObject js_result = new JSONObject(result);
					String code = js_result.getString("code");
					if(code.equals("0")){
						callback.result(true,"success");
					}else{
						callback.result(false,js_result.getString("msg"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.result(false,"Exception:"+e1.getMessage());
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	public static void handleApproveSign(String approveurl, String usersign, String useraddress,String contract, String orderid,String column,String fee,ApproveCallback callback) {
		PhoneTool.onCreateDialog(MyGamesImpl.getInstance().getSdkact(),mTips,mLoading);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String accesstoken = MyGamesImpl.getSharedPreferences().getString("myths_wemixaccesstoken","");
				JSONObject param_js = new JSONObject();
				try {
					param_js.put("signature", usersign);
					param_js.put("contract_address", contract);
					param_js.put("user_address", useraddress);
					param_js.put("amount", MyApplication.getAppContext().getOrderinfo().getAmount());
					param_js.put("orderid", orderid);
					param_js.put("access_token", accesstoken);
					param_js.put("column", column);
					param_js.put("fee", fee);
					param_js.put("extradata", MyApplication.getAppContext().getOrderinfo().getExtraInfo());
					param_js.put("method",MyApplication.getAppContext().getOrderinfo().getUseCase());
					String param = param_js.toString();
					String result = HttpUtils.postMethod(approveurl, param, "utf-8");
					MLog.a(result);
					JSONObject js_result = new JSONObject(result);
					String code = js_result.getString("code");
					if(code.equals("0")){
						String hash = ResourceUtil.getJsonItemByKey(js_result,"hash");
						callback.result(true,hash);
					}else{
						callback.result(false,js_result.getString("msg"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					callback.result(false,"Exception:"+e1.getMessage());
				}
				PhoneTool.disDialog();
			}
		}).start();
	}

	//VK登录验证
	public static void vkLogin_check(final String uid, final String extend) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhoneTool.onCreateDialog(MySdkApi.getMact(),mTips,mLoading);
				JSONObject param = null;
				String param_json=  "";
				String url = Configs.accountserver+"gameparam=setvkaccount";
				try {
					param = new JSONObject();
					param.put("vkaccount", uid);
					param.put("extend", extend);
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				param_json = param.toString();
				String logindata = HttpUtils.postMethod(url, param_json, "utf-8");
				JSONObject loginjs = null;
				try {
					loginjs = new JSONObject(logindata);
					String code= loginjs.getString("code");
					if ("0".equals(code)){
						//保存登录类型用于自动登录
						MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type","vk").apply();
						MyGamesImpl.getSharedPreferences().edit().putString("myths_vk_uid",uid).apply();

						String accountid = loginjs.getJSONObject("data").getString("accountid");
						String sessionid = loginjs.getJSONObject("data").getString("sessionid");

						MyGamesImpl.getSharedPreferences().edit().putString("accountid",accountid).apply();
						MySdkApi.getLoginCallBack().loginSuccess(accountid,sessionid,"vk","");

						MyApplication.getAppContext().getGameArgs().setAccount_id(accountid);
						MyApplication.getAppContext().getGameArgs().setSession_id(sessionid);
						PhoneTool.submitSDKEvent("10","vk login success");
						MyGamesImpl.getInstance().ADJSubmit(2,"");
					} else{
						String msg=loginjs.getString("msg");
						PhoneTool.submitSDKEvent("11",msg);
						MySdkApi.getLoginCallBack().loginFail(code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					PhoneTool.submitSDKEvent("11",logindata);
					MySdkApi.getLoginCallBack().loginFail("1005");
				}
				PhoneTool.disDialog();
			}
		}).start();
	}
}
