package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.text.TextUtils;

import com.samsung.android.sdk.iap.lib.helper.HelperDefine;
import com.samsung.android.sdk.iap.lib.helper.IapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnConsumePurchasedItemsListener;
import com.samsung.android.sdk.iap.lib.listener.OnGetOwnedListListener;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ConsumeVo;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.OwnedProductVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;

import java.util.ArrayList;

public class SamsungSdk {

    private static boolean isResumeCheck = true;
    private static IapHelper iapHelper;

    public static void initSDK(Activity act){
        iapHelper = IapHelper.getInstance(act);
        iapHelper.setOperationMode(HelperDefine.OperationMode.OPERATION_MODE_PRODUCTION);
    }
    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        isResumeCheck = false;
        MyGamesImpl.getSharedPreferences().edit().putString("samsung_conf_url", paynotifyurl).apply();
        checkOwnedPurchases();
        System.out.println("start-pay-proId:"+extra1);
        iapHelper.startPayment(extra1, orderId, new OnPaymentListener() {
            @Override
            public void onPayment(ErrorVo _errorVo, PurchaseVo _purchaseVO) {
                if(_errorVo !=null) {
                    int code = _errorVo.getErrorCode();
                    System.out.println("payCode--->"+code);
                    if (code == IapHelper.IAP_ERROR_NONE) {
                        if (_purchaseVO != null) {
                            if(_purchaseVO.getIsConsumable()){
                                String orderId = _purchaseVO.getPassThroughParam();
                                String proId = _purchaseVO.getItemId();
                                String purchaseId = _purchaseVO.getPurchaseId();
                                handlePurchase(orderId,proId,purchaseId);
                            }
                        }
                    }else{
                        MySdkApi.getMpaycallBack().payFail("code:"+code+";msg:"+_errorVo.getErrorString());
                    }
                }else{
                    MySdkApi.getMpaycallBack().payFail("pay-failed");
                }
            }
        });
    }

    //查询漏单
    private static void checkOwnedPurchases() {
        iapHelper.getOwnedList(IapHelper.PRODUCT_TYPE_ITEM, new OnGetOwnedListListener() {
            @Override
            public void onGetOwnedProducts(ErrorVo _errorVo, ArrayList<OwnedProductVo> _ownedList) {
                if(_errorVo!=null){
                    if(_errorVo.getErrorCode()==IapHelper.IAP_ERROR_NONE){
                        if(_ownedList !=null){
                            for(OwnedProductVo item : _ownedList){
                                if(item.getIsConsumable()){
                                    String orderId = item.getPassThroughParam();
                                    String proId = item.getItemId();
                                    String purchaseId = item.getPurchaseId();
                                    handlePurchase(orderId,proId,purchaseId);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //请求服务端进行支付验证
    private static void handlePurchase(String orderId,String proId,String purchaseId) {
        String url = MyGamesImpl.getSharedPreferences().getString("samsung_conf_url", "");
        url = TextUtils.isEmpty(url)? Configs.othersdkextdata1:url;
        HttpUtils.consumePurchase(url,orderId,proId,purchaseId, new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if (issuc) {
                    MyGamesImpl.getInstance().ADJSubmit(4,orderId, msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFinish();
                    consumeOwnedPurchase(purchaseId);
                } else {
                    PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"xm:"+ msg);
                    System.out.println("consumePurchaseSDK--"+issuc+"--" + msg);
                    if(MySdkApi.getMpaycallBack()!=null)
                        MySdkApi.getMpaycallBack().payFail(msg);
                }
            }
        });
    }

    //消耗
    public static void consumeOwnedPurchase(String purchaseId){
        iapHelper.consumePurchasedItems(purchaseId, new OnConsumePurchasedItemsListener() {
            @Override
            public void onConsumePurchasedItems(ErrorVo _errorVo, ArrayList<ConsumeVo> _consumeList) {
                if(_errorVo !=null){
                    if(_errorVo.getErrorCode()==IapHelper.IAP_ERROR_NONE){
                        if(_consumeList !=null){
                            for(ConsumeVo item : _consumeList){
                                System.out.println("consumePurchased--success");
                            }
                        }
                    }else{
                        System.out.println("consumePurchased--failed");
                    }
                }
            }
        });
    }

    private static long lastClickTime;

    // 限制3秒，防止重复请求
    private static boolean isValidHits() {
        if (System.currentTimeMillis() - lastClickTime > 3000) {
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public static void onResume(Activity act) {
        //通过isResumeCheck防止支付成功后重复请求查询漏单
        if(isResumeCheck){
            if(isValidHits()) checkOwnedPurchases();
        }else{
            isResumeCheck = true;
        }
    }
}
