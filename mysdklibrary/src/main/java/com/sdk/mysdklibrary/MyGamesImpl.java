package com.sdk.mysdklibrary;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.OAuthProvider;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.hihonor.cloudservice.support.account.HonorIdSignInManager;
//import com.hihonor.cloudservice.support.account.request.SignInOptionBuilder;
//import com.hihonor.cloudservice.support.account.request.SignInOptions;
//import com.hihonor.cloudservice.support.account.result.SignInAccountInfo;
//import com.hihonor.honorid.core.helper.handler.ErrorStatus;
//import com.hihonor.iap.framework.aidl.BuildConfig;
//import com.hihonor.iap.framework.utils.JsonUtil;
//import com.hihonor.iap.framework.utils.logger.LogUtils;
//import com.hihonor.iap.sdk.Iap;
//import com.hihonor.iap.sdk.IapClient;
//import com.hihonor.iap.sdk.ProductType;
//import com.hihonor.iap.sdk.bean.ConsumeReq;
//import com.hihonor.iap.sdk.bean.ConsumeResult;
//import com.hihonor.iap.sdk.bean.IsEnvReadyResult;
//import com.hihonor.iap.sdk.bean.OwnedPurchasesReq;
//import com.hihonor.iap.sdk.bean.OwnedPurchasesResult;
//import com.hihonor.iap.sdk.bean.ProductInfoReq;
//import com.hihonor.iap.sdk.bean.ProductInfoResult;
//import com.hihonor.iap.sdk.bean.ProductOrderIntentReq;
//import com.hihonor.iap.sdk.bean.ProductOrderIntentResult;
//import com.hihonor.iap.sdk.bean.PurchaseProductInfo;
//import com.hihonor.iap.sdk.bean.PurchaseResultInfo;
//import com.hihonor.iap.sdk.utils.IapUtil;
import com.google.android.gms.tasks.Task;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.AdjustUtil;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;
import com.sdk.mysdklibrary.activity.EmailLoginActivity;
import com.sdk.mysdklibrary.activity.LoginActivity;
import com.sdk.mysdklibrary.impl.WalletCallback;
import com.sdk.mysdklibrary.interfaces.BindFBCallBack;
import com.sdk.mysdklibrary.interfaces.BindPhoneCallBack;
import com.sdk.mysdklibrary.interfaces.ChangePasswordCallBack;
import com.sdk.mysdklibrary.interfaces.CheckCodeCallBack;
import com.sdk.mysdklibrary.interfaces.EmailCodeCallBack;
import com.sdk.mysdklibrary.interfaces.GetNonceCallBack;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PhoneCodeCallBack;
import com.sdk.mysdklibrary.interfaces.ShareCallBack;
import com.sdk.mysdklibrary.interfaces.UnBindSDKCallBack;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.payUtils.GoogleUtil;
import com.sdk.mysdklibrary.walletconnect.WalletUtil;
import com.sdk.mysdklibrary.walletconnect.WemixUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyGamesImpl {

    private static MyGamesImpl instance;
    private static ShareCallBack m_shareCallBack;
    private Activity activity;
    private Activity sdkact;
    private String userfbinfo = "";

    private Timer tim;

    //Firebase
//    private FirebaseAnalytics mFirebaseAnalytics;
    //Facebook
//    private AppEventsLogger facebookLogger;

    public void setUserfbinfo(String userfbinfo) {
        this.userfbinfo = userfbinfo;
    }

    public String getUserfbinfo() {
        return userfbinfo;
    }

    public void setSdkact(Activity sdkact) {
        this.sdkact = sdkact;
    }

    public Activity getSdkact() {
        return sdkact;
    }

    private static CallbackManager callbackManager = null;

    private static GameRequestDialog requestDialog = null;

    private GoogleSignInClient mGoogleSignInClient;

    private boolean isInit = false;
    private boolean isLogin = false;
    private boolean isPayShow = false;
    private boolean isLiveOn = false;

    private static SharedPreferences sharedPreferences = null;

    public static SharedPreferences getSharedPreferences() {
        Context con = MyApplication.context == null?MyApplication.getAppContext():MyApplication.context;
        return sharedPreferences == null ? con.getSharedPreferences("user_info", 0):sharedPreferences;
    }

    private String topic = UUID.randomUUID().toString();
    private String bridgeUrl = "https://bridge.walletconnect.org";
//    private String bridgeUrl = "https://safe-walletconnect.gnosis.io";

    private String m_nonce;

    public static MyGamesImpl getInstance() {
        if (instance == null) {
            instance = new MyGamesImpl();
        }
        return instance;
    }

    /**
     * 初始化sdk
     *
     * @param context
     * @param callBack
     */
    public void initSDK(final Activity context, final InitCallBack callBack) {
        sharedPreferences = context.getSharedPreferences("user_info", 0);
        activity = context;
        //获取缓存的uid并赋值给GameArgs
        String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
        MyApplication.getAppContext().getGameArgs().setAccount_id(uid);
        //处理设备号
        PhoneTool.managerIMEI(context);
        HttpUtils.checkupnet(context, callBack);

        //Firebase
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        // facebook
//        facebookLogger = AppEventsLogger.newLogger(context);
        //AppsFlyerLib
//        AppsFlyerLib.getInstance().start(context);

        //激活上报
        ADJSubmit(0);
        //wemix
//        WemixUtil.getInstance().init(context);
    }

    /**
     * @param context
     * @param callBack
     * @param type//"bind"表示游客转facebook账号
     */
    public void openfacebookLogin(Activity context, final LoginCallBack callBack, final String type) {
        if (!"bind".equals(type)) {
            //检查facebook的token有效性
            String token = MyGamesImpl.getSharedPreferences().getString("myths_input_token", "");
            if (!token.equals("")) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn) {
                    HttpUtils.gotoAutoLoginActivity("facebook");
                    return;
                }
            }
        }

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }

