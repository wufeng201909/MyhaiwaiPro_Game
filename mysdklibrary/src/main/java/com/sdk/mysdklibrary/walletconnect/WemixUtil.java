package com.sdk.mysdklibrary.walletconnect;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.interfaces.WemixRefreshCallback;
import com.sdk.mysdklibrary.interfaces.WemixSignCallback;
import com.wemadetree.wemixauth.a2a.WemixAuthManager;
import com.wemadetree.wemixauth.a2a.WemixCallback;
import com.wemadetree.wemixauth.a2a.WemixError;
import com.wemadetree.wemixauth.a2a.WemixSdk;
import com.wemadetree.wemixauth.a2a.data.AuthResult;
import com.wemadetree.wemixauth.a2a.data.AuthResultWithUserInfo;
import com.wemadetree.wemixauth.a2a.data.SignData;
import com.wemadetree.wemixauth.a2a.data.SignResult;
import com.wemadetree.wemixauth.a2a.data.WemixUser;

import java.util.List;

public class WemixUtil {
//    private String wemix_devurl = "https://dev-oauth.wemix.co/";
//    private String wemix_stageurl = "https://stg-oauth.wemixnetwork.com/";
//    private String wemix_produrl = "https://oauth.wemixnetwork.com/";
//
//    private static WemixUtil wemixUtil;
//    public static WemixUtil getInstance(){
//        if(wemixUtil == null) wemixUtil = new WemixUtil();
//        return wemixUtil;
//    }
//
//    public void init(Context context){
//        String wemix_clientId = ResourceUtil.getString(context,"wemix_clientId");
//        if(!TextUtils.isEmpty(wemix_clientId))
//            WemixSdk.init(context, wemix_clientId, wemix_stageurl,false);
//    }
//
//    public void login(Activity context){
//        WemixAuthManager.getInstance().logIn(
//                context,
//                new WemixCallback<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult data) {
//                        String accessToken = data.getAccessToken().getAccessToken();
//                        String reqId = data.getRequestId();
//                        long timestamp = System.currentTimeMillis();
//                        long expires = data.getAccessToken().getExpiresIn();//accessToken有效期（s）
//                        getUserInfo(accessToken,timestamp,expires);
//                        System.out.println("accessToken--->"+accessToken+"\n"+"req--->"+reqId);
//                        System.out.println("timestamp--->"+timestamp+"\n"+"expires--->"+expires);
//                    }
//
//                    @Override
//                    public void onError(@NonNull WemixError e) {
//                        e.printStackTrace();
//                        MySdkApi.getLoginCallBack().loginFail(e.getMessage());
//                    }
//                }
//        );
//    }
//    private void getUserInfo(String accessToken,long timestamp,long expires) {
//        WemixAuthManager.getInstance().getUserInfo(new WemixCallback<WemixUser>() {
//            @Override
//            public void onSuccess(WemixUser data) {
//                WemixUser wemixUser = data;
//                String address = wemixUser.getAddress();
//                String uid = wemixUser.getUserId();
//                String sub = wemixUser.getSub();
//                System.out.println("address--->"+address+"\n"+"uid--->"+uid+"\n"+"sub--->"+sub);
//                HttpUtils.wemixLogin_check(address, accessToken,timestamp,expires);
//            }
//
//            @Override
//            public void onError(@NonNull WemixError e) {
//                e.printStackTrace();
//                MySdkApi.getLoginCallBack().loginFail(e.getMessage());
//            }
//        });
//    }
//
//    public void RefreshToken(Context context, WemixRefreshCallback refreshCallback){
//        WemixAuthManager.getInstance().logInWithRefreshToken(
//                context,
//                new WemixCallback<AuthResultWithUserInfo>() {
//                    @Override
//                    public void onSuccess(AuthResultWithUserInfo data) {
//                        String accessToken = data.getAccessToken().getAccessToken();
//                        String address = data.getUserInfo().getAddress();
//                        long timestamp = System.currentTimeMillis();
//                        long expires = data.getAccessToken().getExpiresIn();//accessToken有效期（s）
//                        System.out.println("accessToken--->"+accessToken);
//                        System.out.println("timestamp--->"+timestamp+"\n"+"expires--->"+expires);
//                        MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaddress",address).commit();
//                        MyGamesImpl.getSharedPreferences().edit().putString("myths_wemixaccesstoken",accessToken).commit();
//                        MyGamesImpl.getSharedPreferences().edit().putLong("myths_wemixtimestamp",timestamp).commit();
//                        MyGamesImpl.getSharedPreferences().edit().putLong("myths_wemixexpires",expires).commit();
//                        refreshCallback.onSuccess(address);
//                    }
//
//                    @Override
//                    public void onError(@NonNull WemixError e) {
//                        refreshCallback.onError(e.getMessage());
//                    }
//                }
//        );
//    }
//
//    public void sign(Context context,String useraddress,String hash, WemixSignCallback signCallback){
//        List<SignData> list_approve = WemixHash.Companion.getHashData(hash);
//        WemixAuthManager.getInstance().sign(context, useraddress, list_approve, new WemixCallback<SignResult>() {
//            @Override
//            public void onSuccess(SignResult signResult) {
//                for (String sign: signResult.getSignatures()) {
//                    signCallback.onSuccess(sign);
//                }
//            }
//
//            @Override
//            public void onError(@NonNull WemixError wemixError) {
//                System.out.println("wemixError---"+wemixError.getMessage());
//                signCallback.onError(wemixError.getMessage());
//            }
//        });
//    }

}
