package com.sdk.mysdklibrary.ad;

import android.app.Activity;
import android.content.Context;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.sdk.mysdklibrary.Tools.AdjustUtil;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.interfaces.InterstitialAdListener;
import com.sdk.mysdklibrary.interfaces.RewardAdListener;

public class TopOnUtil extends AdBaseUtil{

    private ATRewardVideoAd mRewardVideoAd;
    private ATInterstitial mInterstitialAd;
    private String rewardAdId = "";
    private String interstitialAdId = "";
    private static TopOnUtil topOnUtil;
    public static TopOnUtil getInstance(){
        if(topOnUtil == null){
            topOnUtil = new TopOnUtil();
        }
        return topOnUtil;
    }
    public void initSDK(Context con){
        String topOn_appId = ResourceUtil.getString(con, "topOn_sdk_appId");
        String topOn_appKey = ResourceUtil.getString(con, "topOn_sdk_appKey");

//        ATSDK.setNetworkLogDebug(true);//SDK日志功能，集成测试阶段建议开启，上线前必须关闭
//        ATSDK.integrationChecking(con.getApplicationContext());//检查广告平台的集成状态，提交审核时需注释此API
        MLog.a("TopOn SDK version: " + ATSDK.getSDKVersionName());//SDK版本
        ATSDK.init(con.getApplicationContext(), topOn_appId, topOn_appKey);//初始化SDK
    }

    //激励广告
    public void rewardAdLoad(Activity act, String adId, RewardAdListener lis){
        rewardAdId = adId;
        if (mRewardVideoAd == null) {
            mRewardVideoAd = new ATRewardVideoAd(act, adId);
            mRewardVideoAd.setAdListener(new ATRewardVideoListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }
                @Override
                public void onRewardedVideoAdFailed(AdError adError) {
                    //注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                    //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                    lis.onAdFailed();
                }
                @Override
                public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                    //ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                    //请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info

                    //建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                    lis.onAdVideoStart();
                    mRewardVideoAd.load();
                    MLog.a("onAdImpression:"+atAdInfo.getAdNetworkType()+",ecpm:"+atAdInfo.getEcpm());
                    //成功展示广告上报
                    AdInfo adInfo = new AdInfo(atAdInfo.getNetworkFirmId()+"",atAdInfo.getTopOnAdFormat(),
                            atAdInfo.getChannel(),atAdInfo.getNetworkFirmId()+"",atAdInfo.getAdsourceId(),
                            atAdInfo.getEcpm()+"",atAdInfo.getScenarioId(),atAdInfo.getShowId());
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,atAdInfo.getEcpm()+"",formAdInfo(adInfo));
                    lis.onAdImpression();
                }
                @Override
                public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
                    lis.onAdVideoEnd();
                }
                @Override
                public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                    //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                    lis.onAdVideoError();
                }
                @Override
                public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
                    lis.onAdClosed();
                }
                @Override
                public void onReward(ATAdInfo atAdInfo) {
                    //建议在此回调中下发奖励，一般在onRewardedVideoAdClosed之前回调
                    MLog.a("onAdReward:"+atAdInfo.getAdNetworkType()+",ecpm:"+atAdInfo.getEcpm());
                    lis.onAdReward();
                }
                @Override
                public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {
                    lis.onAdClicked();
                }
            });
        }
        mRewardVideoAd.load();
        //请求广告上报
        adReport(adId,AD_TYPE_REWARD,EVENT_AD_REQUEST, null);
    }
    public boolean rewardAdIsReady(){
        return mRewardVideoAd != null && mRewardVideoAd.isAdReady();
    }
    public void entryRewardAdScenario(String sceneId) {
        if(mRewardVideoAd!=null)ATRewardVideoAd.entryAdScenario(rewardAdId,sceneId);
    }
    public void rewardAdShow(Activity act, String sceneId){
        if(mRewardVideoAd!=null)mRewardVideoAd.show(act,sceneId);
    }

    //插屏广告
    public void interstitialAdLoad(Activity act, String adId, InterstitialAdListener lis){
        interstitialAdId = adId;
        if (mInterstitialAd == null) {
            mInterstitialAd = new ATInterstitial(act, adId);
            mInterstitialAd.setAdListener(new ATInterstitialListener() {
                @Override
                public void onInterstitialAdLoaded() {
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }

                @Override
                public void onInterstitialAdLoadFail(AdError adError) {
                    //注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                    //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                    lis.onAdFailed();
                }

                @Override
                public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
                    lis.onAdClicked();
                }

                @Override
                public void onInterstitialAdShow(ATAdInfo atAdInfo) {
                    //ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                    //请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info
                    //建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                    mInterstitialAd.load();
                    MLog.a("onAdImpression:"+atAdInfo.getAdNetworkType()+",ecpm:"+atAdInfo.getEcpm());
                    //成功展示广告上报
                    AdInfo adInfo = new AdInfo(atAdInfo.getNetworkFirmId()+"",atAdInfo.getTopOnAdFormat(),
                            atAdInfo.getChannel(),atAdInfo.getNetworkFirmId()+"",atAdInfo.getAdsourceId(),
                            atAdInfo.getEcpm()+"",atAdInfo.getScenarioId(),atAdInfo.getShowId());
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,atAdInfo.getEcpm()+"",formAdInfo(adInfo));
                    lis.onAdImpression();
                }

                @Override
                public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                    lis.onAdClosed();
                }

                @Override
                public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {
                    lis.onAdVideoStart();
                }

                @Override
                public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {
                    lis.onAdVideoEnd();
                }

                @Override
                public void onInterstitialAdVideoError(AdError adError) {
                    //AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                    lis.onAdVideoError();
                }
            });
        }
        mInterstitialAd.load();
        //请求广告上报
        adReport(adId,AD_TYPE_INTER,EVENT_AD_REQUEST, null);
    }
    //是否有可用广告
    public boolean interstitialAdIsReady(){
        return mInterstitialAd != null && mInterstitialAd.isAdReady();
    }
    public void entryInterAdScenario(String sceneId) {
        if(mInterstitialAd!=null)ATInterstitial.entryAdScenario(interstitialAdId,sceneId);
    }
    //展示
    public void interstitialAdShow(Activity act, String sceneId){
        if(mInterstitialAd!=null)mInterstitialAd.show(act,sceneId);
    }
}
