package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;

import java.util.Collections;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import ru.appbazar.sdk.api.SdkApiClient;
import ru.appbazar.sdk.api.SdkApiClientCallback;
import ru.appbazar.sdk.api.SdkApiClientDispatcher;
import ru.appbazar.sdk.model.MarketProduct;
import ru.appbazar.sdk.model.MarketProductsFilter;
import ru.appbazar.sdk.model.NewPurchaseResult;
import ru.appbazar.sdk.model.PurchaseUserParams;
import ru.appbazar.sdk.model.SdkApiOptions;
import ru.appbazar.sdk.model.SdkRequestResult;
import ru.appbazar.sdk.model.networkrequest.ActivePurchase;

public class AppBazarSdk {

    private static SdkApiClient sdkApiC;
    private static boolean isResumeCheck = true;
    private static boolean isConnected = false;
    private static String appId = "appBazar_appId";
//    private static String appId = "25c83c8e-8f68-4e84-b33c-b9c9ae51ecba";

    public static void applicationOnCreate(Application app) {
        SdkApiOptions op = new SdkApiOptions.Builder(appId)
                .setLogsUsing(true)
                .setAnalyticsEnable(true)
                .build();
        SdkApiClientDispatcher.Companion.init(app,op);
    }
    public static void initSDK(Activity act){
        SdkApiClientDispatcher.Companion.get().registerNewPurchasesListener(new Function1<NewPurchaseResult, Unit>() {
            @Override
            public Unit invoke(NewPurchaseResult newPurchaseResult) {
                handlePurchase(newPurchaseResult);
                return null;
            }
        });
    }

//    public static void loginSDK(final Activity activity,String type) {
//        System.out.println("appbazar-loginSDK-type:"+type);
//        SdkApiClientDispatcher.Companion.get().requestClient(new SdkApiClientCallback() {
//            @Override
//            public void success(@NonNull SdkApiClient sdkApiClient) {
//                System.out.println("appbazar-loginSDK--success:"+type);
//                isConnected = true;
//                sdkApiC = sdkApiClient;
//                sdkApiC.registerOnCloseListener(new Function0<Unit>() {
//                    @Override
//                    public Unit invoke() {
//                        System.out.println("appbazar-loginSDK-closed");
//                        isConnected = false;
//                        return null;
//                    }
//                });
//                sdkApiClient.getUserId(new Function1<SdkRequestResult<String>, Unit>() {
//                    @Override
//                    public Unit invoke(SdkRequestResult<String> sdkRequestResult) {
//                        System.out.println("appbazar-loginSDK--success--invoke");
//                        if(sdkRequestResult instanceof SdkRequestResult.Success ){
//                            String uid = ((SdkRequestResult.Success<String>) sdkRequestResult).getValue();
//                            // 获取玩家信息成功，校验服务器端的玩家信息，校验通过后允许进入游戏
//                            HttpUtils.othersdkLogin(uid, "","");
//                            checkOwnedPurchases();
//                        }else{
//                            String msg = ((SdkRequestResult.Error<String>)sdkRequestResult).getError().getMessage();
//                            System.out.println("login-fail:"+msg);
//                            MySdkApi.getLoginCallBack().loginFail(msg);
//                        }
//                        return null;
//                    }
//                });
//            }
//
//            @Override
//            public void connectInProgress() {
//
//            }
//
//            @Override
//            public void error(@NonNull Throwable throwable) {
//                throwable.printStackTrace();
//                MySdkApi.getLoginCallBack().loginFail("AppBazarSdk-login-error:"+throwable.getMessage());
//            }
//        });
//    }
    private static void reConnect(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2){
        SdkApiClientDispatcher.Companion.get().requestClient(new SdkApiClientCallback() {
            @Override
            public void success(@NonNull SdkApiClient sdkApiClient) {
                System.out.println("appbazar-loginSDK--success");
                isConnected = true;
                sdkApiC = sdkApiClient;
                sdkApiC.registerOnCloseListener(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        System.out.println("appbazar-loginSDK-reclosed");
                        isConnected = false;
                        return null;
                    }
                });
                _paySDK(activity,orderId,paynotifyurl,extra1,extra2);
            }

            @Override
            public void connectInProgress() {

            }

            @Override
            public void error(@NonNull Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
    private static void _paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        isResumeCheck = false;
        MyGamesImpl.getSharedPreferences().edit().putString("appbazar_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();
        sdkApiC.getMarketProductsAsync(new MarketProductsFilter(Collections.singletonList(extra1)), new Function1<SdkRequestResult<MarketProduct[]>, Unit>() {
            @Override
            public Unit invoke(SdkRequestResult<MarketProduct[]> sdkRequestResult) {
                if(sdkRequestResult instanceof SdkRequestResult.Success ){
                    try{
                        MarketProduct mPro = (((SdkRequestResult.Success<MarketProduct[]>) sdkRequestResult).getValue())[0];
                        gotoPay(activity,orderId,mPro);
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("no product");
                        MySdkApi.getMpaycallBack().payFail("no product");
                    }
                }else{
                    System.out.println("product error:"+((SdkRequestResult.Error<MarketProduct[]>) sdkRequestResult).getError().getMessage());
                    MySdkApi.getMpaycallBack().payFail("product error");
                }
                return null;
            }
        });
    }
    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        reConnect(activity,orderId,paynotifyurl,extra1,extra2);
    }

    private static void gotoPay(Activity activity, String orderId,MarketProduct mPro){
        String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
        String accountId = TextUtils.isEmpty(uid)? PhoneTool.getIMEI(activity) :uid;
        System.out.println("accountId-->"+accountId);
        PurchaseUserParams param = new PurchaseUserParams(accountId,orderId);
        sdkApiC.startBuyingProductActivityForResult(mPro, param, new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                System.out.println("AppBazar-payFail-->"+throwable.getMessage());
                MySdkApi.getMpaycallBack().payFail(throwable.getMessage());
                return null;
            }
        });
    }

    //查询漏单
    private static void checkOwnedPurchases() {
        if(sdkApiC == null) return;
        sdkApiC.getActivePurchasesAsync(new Function1<SdkRequestResult<ActivePurchase[]>, Unit>() {
            @Override
            public Unit invoke(SdkRequestResult<ActivePurchase[]> sdkRequestResult) {
                if(sdkRequestResult instanceof SdkRequestResult.Success ){
                    ActivePurchase[] ap = ((SdkRequestResult.Success<ActivePurchase[]>) sdkRequestResult).getValue();
                    for (ActivePurchase activePurchase : ap) {
                        handlePurchase(NewPurchaseResult.Companion.from(activePurchase));
                    }
                }
                return null;
            }
        });
    }

    private static void handlePurchase(NewPurchaseResult purchaseResult){
        if (purchaseResult instanceof NewPurchaseResult.Success) {
            check((NewPurchaseResult.Success) purchaseResult);
        }else{
            String msg = ((NewPurchaseResult.Error)purchaseResult).getThrowable().getMessage();
            System.out.println("purchaseResult-fail:"+msg);
            MySdkApi.getMpaycallBack().payFail("purchaseResult-fail:"+msg);
        }
    }

    private static void check(NewPurchaseResult.Success purchaseResult){
        String order = purchaseResult.getPurchaseUserParams().getObfuscatedProfileId();
        String proId = purchaseResult.getProductId();
        String appbazar_token = purchaseResult.getOrderToken();
        String appbazar_order = purchaseResult.getOrderId();

        String url = MyGamesImpl.getSharedPreferences().getString("appbazar_conf_url", "");
        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
        HttpUtils.consumePurchase(url,order,proId,appbazar_order+"@"+appbazar_token, new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if (issuc) {
                    MyGamesImpl.getInstance().ADJSubmit(4,order, msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFinish();
                    consumeOwnedPurchase(purchaseResult);
                } else {
                    PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"xm:"+ msg);
                    System.out.println("consumePurchaseSDK--"+issuc+"--" + msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFail(msg);
                }
            }
        });
    }

    //消耗确认
    public static void consumeOwnedPurchase(NewPurchaseResult.Success purchaseResult){
        SdkApiClientDispatcher.Companion.get().acknowledgePurchase(purchaseResult);
    }

    public static void onResume(Activity act) {
        //通过isResumeCheck防止支付成功后重复请求查询漏单
        if(isResumeCheck){
            checkOwnedPurchases();
        }else{
            isResumeCheck = true;
        }
    }
}
