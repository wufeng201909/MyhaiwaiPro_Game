package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;
import com.xiaomi.billingclient.api.BillingClient;
import com.xiaomi.billingclient.api.BillingClientStateListener;
import com.xiaomi.billingclient.api.BillingFlowParams;
import com.xiaomi.billingclient.api.BillingResult;
import com.xiaomi.billingclient.api.ConsumeResponseListener;
import com.xiaomi.billingclient.api.Purchase;
import com.xiaomi.billingclient.api.PurchasesResponseListener;
import com.xiaomi.billingclient.api.PurchasesUpdatedListener;
import com.xiaomi.billingclient.api.SkuDetails;
import com.xiaomi.billingclient.api.SkuDetailsParams;
import com.xiaomi.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class XmSdk {
    private static boolean isResumeCheck = true;
    private static BillingClient billingClient;
    private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
    private static PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult == null) {
                MySdkApi.getMpaycallBack().payFail("onPurchasesUpdated--null BillingResult");
                return;
            }
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if(purchases == null){
                    MySdkApi.getMpaycallBack().payFail("onPurchasesUpdated--null purchase list");
                }else{
                    processPurchases(purchases);
                }
            }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.PAYMENT_SHOW_DIALOG) {
                //收银台弹出
            } else {
                MySdkApi.getMpaycallBack().payFail("onPurchasesUpdated:"+billingResult.getDebugMessage());
            }
        }
    };

    private static void processPurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            //处理购买
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                handlePurchase(purchase);
            }
        }
    }

    //请求服务端进行支付验证
    private static void handlePurchase(Purchase purchase) {
        String url = sharedPreferences.getString("xm_conf_url", "");
        //url为空时取初始化回传的值
        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
        String order = purchase.getObfuscatedProfileId();
        HttpUtils.consumePurchase(url,order,purchase.getSkus().get(0),purchase.getPurchaseToken(), new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if (issuc) {
                    MyGamesImpl.getInstance().ADJSubmit(4,order, msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFinish();
                    consumeAsync(purchase.getPurchaseToken());
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
    private static void consumeAsync(String purchaseToken) {
        billingClient.consumeAsync(purchaseToken, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //确认成功，您可再次发起购买
                    System.out.println("consumeAsync--OK");
                }else{
                    PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"xm:"+ s);
                }
            }
        });
    }

    private final static BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingServiceDisconnected() {
            MLog.a("TAG", "onBillingServiceDisconnected");
            billingClient.startConnection(billingClientStateListener);
        }
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            MLog.a("TAG", "Service.code : " + billingResult.getResponseCode() + "        msg : " + billingResult.getDebugMessage());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                checkOwnedPurchases();
            }
        }
    };
    public static void initSDK(Activity activity) {
        if(billingClient == null)
            billingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).build();
        billingClient.enableFloatView(activity);//this为当前开发者app主Activity
        billingClient.startConnection(billingClientStateListener);
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        MLog.a("paySDK--start0");
        isResumeCheck = false;
        sharedPreferences.edit().putString("xm_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();
        List<String> skuList = new ArrayList<>();
        skuList.add(extra1);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        MLog.a("paySDK--start1");
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     @Nullable List<SkuDetails> list) {
                        MLog.a("TAG", "onSkuDetailsResponse");
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (SkuDetails skuDetails: list) {
                                if(extra1.equals(skuDetails.getSku())){
                                    launchBillingFlow(activity,orderId,skuDetails);
                                }
                            }
                        }
                    }
                });
    }
    private static void launchBillingFlow(Activity act,String orderId,SkuDetails skuDetails){
        MLog.a("launchBillingFlow--start");
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
                String accountId = TextUtils.isEmpty(uid)? PhoneTool.getIMEI(act) :uid;
                System.out.println("accountId-->"+accountId);
                BillingFlowParams params = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .setObfuscatedAccountId(accountId)
                        .setObfuscatedProfileId(orderId)
                        .build();
                MLog.a("launchBillingFlow--start1");
                BillingResult result = billingClient.launchBillingFlow(act, params);
                int code = result.getResponseCode();
                if(code == BillingClient.BillingResponseCode.OK){
                    MLog.a("launchBillingFlow--success");
                }else{
                    MLog.a("launchBillingFlow--fail:"+code);
                    PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"xm:launchBillingFlow:"+ code);
                    MySdkApi.getMpaycallBack().payFail("launchBillingFlow:"+code);
                }
            }
        });
    }

    //查询漏单
    private static void checkOwnedPurchases() {
        if(billingClient!=null&&billingClient.isReady()){
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        //"查询成功"
                        processPurchases(list);
                    }
                }
            });
        }
    }
    //回到游戏内时查询漏单
    public static void onResume(Activity act) {
        //通过isResumeCheck防止支付成功后重复请求查询漏单
        if(isResumeCheck){
            checkOwnedPurchases();
        }else{
            isResumeCheck = true;
        }
    }

    public static void onDestroy(Activity act) {
        if (billingClient != null) {
            billingClient.dismissFloatView();
        }
    }
}
