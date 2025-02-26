package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.activity.PayActivity;
import com.sdk.mysdklibrary.ad.AdUtils;
import com.sdk.mysdklibrary.interfaces.GetorderCallBack;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;

import java.lang.reflect.Method;
import java.util.HashMap;

public class SkipUtil {
    private static Class<?> clazz = null;
    private static VkSdk vkSdk = null;
    private static VkIdSdk vkIdSdk = null;
    private static String Publisher = null;
    private static HashMap<String, String> map = new HashMap<String, String>();
    private final static String gu_str = "com.sdk.mysdklibrary.othersdk.";
    static {
        map.put("huaweisdk", gu_str + "HwSdk");
        map.put("migamesdk", gu_str + "XmSdk");
        map.put("rustoresdk",gu_str + "RustoreSdk");
        map.put("appbazarsdk",gu_str + "AppBazarSdk");
        map.put("samsungsdk",gu_str + "SamsungSdk");
        map.put("catappultsdk",gu_str + "CatappultSdk");
        map.put("puresdk",     gu_str + "PureSdk");
        map.put("xiaoqisdk",     gu_str + "XqSdk");
    }
    public static VkSdk getVkSdk(){
        return vkSdk;
    }
    public static VkIdSdk getVkIdSdk(){
        return vkIdSdk;
    }
    private static Method getMethod(String flag, Class<?>...clas){
        if(clazz == null) return null;
        try {
            return clazz.getMethod(flag, clas);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void invoke(Method method,Object...prams){
        if(method==null){
            return;
        }
        try {
            method.invoke(null, prams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //登录
    public static void othLogin(Activity act,String type) {
        Method method = getMethod("loginSDK", Activity.class,String.class);
        invoke(method, act,type);
    }
    //中台sdk支付
    public static void mySdkPay(){
        HttpUtils.getPayList();
    }
    //第三方sdk支付下单
    public static void otherSdkPay(Activity act){
        HttpUtils.getOtherPayOrder(act,"", new GetorderCallBack() {
            @Override
            public void callback(String orderid, final String feepoint, String payconfirmurl,String ext2,String ext3,String ext4,String ext5,String ext6,
                                 HashMap<String,String> param) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String cp_fee = MyApplication.getAppContext().getOrderinfo().getFeepoint();
                        String feep = TextUtils.isEmpty(feepoint)?cp_fee:feepoint;
                        othPay(act,orderid,payconfirmurl,feep,ext2);
                    }
                });
            }
        });
    }
    //第三方sdk支付
    private static void othPay(Activity act,String order, String paynotifyurl,String extdata1,String extdata2) {
        Method method = getMethod("paySDK", Activity.class,String.class,String.class,String.class,String.class);
        invoke(method, act,order,paynotifyurl,extdata1,extdata2);
    }
    //----------------以下为application调用的方法----------------
    public static void APPAttachBaseContext(Application app, Context base) {
        Publisher = FilesTool.getPublisherStringContent().split("_")[0];
        if(clazz==null){
            String name = map.get(Publisher);
            String name_ = (name==null)?map.get(Publisher.substring(0, Publisher.length()-1)):name;
            try {
                if (name_ != null) {
                    clazz = Class.forName(name_);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Method method = getMethod("applicationAttachBaseContext", Application.class,Context.class);
        invoke(method, app,base);
    }
    public static void APPOnCreate(Application app) {
        Method method = getMethod("applicationOnCreate", Application.class);
        invoke(method, app);

        //在application初始化广告sdk
        try{
            AdUtils.getInstance().initAdSDK(app);
        }catch (Error ignored){
        }
        //vk
        String vk_isnew = ResourceUtil.getString(app, "vk_sdk_isnew");
        String vkclient = ResourceUtil.getString(app, "vk_client_secret");
        if(!TextUtils.isEmpty(vkclient)){
            if((!"true".equals(vk_isnew))) {//旧版
                vkSdk = new VkSdk();
                vkSdk.init(app);
            }else {//新版
                vkIdSdk = new VkIdSdk();
                vkIdSdk.init(app);
            }
        }
    }
    public static void APPConfigurationChanged(Application app, Configuration newConfig) {
        Method method = getMethod("applicationOnConfigurationChanged", Application.class,Configuration.class);
        invoke(method, app,newConfig);
    }
    public static void APPOnTerminate(Application app) {
        Method method = getMethod("applicationOnTerminate", Application.class);
        invoke(method, app);
    }
    //----------------以上为application调用的方法----------------
    //初始化1
    public static void othInit(Activity activity) {
        Method method = getMethod("initSDK", Activity.class);
        invoke(method, activity);
        //在OnCreate初始化广告sdk
        try{
            AdUtils.getInstance().initAdSDKOnCreate(activity);
        }catch (Error ignored){
        }
    }
    public static void othInit(Activity activity, Bundle savedInstanceState) {
        Method method = getMethod("initSDK", Activity.class,Bundle.class);
        invoke(method, activity,savedInstanceState);
    }
    public static void onSaveInstanceState(Activity activity,Bundle savedInstanceState) {
        Method method = getMethod("onSaveInstanceState", Activity.class,Bundle.class);
        invoke(method, activity,savedInstanceState);
    }
    //----------------以下为生命周期方法----------------
    public static void othNewIntent(Activity act, Intent intent) {
        Method method = getMethod("onNewIntent", Activity.class,Intent.class);
        invoke(method,act,intent);
    }
    public static void othOnResume(Activity act) {
        Method method = getMethod("onResume", Activity.class);
        invoke(method,act);
    }
    public static void othOnPuse(Activity act) {
        Method method = getMethod("onPause", Activity.class);
        invoke(method,act);
    }
    public static void othOnStop(Activity act) {
        Method method = getMethod("onStop", Activity.class);
        invoke(method,act);
    }
    public static void othDestroy(Activity act) {
        Method method = getMethod("onDestroy", Activity.class);
        invoke(method,act);

        if(vkSdk!=null)vkSdk.onDestroy();
    }
    public static void othOnStart(Activity act) {
        Method method = getMethod("onStart", Activity.class);
        invoke(method,act);
    }
    public static void othRestart(Activity act) {
        Method method = getMethod("onRestart", Activity.class);
        invoke(method,act);
    }
    public static void othActivityResult(Activity act, int requestCode,int resultCode, Intent data) {
        Method method = getMethod("onActivityResult", Activity.class,int.class,int.class,Intent.class);
        invoke(method,act,requestCode,resultCode,data);
    }
    public static void othBackPressed(Activity act) {
        Method method = getMethod("onBackPressed", Activity.class);
        invoke(method,act);
    }
    public static void othConfigurationChanged(Configuration newConfig) {
        Method method = getMethod("onConfigurationChanged", Configuration.class);
        invoke(method,newConfig);
    }
    public static void onRequestPermissionsResult(Activity activity,
                                                  int requestCode, String[] permissions, int[] grantResults) {
        Method method = getMethod("onRequestPermissionsResult", int.class,String[].class,int[].class);
        invoke(method,requestCode,permissions,grantResults);
    }
    //----------------以上为生命周期方法----------------
    //退出
    public static void othQuit(Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Method method = getMethod("exit", Activity.class);
                if(method == null){
                    exit(act);
                    return;
                }
                invoke(method,act);
            }
        });

    }
    //默认退出接口
    private static void exit(Activity act){
        String title = ResourceUtil.getString(act,"myths_exit_title");
        String content = ResourceUtil.getString(act,"myths_exit_content");
        String confirm = ResourceUtil.getString(act,"myths_paywallet_con");
        String cancel = ResourceUtil.getString(act,"myths_exit_cancel");
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton(confirm,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        // 执行游戏退出
                        act.finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }
                });
        builder.setNegativeButton(cancel,
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
    }
    //注销
    public static void othLogout(Activity act) {
        Method method = getMethod("logout", Activity.class);
        if(method == null){
            logout(act);
            return;
        }
        invoke(method,act);
    }
    //默认注销回调
    private static void logout(Activity act){
        if(MySdkApi.getLoginCallBack()!=null)
            MySdkApi.getLoginCallBack().LogoutSuccess();
    }

    public static void submitRoleData(int operator, GameRoleBean gameRoleBean) {
        Method method = getMethod("submitRoleData", int.class,GameRoleBean.class);
        invoke(method,operator,gameRoleBean);
    }
    //消耗确认
    public static void consumeOwnedPurchase(String purchaseToken){
        Method method = getMethod("consumeOwnedPurchase",String.class);
        invoke(method, purchaseToken);
    }

    public static void VKLogin(Activity act) {
        if(vkSdk!=null){
            vkSdk.login(act);
        }else if(vkIdSdk!=null){
            vkIdSdk.login(act);
        }
        else{
            MySdkApi.chooseLogin(act, MySdkApi.getLoginCallBack(),1);
        }
    }
}
