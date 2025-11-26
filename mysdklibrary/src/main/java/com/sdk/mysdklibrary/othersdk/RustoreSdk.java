//package com.sdk.mysdklibrary.othersdk;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.text.TextUtils;
//
//import com.sdk.mysdklibrary.MyApplication;
//import com.sdk.mysdklibrary.MyGamesImpl;
//import com.sdk.mysdklibrary.MySdkApi;
//import com.sdk.mysdklibrary.Net.HttpUtils;
//import com.sdk.mysdklibrary.Tools.Configs;
//import com.sdk.mysdklibrary.Tools.MLog;
//import com.sdk.mysdklibrary.Tools.PhoneTool;
//import com.sdk.mysdklibrary.Tools.ResourceUtil;
//import com.sdk.mysdklibrary.Tools.ToastUtils;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import ru.rustore.sdk.core.util.RuStoreUtils;
//import ru.rustore.sdk.pay.PurchaseInteractor;
//import ru.rustore.sdk.pay.RuStorePayClient;
//import ru.rustore.sdk.pay.UserInteractor;
//import ru.rustore.sdk.pay.model.AppUserId;
//import ru.rustore.sdk.pay.model.DeveloperPayload;
//import ru.rustore.sdk.pay.model.OrderId;
//import ru.rustore.sdk.pay.model.PreferredPurchaseType;
//import ru.rustore.sdk.pay.model.ProductId;
//import ru.rustore.sdk.pay.model.ProductPurchaseParams;
//import ru.rustore.sdk.pay.model.ProductPurchaseResult;
//import ru.rustore.sdk.pay.model.Quantity;
//
//public class RustoreSdk {
//    private static SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences("user_info", 0);
//    private static boolean isRuStoreInstalled = false;
//    public static void initSDK(Activity activity) {
//        try{
//            if(activity.getIntent() != null)
//                RuStorePayClient.Companion.getInstance().getIntentInteractor().proceedIntent(activity.getIntent());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        isRuStoreInstalled =  RuStoreUtils.INSTANCE.isRuStoreInstalled(activity);
//
//        //开启定时上报（测试）
//        long flag = System.currentTimeMillis();
//        Timer t = new Timer();
//        startCusReport(flag,t);
//    }
//
//    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
//        MLog.a("paySDK--start");
//        sharedPreferences.edit().putString("rustore_conf_url", paynotifyurl).apply();
//        if(!isRuStoreInstalled){
//            ToastUtils.Toast("The RuStore app is not installed");
//            MySdkApi.getMpaycallBack().payFail("purchaseProduct:Fail:RuStore is not Installed");
//            return;
//        }
//        UserInteractor userInteractor = RuStorePayClient.Companion.getInstance().getUserInteractor();
//        userInteractor.getUserAuthorizationStatus()
//                .addOnSuccessListener(status -> {
//                    switch (status) {
//                        case AUTHORIZED:
//                            // Logic for when the user is authorized in RuStore
//                            System.out.println("AuthorizationStatus:AUTHORIZED");
//                            break;
//                        case UNAUTHORIZED:
//                            // Logic for when the user is NOT authorized in RuStore
//                            System.out.println("AuthorizationStatus:UNAUTHORIZED");
//                            break;
//                    }
//                })
//                .addOnFailureListener(throwable -> {
//                    // Handle error
//                    System.out.println("Authorization--error:"+throwable.getMessage());
//                });
//
////        PurchaseInteractor purchaseInteractor = RuStorePayClient.Companion.getInstance().getPurchaseInteractor();
////        purchaseInteractor.getPurchaseAvailability()
////                .addOnSuccessListener(result -> {
////                    if (result instanceof PurchaseAvailabilityResult.Available) {
////                        // Handling payment availability result
////
////                    } else if (result instanceof PurchaseAvailabilityResult.Unavailable) {
////                        // Handling payment unavailability result
////                        MySdkApi.getMpaycallBack().payFail("getPurchaseAvailability:Unavailable");
////                    }
////                })
////                .addOnFailureListener(throwable -> {
////                    System.out.println("getPurchaseAvailability--onFailure:"+throwable.getMessage());
////                    MySdkApi.getMpaycallBack().payFail("getPurchaseAvailability:Fail:"+throwable.getMessage());
////                });
//        pay(orderId,extra1);
//    }
//
//    private static void pay(String orderId,String proId){
//        String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
//        ProductPurchaseParams params = new ProductPurchaseParams(new ProductId(proId), new Quantity(1), new OrderId(orderId), new DeveloperPayload(orderId), new AppUserId(uid), null);
//        PurchaseInteractor purchaseInteractor = RuStorePayClient.Companion.getInstance().getPurchaseInteractor();
//        purchaseInteractor.purchase(params, PreferredPurchaseType.ONE_STEP)
//                .addOnSuccessListener(result -> {
//                    // Successful payment result handling logic
//                    handlePaymentResult(result);
//                })
//                .addOnFailureListener(throwable -> {
//                    throwable.printStackTrace();
//                    System.out.println("purchaseProduct--onFailure:"+throwable.getMessage());
//                    MySdkApi.getMpaycallBack().payFail("purchaseProduct:Fail:"+throwable.getMessage());
//                });
//
//    }
//    private static void handlePaymentResult(ProductPurchaseResult result) {
//        String purchaseToken = "";
//        switch (result.getProductType()) {
//            case CONSUMABLE_PRODUCT:
//            case NON_CONSUMABLE_PRODUCT:
//                purchaseToken = result.getInvoiceId().getValue();
//                break;
//            case SUBSCRIPTION:
//                purchaseToken = result.getPurchaseId().getValue();
//                break;
//        }
//        String ru_pay_tips = Configs.getItem(Configs.ru_pay_tips);
//        if(!"0".equals(ru_pay_tips)){//为0表示关闭提示语，默认为""或者有值时显示
//            if(TextUtils.isEmpty(ru_pay_tips)){//为空时取本地值
//                ru_pay_tips = ResourceUtil.getString(MySdkApi.getMact(),"myths_rupay_tips");
//            }
//            ToastUtils.Toast(ru_pay_tips);
//        }
//        String orderId = result.getOrderId().getValue();
//        String proId = result.getProductId().getValue();
//        handlePurchase(orderId,proId,purchaseToken);
//    }
//
//    //请求服务端进行支付验证
//    private static void handlePurchase(String orderId,String proId,String token) {
//        String url = sharedPreferences.getString("rustore_conf_url", "");
//
//        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
//        HttpUtils.consumePurchase(url, orderId, proId, token, (issuc, msg) -> {
//            if (issuc) {
//                MyGamesImpl.getInstance().ADJSubmit(4, orderId, msg);
//                if (MySdkApi.getMpaycallBack() != null)
//                    MySdkApi.getMpaycallBack().payFinish();
//            } else {
//                System.out.println("consumePurchase--"+issuc+"--" + msg);
//                if(MySdkApi.getMpaycallBack()!=null)
//                    MySdkApi.getMpaycallBack().payFail(msg);
//            }
//        });
//    }
//
//    public static void onResume(Activity act) {
//
//    }
//    public static void onNewIntent(Activity activity, Intent intent) {
//        RuStorePayClient.Companion.getInstance().getIntentInteractor().proceedIntent(intent);
//    }
//
//    //登录之前上报时长，每5秒上报
//    private static void startCusReport(long flag,Timer t){
//        PhoneTool.submitSDKEvent("A"+flag,"当前时长："+(System.currentTimeMillis()-flag)+"ms");
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(MySdkApi.getLoginCallBack() == null){
//                    startCusReport(flag,t);
//                }else{
//                    t.cancel();
//                }
//            }
//        },5000);
//
//    }
//}
