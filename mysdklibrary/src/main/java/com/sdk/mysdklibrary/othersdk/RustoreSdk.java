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
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import ru.rustore.sdk.billingclient.RuStoreBillingClient;
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory;
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult;
import ru.rustore.sdk.billingclient.model.purchase.Purchase;
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState;
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase;
import ru.rustore.sdk.core.tasks.OnFailureListener;
import ru.rustore.sdk.core.tasks.OnSuccessListener;
import ru.rustore.sdk.core.util.RuStoreUtils;

public class RustoreSdk {
    private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
    private static RuStoreBillingClient billingClient;
    private static boolean isResumeCheck = true;
    private static boolean isRuStoreInstalled = false;
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
        isRuStoreInstalled =  RuStoreUtils.INSTANCE.isRuStoreInstalled(activity);

        //开启定时上报（测试）
        long flag = System.currentTimeMillis();
        Timer t = new Timer();
        startCusReport(flag,t);
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        if(billingClient == null) return;
        MLog.a("paySDK--start0");
        isResumeCheck = false;
        sharedPreferences.edit().putString("rustore_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();

        pay(orderId,extra1);
    }

    private static void pay(String orderId,String proId){
        PurchasesUseCase purchasesUseCase = billingClient.getPurchases();
        MLog.a("paySDK--start1");
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
                System.out.println("purchaseProduct--onFailure:"+throwable.getMessage());
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
            String ru_pay_tips = Configs.getItem(Configs.ru_pay_tips);
            if(!"0".equals(ru_pay_tips)){//为0表示关闭提示语，默认为""或者有值时显示
                if(TextUtils.isEmpty(ru_pay_tips)){//为空时取本地值
                    ru_pay_tips = ResourceUtil.getString(MySdkApi.getMact(),"myths_rupay_tips");
                }
                ToastUtils.Toast(ru_pay_tips);
            }
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
        //设备上安装了rustore商店才调用checkOwnedPurchases，因为此方法始终需要用户的 VK ID 授权，避免频繁弹窗授权
        if(isRuStoreInstalled){
            //通过isResumeCheck防止支付成功后重复请求查询漏单
            if(isResumeCheck){
                checkOwnedPurchases();
            }else{
                isResumeCheck = true;
            }
        }
    }
    public static void onNewIntent(Activity activity, Intent intent) {
        if(billingClient != null)billingClient.onNewIntent(intent);
    }

    //登录之前上报时长，每5秒上报
    private static void startCusReport(long flag,Timer t){
        PhoneTool.submitSDKEvent("A"+flag,"当前时长："+(System.currentTimeMillis()-flag)+"ms");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if(MySdkApi.getLoginCallBack() == null){
                    startCusReport(flag,t);
                }else{
                    t.cancel();
                }
            }
        },5000);

    }
}