//        LoginManager.getInstance().logOut();
        //判断是否安装了facebook
        if(!FilesTool.isContainPackName(context,"com.facebook.katana")){
            callBack.loginFail("10001");//未安装facebook
            ToastUtils.Toast("Not installed Facebook, Please install it and try again");
            return;
        }
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String uid = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                MLog.a("openfacebookLogin-onSuccess-" + "000");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Insert your code here
                                if (object != null) {
                                    MyGamesImpl.getInstance().setUserfbinfo(object.toString());
                                    MLog.a("GraphRequest-" + object.toString());
                                    String name = "";
                                    try {
                                        name = object.getString("name");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    MyGamesImpl.getSharedPreferences().edit().putString("myths_fbname", name).commit();
                                } else {
                                    MLog.a("object-null");
                                }
                                HttpUtils.fblogin_check(uid, token, type);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                //FB登录失败上报
                PhoneTool.submitSDKEvent("19", "cancel");
                callBack.loginFail("1000");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                logout();
                MLog.a("onError-gotologout-");
                //FB登录失败上报
                PhoneTool.submitSDKEvent("19", "onError");
                callBack.loginFail("2003");
            }
        });
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("public_profile", "email"));
    }

    public void bindfacebook(Activity context, final BindFBCallBack callBack) {
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        logout();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String uid = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                MLog.a("bindfacebook-onSuccess-" + "111");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Insert your code here
                                if (object != null) {
                                    MyGamesImpl.getInstance().setUserfbinfo(object.toString());
                                    MLog.a("GraphRequest-" + object.toString());
                                    String name = "";
                                    try {
                                        name = object.getString("name");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    MyGamesImpl.getSharedPreferences().edit().putString("myths_fbname", name).commit();
                                } else {
                                    MLog.a("object-null");
                                }
                                HttpUtils.fblogin_check(uid, token, "gamebindfb");
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                callBack.bindFail("1000");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                callBack.bindFail("onError");
            }
        });
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("public_profile", "email"));
    }

    public void bindGoolge(Activity context, final BindFBCallBack callBack) {
        MLog.a("bindGoolge----");
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        googlelogout();

        String google_login_id = ResourceUtil.getString(context, "pg_google_login_id");

        GetSignInIntentRequest request = GetSignInIntentRequest.builder()
                .setServerClientId(google_login_id)
                .build();
        Identity.getSignInClient(context)
                .getSignInIntent(request)
                .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        try {
                            context.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_GOOGLE_SIGN_IN,
                                    null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MLog.a("Google Sign-in failed-->" + e.getMessage());
//                        callBack.loginFail("Google Sign-in failed-->" + e.getMessage());
                        callBack.bindFail("Google Sign-in failed-->"+ e.getMessage());
                    }
                });

    }

    public void bindApple(Activity context, final BindFBCallBack callBack) {
//        MLog.a("bindApple----");
//        if (callbackManager == null) {
//            callbackManager = CallbackManager.Factory.create();
//        }
//        applelogout();
//
//        OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        Task<AuthResult> pending = mAuth.getPendingAuthResult();
//        if(pending != null){
//
//            pending.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//                    MLog.a("Apple login--Success>");
//
//                    FirebaseUser user = authResult.getUser();
//                    if(user != null){
//                        String appleid =user.getUid();
//                        String appleemail =user.getEmail();
//                        MLog.a("Apple login--Success>emai---"+user.getEmail());
//                        HttpUtils.applelogin_check(appleid, appleemail, "bind");
//                        MLog.a("Apple login--Success>---bind");
//                    }else{
//                        MLog.a("Apple login--Fail-->"+"Apple login back user info is null");
//                        callBack.bindFail("Apple login back user info is null");
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    MLog.a("Apple login--Fail>"+e.getMessage());
//                    callBack.bindFail(e.getMessage());
//                }
//            });
//        }else{
//            mAuth.startActivityForSignInWithProvider(context,provider.build()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//
//                    MLog.a("Apple login--Success>");
//
//                    FirebaseUser user = authResult.getUser();
//
//                    MLog.a("Apple login--Success>---user="+user);
//                    if(user != null){
//                        String appleid =user.getUid();
//                        String appleemail =user.getEmail();
//                        MLog.a("Apple login--Success>emai---"+user.getEmail());
//                        MLog.a("Apple login--Success>---appleid="+appleid);
//                        HttpUtils.applelogin_check(appleid, appleemail, "bind");
//                        MLog.a("Apple login--Success>---bind");
//                    }else{
//                        MLog.a("Apple login--Fail-->"+"Apple login back user info is null");
//                        callBack.bindFail("Apple login back user info is null");
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    MLog.a("Apple login--Fail>"+e.getMessage());
//                    callBack.bindFail(e.getMessage());
//                }
//            });
//        }
    }

    public void logout() {
        try{
            LoginManager.getInstance().logOut();
        }catch (Error ignored){
        }

        MyGamesImpl.getSharedPreferences().edit().putString("myths_fbid", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_input_token", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
    }

    public void googlelogout() {
        if(mGoogleSignInClient!=null)
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            MLog.a("mGoogleSignInClient.signOut()");
                        }
                    });
        //切换账号，清除google登录信息
        MyGamesImpl.getSharedPreferences().edit().putString("myths_googleid", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_googleidtoken", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
    }
    public void walletlogout() {
        MyGamesImpl.getSharedPreferences().edit().putString("myths_walletid", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_walletsign", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
        closeConnect();
    }
    public void wemixlogout() {
        MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaddress", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaccesstoken", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
    }

    public void applelogout() {
        //切换账号，清除google登录信息
        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleid", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_appleemail", "").apply();
        MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        //google登录回调
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            try {
                SignInCredential credential = Identity.getSignInClient(activity).getSignInCredentialFromIntent(data);
                String googleid = credential.getId();
                String googleidtoken = credential.getGoogleIdToken();
                System.out.println("googleid:" + googleid);
                System.out.println("googleidtoken:" + googleidtoken);
                System.out.println("logingType:" + logingType);
                MyGamesImpl.getSharedPreferences().edit().putLong("myths_googleidtoken_expiredTime", System.currentTimeMillis()/1000+1800).apply();
                HttpUtils.googlelogin_check(googleid, googleidtoken,logingType);
            } catch (ApiException e) {
                e.printStackTrace();
                PhoneTool.submitSDKEvent("9",e.getMessage());
                MySdkApi.getLoginCallBack().loginFail("1004");
            }
        }else if (requestCode==REQUEST_CODE_GOOGLE_SIGN_IN_OLD){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String googleid = account.getId();
                String googleEmail = account.getEmail();
                String googleidtoken = account.getIdToken();
                System.out.println("o_googleid:" + googleid);
                System.out.println("o_googleidtoken:" + googleidtoken);
                System.out.println("logingType:" + logingType);
                MyGamesImpl.getSharedPreferences().edit().putLong("myths_googleidtoken_expiredTime", System.currentTimeMillis()/1000+1800).apply();
                HttpUtils.googlelogin_check(TextUtils.isEmpty(googleEmail)?googleid:googleEmail, googleidtoken,logingType);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                MLog.a("REQUEST_CODE_GOOGLE_SIGN_IN_OLD--->"+e.getMessage());
//                MySdkApi.getLoginCallBack().loginFail("1004");
                //拉起新版登录
                googleLoginNewApi(activity, MySdkApi.getLoginCallBack(),ResourceUtil.getString(activity, "pg_google_login_id"));
            }
        }
