package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.vgamepop.android.asdk.base.PublicPurchaseListener;
import com.vgamepop.android.asdk.base.ValueGetListener;
import com.vgamepop.android.asdk.core.ASDKManager;
import com.vgamepop.android.asdk.core.SDKInitConfig;
import com.vgamepop.android.asdk.core.log.LoggedCallBack;
import com.vgamepop.android.asdk.core.net.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PureSdk {

    public static void applicationOnCreate(Application app) {
        String appId = ResourceUtil.getString(app, "pure_appId");
        SDKInitConfig config = new SDKInitConfig.Builder(appId)
                .setDebug(false)
                .setLogCallback(new LoggedCallBack() {
                    @Override
                    public void onCallBack(int level, String tag, String msg) {
                        if (msg != null) {
                            Log.println(level, tag, msg);
                        }
                    }
                }).build();
        ASDKManager.init(app, config);
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl, String extra1, String extra2) {
        String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
        ASDKManager.setUserid(uid);
        List<String> list = new ArrayList<>();
        list.add(extra1);
        ASDKManager.getProducts(list, new ValueGetListener<List<Product>>() {
            @Override
            public void onSucceed(List<Product> products) {
                pay(activity,orderId,extra1);
            }

            @Override
            public void onFailed(@NonNull Throwable throwable) {
                System.out.println("purepay-onFailure-->"+throwable.getMessage());
                MySdkApi.getMpaycallBack().payFail(throwable.getMessage());
            }
        });
    }

    private static void pay(Activity activity, String orderId,String proId){
        Map<String, String> map = new HashMap<>();
        map.put("orderId",orderId);
        ASDKManager.purchaseProduct(activity, orderId, proId, map, new PublicPurchaseListener() {
            @Override
            public void onSucceed(@NonNull String s) {
                MySdkApi.getMpaycallBack().payFinish();
            }

            @Override
            public void onFailed(@NonNull Throwable throwable) {
                System.out.println("purepay-onFailure-->"+throwable.getMessage());
                MySdkApi.getMpaycallBack().payFail(throwable.getMessage());
            }
        });
    }
}
