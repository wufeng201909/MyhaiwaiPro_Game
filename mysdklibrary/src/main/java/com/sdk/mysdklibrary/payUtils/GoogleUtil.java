package com.sdk.mysdklibrary.payUtils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;
import com.sdk.mysdklibrary.interfaces.ProductDetailsCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleUtil {

    private static int count = 0;
    private static GoogleUtil googleUtil;

    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;
    private boolean isGoogleOk;

    public static GoogleUtil getInstance(){
        if(googleUtil == null)googleUtil = new GoogleUtil();
        return googleUtil;
    }
    public boolean isGoogleOk() {
        return isGoogleOk;
    }
    public void initService(Context con){
        try{
            createPurchasesUpdatedListener();
            billingClient = BillingClient.newBuilder(con)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                    .enableAutoServiceReconnection() // Add this line to enable reconnection
                    .build();
            connectService();
        }catch (Error ignored){
        }
    }

    private void createPurchasesUpdatedListener() {
        if(purchasesUpdatedListener == null) {
            purchasesUpdatedListener = new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
                    if (billingResult == null) {
                        payFail(668, "onPurchasesUpdated--null BillingResult");
                        return;
                    }
                    int responseCode = billingResult.getResponseCode();
                    String debugMessage = billingResult.getDebugMessage();
                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        if (list == null) {
                            payFail(669, "onPurchasesUpdated--null purchase list");
                            return;
                        }
                        processPurchases(list);
                        MyGamesImpl.getInstance().getSdkact().finish();
                    } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user cancelling the purchase flow.
                        payFail(responseCode, "USER_CANCELED");
                    } else {
                        // Handle any other error codes.
                        payFail(responseCode, "onPurchasesUpdated:" + debugMessage);
                    }
                }
            };
        }
    }

    private void connectService(){
        try{
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if(billingResult==null){
                        isGoogleOk = false;
                        return;
                    }
                    isGoogleOk = true;
                    int responseCode = billingResult.getResponseCode();
                    String debugMessage = billingResult.getDebugMessage();
                    if (responseCode ==  BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        //查询漏单
                        queryPurchases();
                    }else{
                        isGoogleOk = false;
                        MLog.a("startConnection:"+debugMessage);
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    if(count>5){//重连6次后停止
                        isGoogleOk = false;
                        return;
                    }
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            connectService();
                        }
                    },2000);
                    count++;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void querySkuDetailsAndPay(Activity act, String orderid, String feepoint) {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(feepoint)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()
                                )
                        )
                        .build();
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         QueryProductDetailsResult queryProductDetailsResult) {
                        // check billingResult
                        if(billingResult==null){
                            payFail(667,"queryProductDetailsAsync--null BillingResult");
                            return;
                        }
                        int responseCode = billingResult.getResponseCode();
                        String debugMessage = billingResult.getDebugMessage();
                        if(responseCode==BillingClient.BillingResponseCode.OK){
                            List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
                            MLog.a("productDetailsList---size--->"+productDetailsList.size());
                            for (ProductDetails productDetails:productDetailsList){
                                MLog.a("productDetails---ID---->"+ productDetails.getProductId());
                                if(feepoint.equals(productDetails.getProductId())){
                                    pay(act,orderid,productDetails);
                                    return;
                                }
                            }
                            payFail(responseCode,"queryProductDetailsAsync:"+"no feepoint:"+feepoint);
                        }else{
                            payFail(responseCode,"queryProductDetailsAsync:"+debugMessage);
                        }
                    }
                }
        );
    }

    private void pay(Activity act, String orderid, ProductDetails productDetails) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String offerToken = "";
                List<ProductDetails.OneTimePurchaseOfferDetails> l = productDetails.getOneTimePurchaseOfferDetailsList();
                if (l != null) {
                    for(int i = 0; i < l.size();i++) {
                        offerToken = l.get(i).getOfferToken();
                        if(!TextUtils.isEmpty(offerToken)){
                            break;
                        }
                    }
                }
                ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                        ImmutableList.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                        .setProductDetails(productDetails)
                                        // Get the offer token:
                                        // a. For one-time products, call ProductDetails.getOneTimePurchaseOfferDetailsList()
                                        // for a list of offers that are available to the user.
                                        // b. For subscriptions, call ProductDetails.subscriptionOfferDetails()
                                        // for a list of offers that are available to the user.
                                        .setOfferToken(offerToken)
                                        .build()
                        );
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .setObfuscatedProfileId(orderid)
                        .setObfuscatedAccountId(orderid)
                        .build();
                BillingResult billingResult = billingClient.launchBillingFlow(act, billingFlowParams);
                int responseCode = billingResult.getResponseCode();
                if(responseCode==BillingClient.BillingResponseCode.OK){
                    System.out.println("launchBillingFlow--success");
                }else{
                    payFail(responseCode,"launchBillingFlow:"+billingResult.getDebugMessage());
                }
            }
        });
    }

    //处理订单列表
    private void processPurchases(List<Purchase> purchaseList){
        for (Purchase purchase : purchaseList) {
            if (purchase.getPurchaseState()==Purchase.PurchaseState.PURCHASED) {
                handlePurchase(purchase);
            }
        }
    }
    private void handlePurchase(Purchase purchase){
        if(MySdkApi.getMpaycallBack()!=null) MySdkApi.getMpaycallBack().payFinish();
        consumePurchaseSDK(purchase);
    }
    private void payFail(int responseCode,String debugMessage){
        MySdkApi.getMpaycallBack().payFail("code:"+responseCode+",msg:"+debugMessage);
        MyGamesImpl.getInstance().getSdkact().finish();
    }
    public synchronized void queryPurchases(){
        if (isGoogleOk) {
            int connectionState = billingClient.getConnectionState();
            System.out.println("ConnectionState---->" + connectionState);
            if (connectionState == BillingClient.ConnectionState.CONNECTED)
                billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                        new PurchasesResponseListener() {
                            @Override
                            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                                processPurchases(list);
                            }
                        }
                );
        }
    }
    private void consumePurchaseSDK(Purchase purchase) {
        String order = purchase.getAccountIdentifiers().getObfuscatedProfileId();
        System.out.println("profileId---->" + order);
        HttpUtils.consumePurchaseSDK(order, MySdkApi.getMact().getPackageName(), purchase.getProducts().get(0), purchase.getPurchaseToken(),
                new PayConsumeCallback() {

                    @Override
                    public void result(Boolean issuc, String msg) {
                        if (issuc) {
                            MyGamesImpl.getInstance().ADJSubmit(4,order,msg);
                            consumeAsync(purchase.getPurchaseToken());
                        } else {
                            System.out.println("consumePurchaseSDK--" + msg);
                        }
                    }
                });
    }

    private void consumeAsync(String purchaseToken) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build();
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    System.out.println("onConsumeResponse--OK");
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    public void onDestory() {
        if (billingClient != null && billingClient.isReady()) {
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            billingClient.endConnection();
        }
    }

    public void queryProductDetails(List<String> feepoints, ProductDetailsCallBack detailsCallBack){
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        for (String feepoint:feepoints) {
            QueryProductDetailsParams.Product pro = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(feepoint)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build();
            productList.add(pro);
        }

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();
        if(billingClient == null) return;
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         QueryProductDetailsResult queryProductDetailsResult) {
                        // check billingResult
                        if(billingResult==null){
                            detailsCallBack.error("queryProductDetailsAsync--null BillingResult");
                            return;
                        }
                        int responseCode = billingResult.getResponseCode();
                        String debugMessage = billingResult.getDebugMessage();
                        if(responseCode==BillingClient.BillingResponseCode.OK){
                            List<ProductDetails> productDetailsList = queryProductDetailsResult.getProductDetailsList();
                            MLog.a("productDetailsList--size-->"+productDetailsList.size());
                            List<ProductDetails.OneTimePurchaseOfferDetails> detailsList = new ArrayList<>();
                            for (ProductDetails productDetails:productDetailsList){
                                MLog.a("productDetails--ID-->"+ productDetails.getProductId());
                                ProductDetails.OneTimePurchaseOfferDetails details = productDetails.getOneTimePurchaseOfferDetails();
                                detailsList.add(details);
                            }
                            detailsCallBack.callBack(detailsList);
                        }else{
                            detailsCallBack.error("queryProductDetailsAsync:"+debugMessage);
                        }
                    }
                }
        );
    }

    public BillingClient getBillingClient(){
        return billingClient;
    }
}