//        //荣耀回调
//        if (REQUEST_CODE_HONOR_SIGN_IN == requestCode) {
//
//            if (resultCode == ErrorStatus.ERROR_MCP_CHECK_FAIL) {
//                MLog.a("resultCode : " + resultCode + " ,msg : " + "mcp check fail");
//                MySdkApi.getLoginCallBack().loginFail("1004");
//                return;
//            }
//            //跳转授权页面回调
//            com.hihonor.cloudservice.tasks.Task<SignInAccountInfo> accountTask = HonorIdSignInManager.parseAuthResultFromIntent(resultCode, data);
//            if (accountTask.isSuccessful()) {
//                SignInAccountInfo signInAccountInfo = accountTask.getResult();
// ;
//                String openid=signInAccountInfo.getOpenId();
//                String idToken=signInAccountInfo.getIdToken();
//
//
//
//               // String account=signInAccountInfo.getAccount().toString();
//
//                HttpUtils.honorlogin_check(openid, idToken,"");
//                MLog.a("errCode : " + signInAccountInfo.getOpenId() +"----"+ signInAccountInfo.getIdToken());
//            } else {
//                Exception exception = accountTask.getException();
//                if (exception instanceof ApiException) {
//                    ApiException apiException = (ApiException) exception;
//                    MySdkApi.getLoginCallBack().loginFail("1004");
//                    MLog.a("errCode : " + apiException.getStatusCode() + " , errMsg = " + apiException.getMessage());
//                }
//            }
//        }
//        System.out.println("honorPurchaseSDK--resultCode==" + resultCode );
//        //荣耀支付
//        if (requestCode == REQUEST_CODE_PAY) {
//            // 客户端并不能100%确保支付结果回调
//            // 支付结果通知回调
//            // getOwnedPurchased、getOwnedPurchaseRecord
//            System.out.println("honorPurchaseSDK--resultCode==" + resultCode );
//            if (resultCode == Activity.RESULT_OK) {
//                PurchaseResultInfo purchaseResultInfo = IapUtil.parsePurchaseResultInfoFromIntent(data);
//                if (purchaseResultInfo == null) {
//                    // 取消支付
//                    System.out.println("honorPurchaseSDK--honor data null=="   );
//                    MySdkApi.getMpaycallBack().payFail( "honor data null");
//                } else {
//                    String purchaseProductInfoStr = purchaseResultInfo.getPurchaseProductInfo();
//                    try {
//                        PurchaseProductInfo purchaseProductInfo = JsonUtil.parse(purchaseProductInfoStr, PurchaseProductInfo.class);
//                        switch (purchaseProductInfo.getPurchaseState()) {
//                            case PurchaseProductInfo.PurchaseState.PAID:
//                                System.out.println("honorPurchaseSDK  case--honorPurchaseSDK token===+"+ purchaseProductInfo.getPurchaseToken()  );
//                                ConsumeReq comsumeReq = new ConsumeReq();
//                                //根据PurchaseToken 进行消耗
//                                comsumeReq.setPurchaseToken(purchaseProductInfo.getPurchaseToken());
//                                com.hihonor.iap.sdk.tasks.Task<ConsumeResult> comsumeRespTask = iapClient.consumeProduct(comsumeReq);
//                                comsumeRespTask.addOnSuccessListener(comsumeResp ->
//                                        //消耗成功
//                                        MySdkApi.getMpaycallBack().payFinish() )
//                                        .addOnFailureListener(e ->
//                                                //消耗失败
//                                                MySdkApi.getMpaycallBack().payFail( e.getErrorCode() + ": " + e.getMessage()));
//                                /* HttpUtils.honorPurchaseSDK(honor_id,purchaseProductInfo.getPurchaseToken(), new PayConsumeCallback() {
//                                     @Override
//                                     public void result(Boolean issuc, String msg) {
//                                         if (issuc) {
//                                             System.out.println("honorPurchaseSDK  case--ssuc"   );
//                                             MySdkApi.getMpaycallBack().payFinish();
//
//                                         } else {
//                                             System.out.println("honorPurchaseSDK--  case fail" + msg);
//                                             MySdkApi.getMpaycallBack().payFail( msg);
//                                         }
//                                     }
//                                 });*/
//
//
//                                // 支付成功
//                                //支付成功后默认消耗，用户也可以根据实际情况消耗
//                                //这里由于网络原因可能调用失败，可以添加重试机制，调用 iapClient.obtainOwnedPurchases ，查询已付款未消耗的商品进行消耗
//
//
//                                break;
//                            case PurchaseProductInfo.PurchaseState.UNPAID:
//                            case PurchaseProductInfo.PurchaseState.PAID_FAILED:
//                            default:
//                                // 支付失败
//                                System.out.println("honorPurchaseSDK   --result payfail"   );
//                                MySdkApi.getMpaycallBack().payFail(purchaseProductInfo.getPurchaseState()+"");
//                        }
//                    } catch (Throwable t) {
//                        // 支付失败
//                    }
//                }
//            } else {
//                // 取消支付
//            }
//        }

    }

    public void autoLogin(Activity context) {
        SharedPreferences sharedPreferences = MyGamesImpl.getSharedPreferences();
        String type = sharedPreferences.getString("myths_auto_type", "");

        if ("guest".equals(type)) {
            String name = sharedPreferences.getString("myths_youke_name", "");
            if (!name.equals("")) {
                HttpUtils.gotoAutoLoginActivity("guest");
                return;
            }
        } else if ("facebook".equals(type)) {
            String token = sharedPreferences.getString("myths_input_token", "");
            if (!token.equals("")) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn) {
                    HttpUtils.gotoAutoLoginActivity("facebook");
                    return;
                }
            }
        } else if ("oldacc".equals(type)) {
            String name = sharedPreferences.getString("myths_oldacc_name", "");
            if (!name.equals("")) {
                HttpUtils.gotoAutoLoginActivity("oldacc");
                return;
            }
        } else if ("google".equals(type)) {
            String name = sharedPreferences.getString("myths_googleid", "");
            if (!name.equals("")) {
                HttpUtils.gotoAutoLoginActivity("google");
                return;
            }
        } else if ("wallet".equals(type)) {
            String name = sharedPreferences.getString("myths_walletid", "");
            if (!name.equals("")) {
                HttpUtils.gotoAutoLoginActivity("wallet");
                return;
            }
        } else if (!TextUtils.isEmpty(type)) {
            HttpUtils.gotoAutoLoginActivity(type);
            return;
        }
        openLogin(context);
    }

    public void openLogin(Activity context) {
        Intent itn = new Intent(context, LoginActivity.class);
        itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(itn);
    }

    public void submitRoleData(int operator, GameRoleBean gameRoleBean) {
        HttpUtils.submitRoleData(operator, gameRoleBean);
    }

    public void ADJSubmit(int type,String... params) {
//===========================================================================================
        //初始化成功后bind Google进行漏单查询
        if (type == 1) {
            bind();
        }
//=============================================================================================
        AdjustUtil.getInstance().ADJSubmit(type,params);
    }

    private void initfacebookshare(Activity context) {
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        requestDialog = new GameRequestDialog(context);
        requestDialog.registerCallback(callbackManager,
                new FacebookCallback<GameRequestDialog.Result>() {
                    @Override
                    public void onSuccess(GameRequestDialog.Result result) {
                        String requestid = result.getRequestId();
                        List<String> data = result.getRequestRecipients();
                        MLog.a("share-requestid---->" + requestid);
                        MLog.a("share-data---->" + data.toString());
                        String[] fbs = new String[data.size()];
                        for (int i = 0; i < data.size(); i++) {
                            MLog.a("Invite fb id:" + data.get(i)); // 邀请了那几个fb好友（fb id）
                            fbs[i] = data.get(i);
                        }
                        m_shareCallBack.isSuccess(fbs);
                    }

                    @Override
                    public void onCancel() {
                        m_shareCallBack.isFailed();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        m_shareCallBack.isFailed();
                    }
                }
        );
    }

    //邀请
    public void openfacebookshare(Activity context, String title, String content, ShareCallBack shareCallBack) {
        m_shareCallBack = shareCallBack;
        initfacebookshare(context);
        GameRequestContent requestcontent = new GameRequestContent.Builder()
                .setMessage(content)
                .setTitle(title)
                .setFilters(GameRequestContent.Filters.APP_NON_USERS)
                .build();
        requestDialog.show(requestcontent);

//        if (AppInviteDialog.canShow()) {
//            AppInviteContent content = new AppInviteContent.Builder()
//                    .setApplinkUrl(appLinkUrl)
//                    .setPreviewImageUrl(previewImageUrl)
//                    .build();
//            AppInviteDialog.show(this, content);
//        }

    }

    //绑定google play服务
    private void bind() {
        //绑定google play服务
        GoogleUtil.getInstance().initService(activity);
    }

    public void onResume() {
        GoogleUtil.getInstance().queryPurchases();
    }

    public void onDestory() {
        GoogleUtil.getInstance().onDestory();

        //删除token
//        if(FirebaseMessaging.getInstance()!=null){
//            FireBaseUtil.delFBToken(activity);
//
//        }
    }

    //分享链接
    public void facebookSURL(Activity act, String url) {
        //链接
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .build();
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        ShareDialog shareDialog = new ShareDialog(act);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                MLog.a("share-onSuccess");
            }

            @Override
            public void onCancel() {
                MLog.a("share-onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                MLog.a("share-onError");
            }
        });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            System.out.println("can show");
            shareDialog.show(content);
        } else {
            System.out.println("cannot show");
        }
    }

    //FB分享
    public void facebookS(Activity act, Bitmap bitmap) {
        //照片
//        Bitmap image = bitmap;
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content2 = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
//        //视频
//        Uri videoFileUri = null;
//        ShareVideo video= new ShareVideo.Builder()
//                .setLocalUrl(videoFileUri)
//                .build();
//        ShareVideoContent content3 = new ShareVideoContent.Builder()
//                .setVideo(video)
//                .build();

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        ShareDialog shareDialog = new ShareDialog(act);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                MLog.a("share-onSuccess");
            }

            @Override
            public void onCancel() {
                MLog.a("share-onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                MLog.a("share-onError");
            }
        });
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            System.out.println("can show");
            shareDialog.show(content2);
        } else {
            System.out.println("cannot show");
        }
    }

    /**
     * FB分享图片+文字
     * @param act
     * @param bitmap    图片
     * @param url       内容
     */
    public void facebookMedia(Activity act,Bitmap bitmap,String url) {

        SharePhoto sharePhoto = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        ShareMediaContent mediaContent = new ShareMediaContent.Builder()
                .addMedium(sharePhoto)
                .setShareHashtag(new ShareHashtag.Builder().setHashtag(url).build())
                .build();

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        ShareDialog shareDialog = new ShareDialog(act);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                MLog.a("share-onSuccess");
            }

            @Override
            public void onCancel() {
                MLog.a("share-onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                MLog.a("share-onError");
            }
        });
        if (ShareDialog.canShow(ShareMediaContent.class)) {
            System.out.println("can show");
            shareDialog.show(mediaContent);
        } else {
            System.out.println("cannot show");
            try{
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setData(Uri.parse(getFacebookPageURL(act)));
                act.startActivity(intent1);
            }catch (Exception e){
                e.printStackTrace();
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("wait for 1s and shareDialog.show");
                    shareDialog.show(mediaContent);
                }
            },1000);

        }
    }
    //这个方法是为了生成标准的可用于跳转的Facebook url
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //新版本的Facebook
                return "fb://facewebmodal/f?href=" + "https://www.facebook.com/";
            } else { //旧版本的Facebook
                return "fb://page/";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "https://www.facebook.com/"; //要是没有安装就用普通的url
        }
    }

    private static int REQUEST_CODE_GOOGLE_SIGN_IN = 62990;
    private static int REQUEST_CODE_GOOGLE_SIGN_IN_OLD = 62991;
    private static int REQUEST_CODE_HONOR_SIGN_IN = 1001;

    private static String logingType="";

    public void opengoogleLogin(Activity context, LoginCallBack callBack,String type) {

        logingType=type;
        if (!"bind".equals(type)) {
            //检查已登录的google账号信息
            String name = sharedPreferences.getString("myths_googleid", "");
            if (!name.equals("")) {
                long expiredTime = MyGamesImpl.getSharedPreferences().getLong("myths_googleidtoken_expiredTime", 0);
                if(System.currentTimeMillis()/1000<expiredTime){//token未过期
                    HttpUtils.gotoAutoLoginActivity("google");
                    return;
                }
            }
        }

        String google_login_id = ResourceUtil.getString(context, "pg_google_login_id");
        MLog.a("google_login_id----->" + google_login_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(google_login_id)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        context.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_GOOGLE_SIGN_IN_OLD);
    }
    public void googleLoginNewApi(Activity context, LoginCallBack callBack,String google_login_id){
        //新版google登录可能会出现错误码10无法登录的情况
        GetSignInIntentRequest request = GetSignInIntentRequest.builder()
                .setServerClientId(google_login_id)
                .build();
        Identity.getSignInClient(context)
                .getSignInIntent(request)
                .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        try {
                            context.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_GOOGLE_SIGN_IN,
                                    null, 0, 0, 0, null);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            PhoneTool.submitSDKEvent("9",e.getMessage());
                            MySdkApi.getLoginCallBack().loginFail("10041");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MLog.a("Google Sign-in failed-->" + e.getMessage());
                        PhoneTool.submitSDKEvent("9",e.getMessage());
                        callBack.loginFail("1003");
                    }
                });
    }

    public void openappleLogin(Activity context, LoginCallBack callBack) {

//        OAuthProvider.Builder provider = OAuthProvider.newBuilder("apple.com");
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        Task<AuthResult> pending = mAuth.getPendingAuthResult();
//        if(pending != null){
//
//            pending.addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//                    MLog.a("Apple login--Success>");
//
//                    FirebaseUser user = authResult.getUser();
//                    if(user != null){
//                        String appleid =user.getUid();
//                        String appleemail =user.getEmail();
//
//                        HttpUtils.applelogin_check(appleid, appleemail);
//                    }else{
//                        callBack.loginFail("Apple login back user info is null" );
//                    }
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    MLog.a("Apple login--Fail>"+e.getMessage());
//                    callBack.loginFail("Apple login failed-->" + e.getMessage());
//                }
//            });
//        }else{
//            mAuth.startActivityForSignInWithProvider(context,provider.build()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//
//                    MLog.a("Apple login--Success>");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    MLog.a("Apple login--Fail>"+e.getMessage());
//
//                    callBack.loginFail("Apple login failed-->" + e.getMessage());
//
//                }
//            });
//
//        }
    }

    public void openTwitterLogin(Activity context, LoginCallBack callBack) {

    }
    public void openWalletLogin(Activity context, LoginCallBack callBack){
        String name = sharedPreferences.getString("myths_walletid", "");
        if (!name.equals("")) {
            HttpUtils.gotoAutoLoginActivity("wallet");
            return;
        }
        //walletinit
        walletInit();

        tim = new Timer();
        try {
            tim.schedule(new TimerTask() {
                @Override
                public void run() {
                    PhoneTool.disDialog();
                }
            },5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PhoneTool.onCreateDialog(context,"","");
        WalletCallback wcallback = new WalletCallback() {

            @Override
            public void signCallback(String walletaddress,@NonNull String sign) {
                HttpUtils.walletLogin_check(walletaddress, sign,m_nonce);
            }

            @Override
            public boolean connectCallback(@Nullable String walletaddress,Integer chainId,String chainIdStr) {
                PhoneTool.disDialog();
                try {
                    tim.cancel();
                    tim = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(walletaddress!=null){
                    getLoginNonce(walletaddress,TextUtils.isEmpty(chainIdStr)?(chainId+""):chainIdStr);
                }else{
                    callBack.loginFail("connect failed");
                }
                return true;
            }

            @Override
            public void payCallback(int code, @Nullable String msg) {

            }
        };
        tim.schedule(new TimerTask() {
            @Override
            public void run() {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectWallet(wcallback,"56",true);
                    }
                });
            }
        },1000);
    }
    //钱包登录第一步：连接钱包获取walletaddress和chainId请求服务端拿到nonce
    private void getLoginNonce(String walletaddress,String chainId) {
        HttpUtils.getLoginNonce(walletaddress, chainId, new GetNonceCallBack() {
            @Override
            public void callback(boolean issuccess, String nonce) {
                if(!issuccess){
                    MySdkApi.getLoginCallBack().loginFail("connect fail");
                }else{
                    m_nonce = nonce;
                    signWallet(nonce);
                }
            }
        });
    }

    public void openphoneLogin(Activity context, LoginCallBack callBack) {

    }

    public void wemixLogin(Activity context, LoginCallBack callBack){
        String address = sharedPreferences.getString("myths_wemixaddress", "");
        if (!address.equals("")) {
            long timestamp_now = System.currentTimeMillis();
            long timestamp_login = MyGamesImpl.getSharedPreferences().getLong("myths_wemixtimestamp",0);
            long expires = MyGamesImpl.getSharedPreferences().getLong("myths_wemixexpires",0);
            if((timestamp_now-timestamp_login)/1000+10<expires) {//从wemix登录到再次登录的时间间隔小于token的有效期
                HttpUtils.gotoAutoLoginActivity("wemix");
                return;
            }
        }

        WemixUtil.getInstance().login(context);
    }

    public void acclogin(Activity act, String name, String password) {
        HttpUtils.acclogin(act,name,password);
    }

    public void logFBEvent(String eventName, Bundle bundle) {

    }

    public void logFireBaseEvent(String eventName, Bundle bundle) {

//        if(mFirebaseAnalytics!=null){
//            mFirebaseAnalytics.logEvent(eventName, bundle);
//            MLog.a("logFireBaseEvent");
//
//        }else{
//            FirebaseAnalytics.getInstance(activity).logEvent(eventName, bundle);
//            MLog.a("logFireBaseEvent");
//        }
        MLog.a("logFireBaseEvent111111111111111111");

    }

    public void logFireBaseEventWithMap(String eventName, Map<String, Object> eventMap) {
        Bundle bundleEvent=new Bundle();
        JSONObject jsonEvent=new JSONObject();
        for (Object o : eventMap.keySet()){
            MLog.a("key=" + o + " value=" + eventMap.get(o));
            try {
                bundleEvent.putString(o.toString(),eventMap.get(o).toString());
                jsonEvent.put(o.toString(),eventMap.get(o).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        if(mFirebaseAnalytics!=null){
//            mFirebaseAnalytics.logEvent(eventName, bundleEvent);
//            MLog.a("logFireBaseEvent");
//
//        }else{
//            FirebaseAnalytics.getInstance(activity).logEvent(eventName, bundleEvent);
//            MLog.a("logFireBaseEvent");
//        }

        //上报给中台
        try {
            HttpUtils.logdataReport(eventName,jsonEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logEventWithMap(String eventName, Map<String, Object> eventMap) {

    }

    /**
     * Facebook付费完成 * @param sku 计费点 * @param priceCent价格 * @param transactionId 内部订单号 * @param orderId交易平台外部订单号
     */
    //String sku, int priceCent, String transactionId, String orderId
    public void payFinishEventFB() {
//        if (facebookLogger == null) {
//            return;
//        }
//
//        //            OrderInfo orderinfo=MyApplication.getAppContext().getOrderinfo();
//
//        OrderInfo orderinfo = MyApplication.getAppContext().getOrderinfo();
//        Double money = 0D;
//        try {
//            money = Double.parseDouble(orderinfo.getAmount());
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        MLog.a("payFinish===>" + orderinfo.getFeepoint() + "===" + orderinfo.getAmount() + "===" + orderinfo.getTransactionId() + "===" + "===" + orderinfo.getExtraInfo());
//        //官方事件统计
//        Bundle params = new Bundle();
//        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, 1);
//        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
//        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, orderinfo.getFeepoint());
//        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
//        facebookLogger.logPurchase((BigDecimal.valueOf(money)), Currency.getInstance("USD"), params);
//        MLog.a("facebook stat purchase===》" + orderinfo.getFeepoint() + "===" + money + "===" + orderinfo.getTransactionId() + "===" + "===" + orderinfo.getExtraInfo());

    }

    /**
     * Appsflyer 付费完成 * @param sku 计费点 * @param priceCent 价格 美分 * @param transactionId 内部订单号 * @param orderId 交易平台外部订单号
     */
    //String sku, float priceCent, String transactionId, String orderId
    public void payFinishEventAF() {

//        OrderInfo orderinfo = MyApplication.getAppContext().getOrderinfo();
//        Float money = 0F;
//        try {
//            money = Float.parseFloat(orderinfo.getAmount());
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
//        DecimalFormat df = new DecimalFormat("#.00", symbols);
//        float priceDeci = Float.parseFloat(df.format(money));
//        Map<String, Object> eventValue = new HashMap<String, Object>();
//        eventValue.put(AFInAppEventParameterName.REVENUE, priceDeci);
//        eventValue.put(AFInAppEventParameterName.CURRENCY, "USD");
//        eventValue.put(AFInAppEventParameterName.QUANTITY, 1);
//        eventValue.put(AFInAppEventParameterName.CONTENT_ID, orderinfo.getFeepoint());
//        eventValue.put(AFInAppEventParameterName.ORDER_ID, orderinfo.getTransactionId());
//        eventValue.put("af_transitionid", orderinfo.getTransactionId());
//        AppsFlyerLib.getInstance().logEvent(activity.getApplicationContext(), AFInAppEventType.PURCHASE, eventValue);
//        MLog.a("jin Appsflyer  payFinishReport===》sku:" + orderinfo.getFeepoint() + " === priceDeci:" + priceDeci + " === transactionId:" + orderinfo.getTransactionId() + " === orderId:" + orderinfo.getTransactionId());
    }

    public void getPhoneCode(Activity context,String areaCode,String num, PhoneCodeCallBack callBack) {
        HttpUtils.getPhoneCode(areaCode,num,callBack);
    }

    public void openEmailLogin(Activity context, LoginCallBack callBack,String type) {
        if (!"bind".equals(type)) {
            //检查已登录的email账号信息
            String name = sharedPreferences.getString("myths_email", "");
            if (!name.equals("")) {
                HttpUtils.gotoAutoLoginActivity("email");
                return;
            }
        }
        //跳转email登录界面
        Intent itn = new Intent(MySdkApi.getMact(), EmailLoginActivity.class);
        itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itn.putExtra("type",type);
        MySdkApi.getMact().startActivity(itn);
    }

    public void getEmailCode(Activity context, String email, EmailCodeCallBack callBack){
        HttpUtils.getEmailCode(context,email,callBack);
    }

    public void unBindSDK(Activity context, String areaCode, String phoneNum, String code,String loginType, UnBindSDKCallBack callBack) {

        HttpUtils.getUnBindSDK(areaCode,phoneNum,code,loginType,callBack);

    }

    public void bindPhone(Activity context, String type, String areaCode,String phoneNum, String code, String password, BindPhoneCallBack callBack) {

        HttpUtils.getbindPhone(type,areaCode,phoneNum,code,password,callBack);
    }

    public void phoneLogin(Activity context, String areaCode, String phoneNum, String code, String password, LoginCallBack callBack) {
        HttpUtils.getPhoneLogin(areaCode,phoneNum,code,password,callBack);
    }

    public void emailLogin(Activity context, String email, String code,boolean isBind) {
        HttpUtils.getEmailLogin(context,email,code,isBind);
    }

    public void checkPhoneCode(Activity context, String areaCode, String phoneNum, String code, CheckCodeCallBack callBack) {
        HttpUtils.getCheckPhoneCode(areaCode,phoneNum,code,callBack);
    }

    public void phoneLogin2(Activity context, String areaCode, String phoneNum, String password, LoginCallBack callBack) {
        HttpUtils.getPhoneLogin2(areaCode,phoneNum,password,callBack);
    }

    public void changePassword(Activity context,String areaCode,String phoneNum, String oldPassword, String newPassword, ChangePasswordCallBack callBack) {
        HttpUtils.getChangePassword(areaCode,phoneNum,oldPassword,newPassword,callBack);
    }

    public void forgetPassword(Activity context, String areaCode, String phoneNum,String verifycode,String newPassword, ChangePasswordCallBack callBack) {
        HttpUtils.forgetPassword(areaCode,phoneNum,verifycode,newPassword,callBack);
    }

    //用于搜集游戏的错误日志
    public void dataReport(String eventName, JSONObject json) {
        PhoneTool.submitErrorEvent(Configs.getGameErrorCode(),eventName+":"+json.toString());
//        HttpUtils.logdataReport(eventName,json);
    }


    public void setfirebaseid(Activity context,  String firebaseid) {

        HttpUtils.setfirebaseid(firebaseid);
    }

    public void loginoutOther(Activity act) {
        try{
            guestlogout();
            logout();
            googlelogout();
            walletlogout();
        }catch (Exception ignored){
        }

    }

    private void guestlogout(){
        SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
        sharedPreferences.edit().putString("myths_youke_name","").apply();
    }
    /************************** 荣耀************************************/
    public void openHonorLogin(Activity context, LoginCallBack callBack) {

//        SignInOptions signInOptions = new SignInOptionBuilder(SignInOptions.DEFAULT_AUTH_REQUEST_PARAM)
//                .setClientId("100100088")
//                .createParams();
//        Intent signInIntent = HonorIdSignInManager.getService(context, signInOptions).getSignInIntent();
//        if (null==signInIntent){
//            MLog.a("Honor version too low");
//            callBack.loginFail("Honor version too low");
//            return;
//        }
//        context.startActivityFromChild(context, signInIntent, REQUEST_CODE_HONOR_SIGN_IN);
    }

//    private IapClient iapClient;
//    private String honor_id="";
//    //其他支付方式
    public void openOtherPay(Activity context, String orderid, String feepoint, String payconfirmurl) {
//        System.out.println("honorPurchaseSDK  openOtherPay  "   );
//        iapClient = Iap.getIapClient(context);
//        //检查当前环境是否可用
//        honor_id=orderid;
//        iapClient.checkEnvReady().addOnSuccessListener(new com.hihonor.iap.sdk.tasks.OnSuccessListener<IsEnvReadyResult>() {
//            @Override
//            public void onSuccess(IsEnvReadyResult isEnvReadyResult) {
//                //可用
//
//                cban();
//                Creatorder(context,orderid,feepoint,payconfirmurl);
//            }
//        }).addOnFailureListener(new com.hihonor.iap.sdk.tasks.OnFailureListener() {
//            @Override
//            public void onFailure(com.hihonor.iap.framework.data.ApiException e) {
//                //不可用
//                MLog.a("checkEnvReady %d %s"+ e.errorCode+e.message);
//            }
//        });
//
//
//
    }
    private static final int REQUEST_CODE_PAY = 1002;
    private static final String PUBLIC_KEY="0";
    //创建订单
//    private void Creatorder(Activity context, String orderid, String feepoint, String payconfirmurl) {
//
//        System.out.println("honorPurchaseSDK  openOtherPay  Creatorder"   );
//        ProductOrderIntentReq productOrderIntentReq = new ProductOrderIntentReq();
//        productOrderIntentReq.setProductType(ProductType.CONSUME);
//        productOrderIntentReq.setProductId(feepoint);
//        productOrderIntentReq.setBizOrderNo(orderid);
//        productOrderIntentReq.setNeedSandboxTest(1);
//
//        //防止掉单
//        //创建订单前，需要调用obtainOwnedPurchases 查询已购买，未消耗的商品，进行消耗
//        com.hihonor.iap.sdk.tasks.Task<ProductOrderIntentResult> productOrderIntent =
//                iapClient.createProductOrderIntent(productOrderIntentReq);
//        productOrderIntent.addOnSuccessListener(createProductOrderResp -> {
//            Intent intent = createProductOrderResp.getIntent();
//
//            if (intent != null) {
//                //
//                System.out.println("honorPurchaseSDK  openOtherPay  Creatorder  intent"   );
//                context.startActivityForResult(intent, REQUEST_CODE_PAY);
//            }
//        }).addOnFailureListener(e -> {
//            //   e.errorCode 对应 OrderStatusCode的值
//            MLog.a(  String.format("createProductOrderIntent %d %s", e.errorCode, e.message));
//        });
//
//    }
//    private String continueToken = "";
//    private String continueTokenRecord = "";
//    //查询商品信息
//    private void  cccorder(Activity context, String orderid, String feepoint, String payconfirmurl) {
//        ProductInfoReq productInfoReq = new ProductInfoReq();
//        productInfoReq.setProductType(ProductType.CONSUME);
//        List<String> list = new ArrayList<String>();
//        list.add(orderid);
//        productInfoReq.setProductIds(list);
//        com.hihonor.iap.sdk.tasks.Task<ProductInfoResult> productInfo = iapClient.getProductInfo(productInfoReq);
//        productInfo.addOnSuccessListener(productInfoResult -> {
//                }
//        ).addOnFailureListener(e -> {
//            MLog.a( String.format("getProductInfo %d %s", e.errorCode, e.message));
//        });
//    }
//
//    //查询已购买未消耗的列表
//    private boolean ishonornopay=false;
//
//    private void cban(){
//
//        System.out.println("honorPurchaseSDK  openOtherPay cban "   );
//        OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
//
//
//        //传入上一次查询得到的continueToken，获取新的数据，第一次传空
//        ownedPurchasesReq.setContinuationToken(continueToken);
//        iapClient.obtainOwnedPurchases(ownedPurchasesReq).addOnSuccessListener(new com.hihonor.iap.sdk.tasks.OnSuccessListener<OwnedPurchasesResult>() {
//            @Override
//            public void onSuccess(OwnedPurchasesResult ownedPurchasesResult) {
//                System.out.println("honorPurchaseSDK  obtainOwnedPurchases "   );
//                //ContinueToken用于获取下一个列表的数据，第一次为空，如果有更多数据ContinueToken有值，为空则没有更多数据
//                continueToken = ownedPurchasesResult.getContinueToken();
//
//                // purchaseList 和 sigList 一一对应
//                List<String> sigList = ownedPurchasesResult.getSigList();
//                List<String> purchaseList = ownedPurchasesResult.getPurchaseList();
//
//               // if(purchaseList.size()>0&&purchaseList.size()==sigList.size()){
//                    //有掉单
//                    ishonornopay=true;
//                    //签名算法
//                    String sigAlgorithm = ownedPurchasesResult.getSigAlgorithm();
//                    //                    公钥验签
//                    if ("Honorsign.RSA".equals(sigAlgorithm)) {
//                        try {
//                            PublicKey publicKey = RSAUtil.getPublicKey( "s");
//                            //   LogUtils.d(TAG, " publicKey :" + publicKey);
//                            System.out.println("honorPurchaseSDK   publicKey :" + publicKey);
//                            String token="",oriderid="";
//                            for (int i = 0; i < purchaseList.size(); i++) {
//                                String PurchaseProductInfoStr = purchaseList.get(i);
//                                boolean verify = RSAUtil.verify(PurchaseProductInfoStr, publicKey, sigList.get(i));
//                                System.out.println("honorPurchaseSDK   verify :" + verify);
//                                if(verify){
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(PurchaseProductInfoStr);
//                                          token=jsonObject.getString("purchaseToken");
//                                          oriderid=jsonObject.getString("orderId");
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    String finalToken = token;
//                                    ConsumeReq comsumeReq = new ConsumeReq();
//                                    //根据PurchaseToken 进行消耗
//                                    comsumeReq.setPurchaseToken(finalToken);
//                                    com.hihonor.iap.sdk.tasks.Task<ConsumeResult> comsumeRespTask = iapClient.consumeProduct(comsumeReq);
//                                    comsumeRespTask.addOnSuccessListener(comsumeResp ->
//                                            //消耗成功
//                                            MySdkApi.getMpaycallBack().payFinish() )
//                                            .addOnFailureListener(e ->
//                                                    //消耗失败
//                                                     MySdkApi.getMpaycallBack().payFail( e.getErrorCode() + ": " + e.getMessage()));
//                                    /*HttpUtils.honorPurchaseSDK(oriderid,token, new PayConsumeCallback() {
//                        @Override
//                        public void result(Boolean issuc, String msg) {
//                            if (issuc) {
//                                System.out.println("honorPurchaseSDK  openOtherPay cban  issuc"   );
//                                MySdkApi.getMpaycallBack().payFinish();
//
//                            } else {
//                                System.out.println("honorPurchaseSDK--openOtherPay cban fail" + msg);
//                                MySdkApi.getMpaycallBack().payFail( msg);
//                            }
//                        }
//                    });*/
//                                }
//
//                                //  Log.d(TAG, " PurchaseProductInfoStr verify " + verify + "  , " + PurchaseProductInfoStr);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//
//
//
//               // }
//
//
//
//
//              //  Log.d(TAG, ownedPurchasesResult.toString());
//            }
//        }).addOnFailureListener(new com.hihonor.iap.sdk.tasks.OnFailureListener() {
//            @Override
//            public void onFailure(com.hihonor.iap.framework.data.ApiException e) {
//                //   e.errorCode 对应 OrderStatusCode的值
//                MLog.a( String.format("createProductOrderIntent %d %s", e.errorCode, e.message));
//            }
//        });
//      //  return false;
//    }

    //钱包初始化
    public void walletInit(){
        WalletUtil.getInstance().walletInit(activity);
    }

    /**
     *  连接钱包
     * @param callback  回调
     * @param chainId   链id
     * @param onlyRequestLogin  是否只用于请求登录
     */
    public void connectWallet(WalletCallback callback, String chainId,boolean onlyRequestLogin){
        WalletUtil.getInstance().connectWallet(callback,chainId,onlyRequestLogin);
    }
    //钱包支付
    public void payWallet(String wallet_authData, String wallet_payData, String wallet_authTo, String wallet_payTo,String wallet_nonce){
        WalletUtil.getInstance().payWallet(wallet_authData,wallet_payData,wallet_authTo,wallet_payTo,wallet_nonce);
    }
    //钱包签名
    public void signWallet(String nonce){
        WalletUtil.getInstance().signWallet(nonce);
    }

    public void closeConnect(){
        WalletUtil.getInstance().closeConnect();
    }
    public void closeConnectAndFresh(){
//        if(wcutils!=null) wcutils.close();
    }

}
