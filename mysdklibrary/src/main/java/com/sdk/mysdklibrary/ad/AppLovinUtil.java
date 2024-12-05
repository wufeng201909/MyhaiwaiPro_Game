package com.sdk.mysdklibrary.ad;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.sdk.mysdklibrary.Tools.AdjustUtil;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.interfaces.InterstitialAdListener;
import com.sdk.mysdklibrary.interfaces.RewardAdListener;

import java.util.concurrent.TimeUnit;

public class AppLovinUtil extends AdBaseUtil{

    private MaxRewardedAd mtpReward;
    private MaxInterstitialAd mtpInterstitial;
    private int retryAttempt_Reward;
    private int retryAttempt_Interstitialeward;

    private static AppLovinUtil appLovinUtil;
    public static AppLovinUtil getInstance(){
        if(appLovinUtil == null){
            appLovinUtil = new AppLovinUtil();
        }
        return appLovinUtil;
    }

    public void initSDK(Context con){
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance(con).setMediationProvider("max");
        AppLovinSdk.initializeSdk(con, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
                MLog.a("AppLovinSdk-onSdkInitialized");
            }
        } );
    }

    //激励广告
    public void rewardAdLoad(Activity act, String adId, RewardAdListener lis){
        if(mtpReward == null){
            mtpReward = MaxRewardedAd.getInstance(adId,act);
            mtpReward.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                    lis.onAdReward();
                }

//                @Override
//                public void onRewardedVideoStarted(MaxAd maxAd) {
//                    lis.onAdVideoStart();
//                }
//
//                @Override
//                public void onRewardedVideoCompleted(MaxAd maxAd) {
//                    lis.onAdVideoEnd();
//                }

                @Override
                public void onAdLoaded(MaxAd maxAd) {
                    // Reset retry attempt
                    retryAttempt_Reward = 0;
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }

                @Override
                public void onAdDisplayed(MaxAd maxAd) {
                    //成功展示广告上报
                    String ecpm = maxAd.getRevenue()*1000+"";
                    System.out.println("getAdUnitId:"+maxAd.getAdUnitId());
                    System.out.println("getAdReviewCreativeId:"+maxAd.getAdReviewCreativeId());
                    System.out.println("getDspId:"+maxAd.getDspId());
                    System.out.println("getNetworkName:"+maxAd.getNetworkName());
                    System.out.println("getPlacement:"+maxAd.getPlacement());
                    System.out.println("getCreativeId:"+maxAd.getCreativeId());
                    System.out.println("getDspName:"+maxAd.getDspName());
                    System.out.println("getNetworkPlacement:"+maxAd.getNetworkPlacement());
                    System.out.println("getRevenuePrecision:"+maxAd.getRevenuePrecision());
                    System.out.println("getRevenue:"+maxAd.getRevenue());
                    AdInfo adInfo = new AdInfo(maxAd.getAdUnitId(),AD_TYPE_REWARD,
                            maxAd.getNetworkName(),"","",ecpm,maxAd.getPlacement(),maxAd.getDspId());
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,ecpm,formAdInfo(adInfo));
                    lis.onAdImpression();
                }

                @Override
                public void onAdHidden(MaxAd maxAd) {
                    lis.onAdClosed();
                    MLog.a("Reward-onAdHidden");
                    req_loadRewardAd(adId);
                }

                @Override
                public void onAdClicked(MaxAd maxAd) {
                    lis.onAdClicked();
                }

                @Override
                public void onAdLoadFailed(String s, MaxError error) {
                    lis.onAdFailed();
                    String error1 =String.format("code: %d, message: %s",error.getCode(), error.getMessage());
                    MLog.a("AdsReward Failed : " + error1);

                    // Interstitial ad failed to load
                    // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                    retryAttempt_Reward++;
                    long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt_Reward ) ) );

                    new Handler(act.getMainLooper()).postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            req_loadRewardAd(adId);
                        }
                    }, delayMillis);
                }

                @Override
                public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                    lis.onAdVideoError();
                    MLog.a("Reward-onAdDisplayFailed");
                    req_loadRewardAd(adId);
                }
            });
        }
        req_loadRewardAd(adId);
    }
    //请求加载激励广告并上报
    private void req_loadRewardAd(String adId){
        mtpReward.loadAd();
        //请求广告上报
        adReport(adId,AD_TYPE_REWARD,EVENT_AD_REQUEST, null);
    }
    public boolean rewardAdIsReady(){
        return mtpReward!=null && mtpReward.isReady();
    }
    public void entryRewardAdScenario(String sceneId) {

    }
    public void rewardAdShow(Activity act, String sceneId){
        if(mtpReward!=null)mtpReward.showAd(sceneId);
    }

    //插屏广告
    public void interstitialAdLoad(Activity act, String adId, InterstitialAdListener lis){
        if(mtpInterstitial == null){
            mtpInterstitial = new MaxInterstitialAd(adId, act);
            mtpInterstitial.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd maxAd) {
                    retryAttempt_Interstitialeward = 0;
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }

                @Override
                public void onAdDisplayed(MaxAd maxAd) {
                    //成功展示广告上报
                    String ecpm = maxAd.getRevenue()*1000+"";
                    AdInfo adInfo = new AdInfo(maxAd.getAdUnitId(),AD_TYPE_INTER,
                            maxAd.getNetworkName(),"","",ecpm,maxAd.getPlacement(),maxAd.getDspId());
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,ecpm,formAdInfo(adInfo));
                    lis.onAdImpression();
                }

                @Override
                public void onAdHidden(MaxAd maxAd) {
                    lis.onAdClosed();
                    MLog.a("Interstitial-onAdHidden");
                    req_loadInterAd(adId);
                }

                @Override
                public void onAdClicked(MaxAd maxAd) {
                    lis.onAdClicked();
                }

                @Override
                public void onAdLoadFailed(String s, MaxError error) {
                    lis.onAdFailed();
                    String error1 =String.format("code: %d, message: %s",error.getCode(), error.getMessage());
                    MLog.a("AdsInterstitial Failed : " + error1);

                    // Interstitial ad failed to load
                    // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)
                    retryAttempt_Interstitialeward++;
                    long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt_Interstitialeward ) ) );

                    new Handler(act.getMainLooper()).postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            req_loadInterAd(adId);
                        }
                    }, delayMillis);
                }

                @Override
                public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                    lis.onAdVideoError();
                    MLog.a("Interstitial-onAdDisplayFailed");
                    req_loadInterAd(adId);
                }
            });
        }
        req_loadInterAd(adId);
    }
    //请求加载激励广告并上报
    private void req_loadInterAd(String adId){
        mtpInterstitial.loadAd();
        //请求广告上报
        adReport(adId,AD_TYPE_INTER,EVENT_AD_REQUEST, null);
    }
    public boolean interstitialAdIsReady(){
        return mtpInterstitial!=null && mtpInterstitial.isReady();
    }
    public void entryInterAdScenario(String sceneId) {

    }
    public void interstitialAdShow(Activity act, String sceneId){
        if(mtpInterstitial!=null)mtpInterstitial.showAd(sceneId);
    }

}
