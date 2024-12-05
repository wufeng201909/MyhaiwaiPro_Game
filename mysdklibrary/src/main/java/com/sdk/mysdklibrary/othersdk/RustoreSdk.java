package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;

import java.util.List;

import kotlin.Unit;
import ru.rustore.sdk.billingclient.RuStoreBillingClient;
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory;
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult;
import ru.rustore.sdk.billingclient.model.purchase.Purchase;
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState;
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase;
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult;
import ru.rustore.sdk.core.tasks.OnFailureListener;
import ru.rustore.sdk.core.tasks.OnSuccessListener;

public class RustoreSdk {
    private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
    private static RuStoreBillingClient billingClient;
    private static boolean isResumeCheck = true;
    public static void initSDK(Activity activity) {
        String appId = ResourceUtil.getString(activity, "ruStore_appId");
        String deeplinkScheme = ResourceUtil.getString(activity, "ruStore_deeplinkScheme");
        if(TextUtils.isEmpty(appId)) return;
        if(billingClient == null){
            billingClient = RuStoreBillingClientFactory.INSTANCE.create(
                    activity.getApplicationContext(),
                    appId,
                    deeplinkScheme
            );
        }

        try{
            if(activity.getIntent() != null)
                billingClient.onNewIntent(activity.getIntent());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        if(billingClient == null) return;
        isResumeCheck = false;
        sharedPreferences.edit().putString("rustore_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();

        billingClient.getPurchases().checkPurchasesAvailability().addOnSuccessListener(new OnSuccessListener<FeatureAvailabilityResult>() {
            @Override
            public void onSuccess(FeatureAvailabilityResult featureAvailabilityResult) {
                if (featureAvailabilityResult instanceof FeatureAvailabilityResult.Available) {
                    // Process purchases available
                    pay(orderId,extra1);
                } else if (featureAvailabilityResult instanceof FeatureAvailabilityResult.Unavailable) {
                    // Process purchases unavailable
                    String msg = "unavailable";
                    try{
                        ((FeatureAvailabilityResult.Unavailable) featureAvailabilityResult).getCause().printStackTrace();
                        msg = ((FeatureAvailabilityResult.Unavailable) featureAvailabilityResult).getCause().getMessage();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    MySdkApi.getMpaycallBack().payFail("FeatureAvailabilityResult.Unavailable:"+msg);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Throwable throwable) {
                throwable.printStackTrace();
                MySdkApi.getMpaycallBack().payFail("checkPurchasesAvailability:false:"+throwable.getMessage());
            }
        });

    }

    private static void pay(String orderId,String proId){
        PurchasesUseCase purchasesUseCase = billingClient.getPurchases();
        purchasesUseCase.purchaseProduct(proId,orderId,1,orderId).addOnSuccessListener(new OnSuccessListener<PaymentResult>() {
            @Override
            public void onSuccess(PaymentResult paymentResult) {
                // Process PaymentResult
                handlePaymentResult(paymentResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Throwable throwable) {
                throwable.printStackTrace();
                MySdkApi.getMpaycallBack().payFail("purchaseProduct:Fail:"+throwable.getMessage());
            }
        });
    }
    private static void handlePaymentResult(PaymentResult paymentResult) {
        if (paymentResult instanceof PaymentResult.Cancelled) {//取消
//            String purchaseId = ((PaymentResult.Cancelled) paymentResult).getPurchaseId();
//            purchasesUseCase.deletePurchase(purchaseId);
            MySdkApi.getMpaycallBack().payFail("Cancelled");
        } else if (paymentResult instanceof PaymentResult.Success) {//成功
            PaymentResult.Success purchaseResult = ((PaymentResult.Success) paymentResult);
            String subscriptionToken = purchaseResult.getSubscriptionToken();
            String orderId = purchaseResult.getOrderId();
            String proId = purchaseResult.getProductId();
            String purchaseId = purchaseResult.getPurchaseId();
            handlePurchase(orderId,proId,purchaseId,subscriptionToken);
        } else if (paymentResult instanceof PaymentResult.Failure) {//失败
//            String purchaseId = ((PaymentResult.Failure) paymentResult).getPurchaseId();
//            if (purchaseId != null) {
//                purchasesUseCase.deletePurchase(purchaseId);
//            }
            MySdkApi.getMpaycallBack().payFail("Failure:"+((PaymentResult.Failure) paymentResult).getErrorCode());
        } else if (paymentResult instanceof PaymentResult.InvalidPaymentState) {
            MySdkApi.getMpaycallBack().payFail("InvalidPaymentState");
        }
    }

    //请求服务端进行支付验证
    private static void handlePurchase(String orderId,String proId,String purchaseId,String token) {
        String url = sharedPreferences.getString("rustore_conf_url", "");

        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
        HttpUtils.consumePurchase(url, orderId, proId, token, new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if (issuc) {
                    MyGamesImpl.getInstance().ADJSubmit(4, orderId, msg);
                    if (MySdkApi.getMpaycallBack() != null)
                        MySdkApi.getMpaycallBack().payFinish();
                    consumeAsync(purchaseId, orderId);
                } else {
                    System.out.println("consumePurchase--"+issuc+"--" + msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFail(msg);
                }
            }
        });
    }

    //查询漏单
    private static void checkOwnedPurchases() {
        if(billingClient == null) return;
        PurchasesUseCase purchasesUseCase = billingClient.getPurchases();
        purchasesUseCase.getPurchases().addOnSuccessListener(new OnSuccessListener<List<Purchase>>() {
            @Override
            public void onSuccess(List<Purchase> purchasesResponse) {
                // Process success
                for (Purchase purchase: purchasesResponse) {
                    if (purchase.getPurchaseId() != null) {
                        if (purchase.getPurchaseState() == PurchaseState.CREATED || purchase.getPurchaseState() == PurchaseState.INVOICE_CREATED) {
                        } else if (purchase.getPurchaseState() == PurchaseState.PAID) {
                            handlePurchase(purchase.getOrderId(),purchase.getProductId(),purchase.getPurchaseId(),purchase.getSubscriptionToken());
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Throwable throwable) {
                MLog.a("getPurchases--fail:"+throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }
    //消耗确认
    private static void consumeAsync(String purchaseId,String developerPayload) {
        PurchasesUseCase purchasesUseCase = billingClient.getPurchases();
        purchasesUseCase.confirmPurchase(purchaseId, developerPayload).addOnSuccessListener(new OnSuccessListener<Unit>() {
            @Override
            public void onSuccess(Unit result) {
                // Process success
                MLog.a("consumeAsync--onSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Throwable throwable) {
                throwable.printStackTrace();
                MLog.a("consumeAsync--onFailure");
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
    public static void onNewIntent(Activity activity, Intent intent) {
        if(billingClient != null)billingClient.onNewIntent(intent);
    }
}
