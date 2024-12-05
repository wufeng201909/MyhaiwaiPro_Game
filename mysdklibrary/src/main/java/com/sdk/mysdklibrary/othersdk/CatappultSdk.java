package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.appcoins.sdk.billing.AppcoinsBillingClient;
import com.appcoins.sdk.billing.BillingFlowParams;
import com.appcoins.sdk.billing.Purchase;
import com.appcoins.sdk.billing.PurchasesResult;
import com.appcoins.sdk.billing.PurchasesUpdatedListener;
import com.appcoins.sdk.billing.ResponseCode;
import com.appcoins.sdk.billing.SkuDetails;
import com.appcoins.sdk.billing.SkuDetailsParams;
import com.appcoins.sdk.billing.helpers.CatapultBillingAppCoinsFactory;
import com.appcoins.sdk.billing.listeners.AppCoinsBillingStateListener;
import com.appcoins.sdk.billing.listeners.ConsumeResponseListener;
import com.appcoins.sdk.billing.listeners.SkuDetailsResponseListener;
import com.appcoins.sdk.billing.types.SkuType;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;

import java.util.ArrayList;
import java.util.List;

public class CatappultSdk {
    private static Activity myCon;
    private static AppcoinsBillingClient cab;
    private static boolean isResumeCheck = false;
    private static PurchasesUpdatedListener purchaseUpdatedListener = (responseCode, purchases) -> {
        if (responseCode == ResponseCode.OK.getValue()) {
            if(purchases == null){
                MySdkApi.getMpaycallBack().payFail("onPurchasesUpdated--null purchase list");
                return;
            }
            for (Purchase purchase : purchases) {
                String token = purchase.getToken();
                String orderId = purchase.getDeveloperPayload();
                String sku = purchase.getSku();
                // After validating and attributing consumePurchase may be called
                // to allow the user to purchase the item again and change the purchase's state.
                // Also consume subscriptions to make them active, there will be no issue in consuming more than once
                handlePurchase(orderId,sku,token);
            }
        } else {
            MySdkApi.getMpaycallBack().payFail("onPurchasesUpdated-code:"+responseCode);
        }
    };
    private static AppCoinsBillingStateListener appCoinsBillingStateListener = new AppCoinsBillingStateListener() {
        @Override public void onBillingSetupFinished(int responseCode) {
            if (responseCode != ResponseCode.OK.getValue()) {
                MLog.a("Problem setting up in-app billing: " + responseCode);
                return;
            }
            cab.launchAppUpdateDialog(myCon);//自动检查应用更新
            // Check for pending and/or owned purchases
            checkOwnedPurchases();
        }

        @Override public void onBillingServiceDisconnected() {
            MLog.a( "Disconnected");
        }
    };
    public static void initSDK(Activity activity) {
        String base64EncodedPublicKey = ResourceUtil.getString(activity, "catappult_base64EncodedPublicKey");
        myCon = activity;
        cab = CatapultBillingAppCoinsFactory.BuildAppcoinsBilling(
                activity.getApplicationContext(),
                base64EncodedPublicKey,
                purchaseUpdatedListener
        );
        cab.startConnection(appCoinsBillingStateListener);
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        isResumeCheck = false;
        MyGamesImpl.getSharedPreferences().edit().putString("cp_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();
        List<String> inapps = new ArrayList<String>();
        // Fill the inapps with the skus of items
        inapps.add(extra1);
        SkuDetailsParams skuDetailsParams = new SkuDetailsParams();
        skuDetailsParams.setItemType(SkuType.inapp.toString());
        skuDetailsParams.setMoreItemSkus(inapps);
        cab.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                MLog.a("Received skus " + responseCode);
                for (SkuDetails sku: skuDetailsList) {
                    // You should add these details to a list in order to update
                    // UI or use it in any other way
                    startPurchase(activity,sku.getSku(),orderId);
                }
            }
        });
    }

    private static void startPurchase(Activity activity,String sku, String developerPayload) {
        if (!cab.isReady()) {
            String msg = "Billing service is not ready yet to make purchases.";
            MLog.a( msg);
            MySdkApi.getMpaycallBack().payFail(msg);
            return;
        }

        MLog.a("Launching purchase flow.");
        // Your sku type, can also be SkuType.subs.toString()
        BillingFlowParams billingFlowParams =
                new BillingFlowParams(
                        sku,
                        SkuType.inapp.toString(),
                        developerPayload,
                        developerPayload,
                        "BDS"
                );

        Thread thread = new Thread(() -> {
            final int responseCode = cab.launchBillingFlow(activity, billingFlowParams);
            activity.runOnUiThread(() -> {
                if (responseCode != ResponseCode.OK.getValue()) {
                    String msg = "Error purchasing with response code : " + responseCode;
                    MLog.a(msg);
                    MySdkApi.getMpaycallBack().payFail(msg);
                }
            });
        });
        thread.start();
    }

    public static void onActivityResult(final Activity act,int requestCode, int resultCode, Intent data) {
        cab.onActivityResult(requestCode, resultCode, data);
    }

    //请求服务端进行支付验证
    private static void handlePurchase(String orderId,String sku,String purchaseToken) {
        String url = MyGamesImpl.getSharedPreferences().getString("cp_conf_url", "");
        //url为空时取初始化回传的值
        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
        HttpUtils.consumePurchase(url, orderId, sku, purchaseToken, new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if (issuc) {
                    MyGamesImpl.getInstance().ADJSubmit(4, orderId, msg);
                    if (MySdkApi.getMpaycallBack() != null)
                        MySdkApi.getMpaycallBack().payFinish();
                    consumeAsync(purchaseToken);
                } else {
                    System.out.println("consumePurchase--"+issuc+"--" + msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFail(msg);
                }
            }
        });
    }
    //消耗确认
    private static void consumeAsync(String purchaseToken) {
        cab.consumeAsync(purchaseToken, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(int responseCode, String purchaseToken) {
                MLog.a("Consumption finished. Purchase: " + purchaseToken + ", result: " + responseCode);
                if (responseCode == ResponseCode.OK.getValue()) {
                    System.out.println("consumeAsync--OK");
                    //Your SKU logic goes here
                } else {
                    System.out.println("consumeAsync--onFailure");
                }
            }
        });
    }

    //查询漏单
    private static void checkOwnedPurchases() {
        Thread thread = new Thread(() -> {
            PurchasesResult purchasesResult = cab.queryPurchases(SkuType.inapp.toString());
            List<Purchase> purchases = purchasesResult.getPurchases();
            for (Purchase purchase : purchases) {
                String orderId = purchase.getDeveloperPayload();
                String sku = purchase.getSku();
                String token = purchase.getToken();
                handlePurchase(orderId,sku,token);
            }
        });
        thread.start();
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
