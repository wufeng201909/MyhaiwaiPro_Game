package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.jos.AntiAddictionCallback;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AccountAuthResult;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.utils.ResourceLoaderUtil;
import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HwSdk {

	private static Activity myCon;
	private static Timer tim;
	private static JosAppsClient appsClient = null;
	private static boolean ishwInitSuc = false;
	private static boolean isclicklogin = false;
	private static boolean isResumeCheck = true;
	private static boolean ishwInitOver = false;
	private static boolean ishashwid = false;
	private static String mType = "";
	private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
	public static void applicationAttachBaseContext(Application app, Context base) {
		ishashwid = FilesTool.isContainPackName(base,"com.huawei.hwid");
		try {
			AGConnectOptionsBuilder builder = new AGConnectOptionsBuilder();
			builder.setInputStream(base.getAssets().open("agconnect-services.json"));
			AGConnectInstance.initialize(base, builder);
		} catch (Exception e) {
			e.printStackTrace();
			PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:"+e.getMessage());
		}
	}
    public static void applicationOnCreate(Application app) {
		HuaweiMobileServicesUtil.setApplication(app);
	}

	private static AppParams appParams = new AppParams(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME, new AntiAddictionCallback() {
		@Override
		public void onExit() {
			//在此处实现游戏防沉迷功能，如保存游戏、调用帐号退出接口
			MLog.a("-----------onExit--------------");
			if(myCon!=null)myCon.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	});
	private static OnSuccessListener<Void> onSuccess = new OnSuccessListener<Void>() {
		@Override
		public void onSuccess(Void aVoid) {
			MLog.a("init success");
			ishwInitSuc = true;
			if(isclicklogin) loginSDK(myCon,mType);
		}
	};
	private static OnFailureListener onFail = new OnFailureListener() {
		@Override
		public void onFailure(Exception e) {
			if (e instanceof ApiException) {
				ApiException apiException = (ApiException) e;
				int statusCode = apiException.getStatusCode();
				//错误码为7401时表示用户未同意华为联运隐私协议
				if (statusCode == JosStatusCodes.JOS_PRIVACY_PROTOCOL_REJECTED) {
					MLog.a("has reject the protocol");
					//在此处实现退出游戏
					// 执行游戏退出
					myCon.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}else if(statusCode == 907135003 || statusCode == 907135002){
					if(isclicklogin){
						if(isValidHits()){//防止短时间内重复调用init
							MLog.a("-----hwclientInit00-------");
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									MLog.a("-----hwclientInit11-------");
									hwclientInit();
								}
							},1000);
						}
					}else{
						ishwInitOver = true;
					}
				}else{
					isclicklogin = false;
					PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"statusCode-->"+statusCode);
				}
				//在此处实现其他错误码的处理
				System.out.println("statusCode-->"+statusCode);
			}
		}
	};
	private static long lastClickTime = 0;
	private static boolean isValidHits() {
		if (System.currentTimeMillis() - lastClickTime > 1000) {
			lastClickTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	private static void hwclientInit(){
		MLog.a("-----hwclientInit-------");
		Task<Void> initTask = appsClient.init(appParams);
		initTask.addOnSuccessListener(onSuccess).addOnFailureListener(onFail);
	}
	public static void initSDK(Activity activity) {
		myCon = activity;
		isclicklogin = false;
		initSDK_(activity);
	}
	private static void initSDK_(Activity activity) {
		appsClient = JosApps.getJosAppsClient(activity);
		ResourceLoaderUtil.setmContext(activity);
		if(!ishashwid)return;
		hwclientInit();
	}

	public static void loginSDK(final Activity activity,String type) {
		mType = type;
		if(!ishashwid){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtils.Toast(ResourceUtil.getString(activity,"myths_wait_loadhw"));
					PhoneTool.submitSDKEvent("13","has not hms");
					MySdkApi.getLoginCallBack().loginFail("has not hms");
				}
			});
			return;
		}
		if(!isclicklogin){
			isclicklogin = true;
		}
		if(ishwInitOver){
			ishwInitOver = false;
			hwclientInit();
			return;
		}
		//检查初始化是否成功
		if(!ishwInitSuc){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtils.Toast(ResourceUtil.getString(activity,"myths_wait"));
				}
			});
			return;
		}
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				timerSch(activity);
				AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).setAuthorizationCode().createParams();
				Task<AuthAccount> authAccountTask = AccountAuthManager.getService(activity, authParams).silentSignIn();
				authAccountTask.addOnSuccessListener(
								new OnSuccessListener<AuthAccount>() {
									@Override
									public void onSuccess(AuthAccount authAccount) {
										timerCancle();
										getGamePlayer();
									}
								})
						.addOnFailureListener(
								new OnFailureListener() {
									@Override
									public void onFailure(Exception e) {
										if (e instanceof ApiException) {
											// 在此处实现华为帐号显式授权
											Intent intent = AccountAuthManager.getService(activity,authParams).getSignInIntent();
											activity.startActivityForResult(intent, 8888);  //SIGN_IN_INTENT为自定义整型常量
										}
									}
								});
			}
		});
	}
	public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
		if(!ishashwid){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:has not hms");
					ToastUtils.Toast(ResourceUtil.getString(activity,"myths_wait_loadhw"));
					MySdkApi.getMpaycallBack().payFail("has not hms");
				}
			});
			return;
		}
		isResumeCheck = false;
		sharedPreferences.edit().putString("huawei_conf_url", paynotifyurl).apply();
		checkOwnedPurchases();
		PurchaseIntentReq req = new PurchaseIntentReq();
		// 通过createPurchaseIntent接口购买的商品必须是您在AppGallery Connect网站配置的商品
		req.setProductId(extra1);
		// priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
		req.setPriceType(0);
		req.setDeveloperPayload(orderId);
		// 调用createPurchaseIntent接口创建托管商品订单
		Task<PurchaseIntentResult> task = Iap.getIapClient(activity).createPurchaseIntent(req);
		task.addOnSuccessListener(new OnSuccessListener<PurchaseIntentResult>() {
			@Override
			public void onSuccess(PurchaseIntentResult result) {
				// 获取创建订单的结果
				Status status = result.getStatus();
				if (status.hasResolution()) {
					try {
						// 6666是您自定义的常量
						// 启动IAP返回的收银台页面
						status.startResolutionForResult(activity, 6666);
					} catch (IntentSender.SendIntentException exp) {
						exp.printStackTrace();
						PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:"+ exp.getMessage());
						MySdkApi.getMpaycallBack().payFail(exp.getMessage());
					}
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				if (e instanceof IapApiException) {
					IapApiException apiException = (IapApiException) e;
					Status status = apiException.getStatus();
					int returnCode = apiException.getStatusCode();
					MLog.a("createPurchaseIntent---onFailure--returnCode-->"+returnCode);
					switch (returnCode) {
						case OrderStatusCode.ORDER_NOT_ACCEPT_AGREEMENT://未同意支付协议
							// Account is not logged in or the user does not agree to payment agreement.
							if (status.hasResolution()) {
								try {
									status.startResolutionForResult(activity, 9999);
								} catch (IntentSender.SendIntentException exp) {
								}
							}
					}
				} else {
					// 其他外部错误
				}
				System.out.println("hwpay-onFailure-->"+e.getMessage());
				MySdkApi.getMpaycallBack().payFail(e.getMessage());
			}
		});

	}

	public static void onActivityResult(final Activity act,int requestCode, int resultCode, Intent data) {
		MLog.a("onActivityResult--requestCode:"+requestCode+";resultCode:"+resultCode);
		if (requestCode == 8888) {
			handleSignInResult(data);
		} else if(requestCode == 9999){
			MLog.a("ASDK","requestCode9999--resultCode---"+resultCode);
		} else if (requestCode == 6666) {
			if (data != null) {
				PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(act).parsePurchaseResultInfoFromIntent(data);
				String msg = "";
				switch(purchaseResultInfo.getReturnCode()) {
					case OrderStatusCode.ORDER_STATE_CANCEL:
						msg = "".equals(msg)?"-ORDER_STATE_CANCEL-":msg;
					case OrderStatusCode.ORDER_STATE_FAILED:
						msg = "".equals(msg)?"-ORDER_STATE_FAILED-":msg;
					case OrderStatusCode.ORDER_PRODUCT_OWNED:
						// to check if there exists undelivered products.
						msg = "".equals(msg)?"-ORDER_PRODUCT_OWNED-":msg;
						MLog.a(msg);
						MySdkApi.getMpaycallBack().payFail(msg);
						break;
					case OrderStatusCode.ORDER_STATE_SUCCESS:
						// pay success.
						String inAppPurchaseData = purchaseResultInfo.getInAppPurchaseData();
						String inAppSignature = purchaseResultInfo.getInAppDataSignature();
						handlePurchase(inAppPurchaseData,inAppSignature);
						break;
					default:
						msg = purchaseResultInfo.getReturnCode()+"";
						MLog.a(msg);
						MySdkApi.getMpaycallBack().payFail(msg);
						break;
				}
			}else{
				System.out.println("data is null");
				PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:data is null");
				MySdkApi.getMpaycallBack().payFail("data is null");
			}
		}
	}

	private static void handleSignInResult(Intent data) {
		if (null == data) {
			// 登录失败 | sign in fail
			PhoneTool.submitSDKEvent("13","data-null");
			MySdkApi.getLoginCallBack().loginFail("sign in fail");
			return;
		}
		String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
		if (TextUtils.isEmpty(jsonSignInResult)) {
			MLog.a("SignIn result is empty");
			PhoneTool.submitSDKEvent("13","SignIn result is empty");
			MySdkApi.getLoginCallBack().loginFail("SignIn result is empty");
			return;
		}
		try {
			AccountAuthResult signInResult = new AccountAuthResult().fromJson(jsonSignInResult);
			if (0 == signInResult.getStatus().getStatusCode()) {
				getGamePlayer();
			} else {
				String msg = "Sign in failed: " + signInResult.getStatus().getStatusCode()+",msg:"+signInResult.getStatus().getStatusMessage();
				MLog.a(msg);
				PhoneTool.submitSDKEvent("13",msg);
				MySdkApi.getLoginCallBack().loginFail(msg);
			}
		} catch (JSONException var7) {
			System.out.println("Failed to convert json from signInResult.");
			PhoneTool.submitSDKEvent("13","Failed to convert json from signInResult.");
		}
	}

	private static void getGamePlayer() {
		// 调用getPlayersClient方法初始化
		PlayersClient client = Games.getPlayersClient(myCon);
		// 执行游戏登录
		Task<Player> task = client.getGamePlayer(true);
		task.addOnSuccessListener(new OnSuccessListener<Player>() {
			@Override
			public void onSuccess(Player player) {
				String accessToken = player.getAccessToken();
				String displayName = player.getDisplayName(); // 免授权登录场景下无法获取玩家昵称
				String unionId = player.getUnionId();
				String openId = player.getOpenId();
				if(mType.equals("bind")){//绑定华为账号
					HttpUtils.bindHwLogin(accessToken);
				}else{
					// 获取玩家信息成功，校验服务器端的玩家信息，校验通过后允许进入游戏
					HttpUtils.othersdkLogin(openId, accessToken,player.getPlayerId());
					//查询漏单
					checkOwnedPurchases();
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				if (e instanceof ApiException) {
					String result = "rtnCode:" + ((ApiException) e).getStatusCode();
					// 获取玩家信息失败，不允许进入游戏，并根据错误码处理
					if (7400 == ((ApiException) e).getStatusCode()||7018 == ((ApiException) e).getStatusCode()) {
						// 7400表示用户未签署联运协议，需要继续调用init接口
						// 7018表示初始化失败，需要继续调用init接口
						hwclientInit();
					}else{
						PhoneTool.submitSDKEvent("13",result);
					}
				}
			}
		});
	}

	//查询漏单
	private static void checkOwnedPurchases() {
		if(!ishashwid)return;
		OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
		ownedPurchasesReq.setPriceType(0);
		Task<OwnedPurchasesResult> task = Iap.getIapClient(myCon).obtainOwnedPurchases(ownedPurchasesReq);
		task.addOnSuccessListener(new OnSuccessListener<OwnedPurchasesResult>() {
			@Override
			public void onSuccess(OwnedPurchasesResult result) {
				// Obtain the execution result.
				if (result != null && result.getInAppPurchaseDataList() != null) {
					for (int i = 0; i < result.getInAppPurchaseDataList().size(); i++) {
						String inAppPurchaseData = result.getInAppPurchaseDataList().get(i);
						String inAppSignature = result.getInAppSignature().get(i);
						// use the payment public key to verify the signature of the inAppPurchaseData.
						// if success.
						handlePurchase(inAppPurchaseData,inAppSignature);
					}
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:"+ e.getMessage());
				if (e instanceof IapApiException) {
					IapApiException apiException = (IapApiException)e;
					Status status = apiException.getStatus();
					int returnCode = apiException.getStatusCode();
				} else {
					// Other external errors
				}
			}
		});
	}

	private static void handlePurchase(String inAppPurchaseData,String inAppSignature){
		try {
			InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
			int purchaseState = inAppPurchaseDataBean.getPurchaseState();
			//purchaseState为0时表示此次交易是成功的，您的应用仅需要对这部分商品进行补发货操作
			if(purchaseState == InAppPurchaseData.PurchaseState.PURCHASED){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.CHINA);
				String formattedTime = sdf.format(new Date());
				MLog.a("handlePurchase--formattedTime-->"+formattedTime);
				String order = inAppPurchaseDataBean.getDeveloperPayload();
				String purchaseToken = inAppPurchaseDataBean.getPurchaseToken();
				String url = sharedPreferences.getString("huawei_conf_url", "");
				url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
				HttpUtils.hwPurchaseComsume(url,order,inAppPurchaseData+"@"+inAppSignature,purchaseToken,formattedTime);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:"+ e.getMessage());
		}
	}
	//消耗确认
	public static void consumeOwnedPurchase(String purchaseToken){
		ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
		req.setPurchaseToken(purchaseToken);
		Task<ConsumeOwnedPurchaseResult> task = Iap.getIapClient(myCon).consumeOwnedPurchase(req);
		task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
			@Override
			public void onSuccess(ConsumeOwnedPurchaseResult result) {
				// Obtain the result
				System.out.println("consumeOwnedPurchase--onSuccess");
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"hw:"+ e.getMessage());
				if (e instanceof IapApiException) {
					IapApiException apiException = (IapApiException)e;
					Status status = apiException.getStatus();
					int returnCode = apiException.getStatusCode();
				} else {
					// Other external errors
				}
				System.out.println("consumeOwnedPurchase--onFailure");
			}
		});
	}

	public static void onResume(Activity act) {
		//通过isResumeCheck防止支付成功后重复请求查询漏单
		if(isResumeCheck){
			checkOwnedPurchases();
		}else{
			isResumeCheck = true;
		}
	}

	private static void timerSch(Activity activity){
		if(tim == null) tim = new Timer();
		try {
			tim.schedule(new TimerTask() {
				@Override
				public void run() {
					PhoneTool.disDialog();
				}
			},3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PhoneTool.onCreateDialog(activity,"","");
	}

	private static void timerCancle(){
		PhoneTool.disDialog();
		try {
			tim.cancel();
			tim = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
