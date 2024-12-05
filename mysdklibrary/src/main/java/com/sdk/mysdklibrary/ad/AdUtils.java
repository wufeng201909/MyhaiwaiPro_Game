package com.sdk.mysdklibrary.ad;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.interfaces.BannerAdListener;
import com.sdk.mysdklibrary.interfaces.InterstitialAdListener;
import com.sdk.mysdklibrary.interfaces.NativeAdListener;
import com.sdk.mysdklibrary.interfaces.RewardAdListener;
import com.sdk.mysdklibrary.interfaces.SplashAdListener;

public class AdUtils {

    private static AdUtils adUtils;
    private static AdBaseUtil adBaseUtil;
    private boolean isAdParamInit;
    private String scene_gu = "";
    public static AdUtils getInstance(){
        if(adUtils == null){
            adUtils = new AdUtils();
        }
        return adUtils;
    }
    public void initAdSDK(){
    }

    //在application中初始化-tradPlus
    public void initAdSDK(Context con){
        String trad_appId = ResourceUtil.getString(con, "tradPlus_sdk_appId");
        if(TextUtils.isEmpty(trad_appId)) return;
        isAdParamInit = true;
        try {
            TradUtil.getInstance().initSDK(con);
        }catch (Error e){
            e.printStackTrace();
        }
        String pub = FilesTool.getPublisherStringContent();
        if(pub.startsWith("asdk")){//google包
            scene_gu = "";
        }else if(pub.startsWith("migame")){
            scene_gu = "mi";
        }else if(pub.startsWith("huawei")){
            scene_gu = "hw";
        }else{
            scene_gu = pub.substring(0,pub.indexOf("sdk"));
        }
        adBaseUtil = TradUtil.getInstance();
    }

    //在OnCreate中初始化-appLovin,topOn
    public void initAdSDKOnCreate(Context con){
        String appLovin_key = ResourceUtil.getString(con, "appLovin_sdk_key");
        String topOn_appId = ResourceUtil.getString(con, "topOn_sdk_appId");
        if(!TextUtils.isEmpty(appLovin_key)){
            isAdParamInit = true;
            AppLovinUtil.getInstance().initSDK(con);
            adBaseUtil = AppLovinUtil.getInstance();
        }else if(!TextUtils.isEmpty(topOn_appId)){
            isAdParamInit = true;
            TopOnUtil.getInstance().initSDK(con);
            adBaseUtil = TopOnUtil.getInstance();
        }
    }

    //激励广告----------------------------------------------------------------------
    //创建并加载(tradPlus和appLovin只能取一个，不能共存)
    public void rewardAdLoad(Activity act,RewardAdListener lis){
        System.out.println("--rewardAdLoad--");
        if(!isAdParamInit) return;
        String rewardAdId = "";
        if(adBaseUtil instanceof TradUtil){
            rewardAdId = ResourceUtil.getString(act,"tradPlus_rewardAdId");
        }else if(adBaseUtil instanceof AppLovinUtil){
            rewardAdId = ResourceUtil.getString(act,"appLovin_rewardAdId");
        }else if(adBaseUtil instanceof TopOnUtil){
            rewardAdId = ResourceUtil.getString(act,"topOn_rewardAdId");
        }
        if(!TextUtils.isEmpty(rewardAdId))
            adBaseUtil.rewardAdLoad(act,rewardAdId,lis);
    }
    //是否有可用广告
    public boolean rewardAdIsReady(){
        System.out.println("--rewardAdIsReady--");
        if(!isAdParamInit) return false;
        boolean isReady = adBaseUtil.rewardAdIsReady();
        System.out.println("--rewardAdIsReady--"+isReady);
        return isReady;
    }
    //进入激励广告场景
    public void entryRewardAdScenario(String sceneId){
        System.out.println("--entryRewardAdScenario--");
        if(!isAdParamInit) return;
        System.out.println("--entryRewardAdScenario--"+scene_gu+sceneId);
        adBaseUtil.entryRewardAdScenario(scene_gu+sceneId);
    }
    //展示
    public void rewardAdShow(Activity act,String sceneId){
        System.out.println("--rewardAdShow--");
        if(!isAdParamInit) return;
        System.out.println("--rewardAdShow--"+scene_gu+sceneId);
        adBaseUtil.rewardAdShow(act,TextUtils.isEmpty(sceneId)?null:(scene_gu+sceneId));
    }
    //激励广告----------------------------------------------------------------------

    //插屏广告----------------------------------------------------------------------
    //创建并加载
    public void interstitialAdLoad(Activity act,InterstitialAdListener lis){
        System.out.println("--interstitialAdLoad--");
        if(!isAdParamInit) return;
        String interstitialAdId = "";
        if(adBaseUtil instanceof TradUtil){
            interstitialAdId = ResourceUtil.getString(act,"tradPlus_interstitialAdId");
        }else if(adBaseUtil instanceof AppLovinUtil){
            interstitialAdId = ResourceUtil.getString(act,"appLovin_interstitialAdId");
        }else if(adBaseUtil instanceof TopOnUtil){
            interstitialAdId = ResourceUtil.getString(act,"topOn_interstitialAdId");
        }
        if(!TextUtils.isEmpty(interstitialAdId))
            adBaseUtil.interstitialAdLoad(act,interstitialAdId,lis);
    }
    //是否有可用广告
    public boolean interstitialAdIsReady(){
        System.out.println("--interstitialAdIsReady--");
        if(!isAdParamInit) return false;
        boolean isReady = adBaseUtil.interstitialAdIsReady();
        System.out.println("--interstitialAdIsReady--"+isReady);
        return isReady;
    }
    //进入插屏广告场景
    public void entryInterAdScenario(String sceneId){
        System.out.println("--entryInterAdScenario--");
        if(!isAdParamInit) return;
        System.out.println("--entryInterAdScenario--"+scene_gu+sceneId);
        adBaseUtil.entryInterAdScenario(scene_gu+sceneId);
    }
    //展示
    public void interstitialAdShow(Activity act,String sceneId){
        System.out.println("--interstitialAdShow--");
        if(!isAdParamInit) return;
        System.out.println("--interstitialAdShow--"+scene_gu+sceneId);
        adBaseUtil.interstitialAdShow(act,TextUtils.isEmpty(sceneId)?null:(scene_gu+sceneId));
    }
    //插屏广告----------------------------------------------------------------------

    //原生广告----------------------------------------------------------------------
    //创建并加载
    public void nativeAdLoad(Activity act, NativeAdListener lis){

    }
    //是否有可用广告
    public boolean nativeAdIsReady(){
        return true;
    }
    //展示
    public void nativeAdShow(Activity act, ViewGroup adContainer){

    }
    //原生广告----------------------------------------------------------------------

    //横幅广告----------------------------------------------------------------------
    //创建并加载
    public void bannerAdLoad(Activity act, FrameLayout adContainer, BannerAdListener lis){

    }
    //是否有可用广告
//    public boolean bannerAdIsReady(){
//        return true;
//    }
//    //展示
//    public void bannerAdShow(Activity act, ViewGroup adContainer){
//
//    }
    //横幅广告----------------------------------------------------------------------

    //开屏广告----------------------------------------------------------------------
    //创建并加载
    public void splashAdLoad(Activity act, SplashAdListener lis){

    }
    //是否有可用广告
    public boolean splashAdIsReady(){
        return true;
    }
    //展示
    public void splashAdShow(Activity act, ViewGroup adContainer){

    }
    //开屏广告----------------------------------------------------------------------
}
