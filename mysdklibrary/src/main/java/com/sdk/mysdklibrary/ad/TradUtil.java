package com.sdk.mysdklibrary.ad;

import android.app.Activity;
import android.content.Context;

import com.sdk.mysdklibrary.Tools.AdjustUtil;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;

import java.util.HashMap;

public class TradUtil extends AdBaseUtil{
    private final HashMap<String,TPReward> map_tpReward = new HashMap<String,TPReward>();
    private final HashMap<String,TPInterstitial> map_tpInterstitial = new HashMap<String,TPInterstitial>();
    private static TradUtil tradUtil;
    private TPReward mtpReward;
    private TPInterstitial mtpInterstitial;
    private boolean isExecuteInit = false;

    public static TradUtil getInstance(){
        if(tradUtil == null){
            tradUtil = new TradUtil();
        }
        return tradUtil;
    }
    public void initSDK(Context con){
        if(isExecuteInit) return;//防止重复初始化
        isExecuteInit = true;
        String trad_appId = ResourceUtil.getString(con, "tradPlus_sdk_appId");
        TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
            @Override
            public void onInitSuccess() {
                // 初始化成功，建议在该回调后 发起广告请求
                System.out.println("TradPlusSdk--onInitSuccess");
            }
        });
        TradPlusSdk.initSdk(con, trad_appId);
    }
    //激励广告
    public void rewardAdLoad(Activity act, String adId, com.sdk.mysdklibrary.interfaces.RewardAdListener lis){
        if(map_tpReward.get(adId) == null){
            TPReward tpReward = new TPReward(act,adId);
            map_tpReward.put(adId,tpReward);
            tpReward.setAdListener(new RewardAdListener() {
                @Override // 广告加载完成 首个广告源加载成功时回调 一次加载流程只会回调一次
                public void onAdLoaded(TPAdInfo tpAdInfo) {
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }

                @Override // 广告被点击
                public void onAdClicked(TPAdInfo tpAdInfo) {
                    MLog.a("onAdClicked:"+tpAdInfo.toString());
                    lis.onAdClicked();
                }

                @Override // 广告成功展示在页面上
                public void onAdImpression(TPAdInfo tpAdInfo) {
                    MLog.a("onAdImpression:"+tpAdInfo.toString());
                    //成功展示广告上报
                    AdInfo adInfo = new AdInfo(tpAdInfo.tpAdUnitId,tpAdInfo.networkType,
                            tpAdInfo.adSourceName,tpAdInfo.adNetworkId,tpAdInfo.adSourceId,
                            tpAdInfo.ecpm,tpAdInfo.sceneId,tpAdInfo.impressionId);
                    adReport(adId,AD_TYPE_REWARD,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,tpAdInfo.ecpm,formAdInfo(adInfo));
                    lis.onAdImpression();
                }

                @Override // 广告加载失败
                public void onAdFailed(TPAdError error) {
                    lis.onAdFailed();
                }

                @Override // 广告被关闭
                public void onAdClosed(TPAdInfo tpAdInfo) {
                    lis.onAdClosed();
                }

                @Override // 激励视频奖励回调
                public void onAdReward(TPAdInfo tpAdInfo) {
                    MLog.a("onAdReward:"+tpAdInfo.toString());
                    lis.onAdReward();
                }

                @Override // 视频播放开始
                public void onAdVideoStart(TPAdInfo tpAdInfo) {
                    lis.onAdVideoStart();
                }

                @Override //视频播放结束
                public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                    lis.onAdVideoEnd();
                }

                @Override //视频播放失败（部分广告源支持）
                public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {
                    lis.onAdVideoError();
                }
            });
            tpReward.setAllAdLoadListener(new LoadAdEveryLayerListener() {
                @Override
                public void onAdAllLoaded(boolean b) {

                }

                @Override
                public void oneLayerLoadFailed(TPAdError adError, TPAdInfo adInfo) {
                    MLog.a("oneLayerLoadFailed: 错误码为" + adError.getErrorCode() + "，错误信息为 " + adError.getErrorMsg());
                }

                @Override
                public void oneLayerLoaded(TPAdInfo tpAdInfo) {

                }

                @Override
                public void onAdStartLoad(String s) {

                }

                @Override
                public void oneLayerLoadStart(TPAdInfo tpAdInfo) {

                }

                @Override
                public void onBiddingStart(TPAdInfo tpAdInfo) {

                }

                @Override
                public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {

                }

                @Override
                public void onAdIsLoading(String s) {

                }
            });
        }
        mtpReward = map_tpReward.get(adId);
        mtpReward.loadAd();
        //请求广告上报
        adReport(adId,AD_TYPE_REWARD,EVENT_AD_REQUEST, null);
    }
    public boolean rewardAdIsReady(){
        return mtpReward != null && mtpReward.isReady();
    }
    public void entryRewardAdScenario(String sceneId) {
        if(mtpReward!=null)mtpReward.entryAdScenario(sceneId);
    }
    public void rewardAdShow(Activity act, String sceneId){
        if(mtpReward!=null)mtpReward.showAd(act,sceneId);
    }

    //插屏广告
    public void interstitialAdLoad(Activity act, String adId, com.sdk.mysdklibrary.interfaces.InterstitialAdListener lis){
        if(map_tpInterstitial.get(adId) == null){
            TPInterstitial tpInterstitial = new TPInterstitial(act,adId);
            map_tpInterstitial.put(adId,tpInterstitial);
            tpInterstitial.setAdListener(new InterstitialAdListener() {

                @Override //广告加载完成 首个广告源加载成功时回调 一次加载流程只会回调一次
                public void onAdLoaded(TPAdInfo tpAdInfo) {
                    //成功加载广告上报
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_LOADED, null);
                    lis.onAdLoaded();
                }

                @Override // 广告被点击
                public void onAdClicked(TPAdInfo tpAdInfo) {
                    MLog.a("onAdClicked:"+tpAdInfo.toString());
                    lis.onAdClicked();
                }

                @Override // 广告成功展示在页面上
                public void onAdImpression(TPAdInfo tpAdInfo) {
                    MLog.a("onAdImpression:"+tpAdInfo.toString());
                    //成功展示广告上报
                    AdInfo adInfo = new AdInfo(tpAdInfo.tpAdUnitId,tpAdInfo.networkType,
                            tpAdInfo.adSourceName,tpAdInfo.adNetworkId,tpAdInfo.adSourceId,
                            tpAdInfo.ecpm,tpAdInfo.sceneId,tpAdInfo.impressionId);
                    adReport(adId,AD_TYPE_INTER,EVENT_AD_IMPRESSION,adInfo);
                    //adjust上报
                    AdjustUtil.getInstance().ADJSubmit(3,tpAdInfo.ecpm,formAdInfo(adInfo));
                    lis.onAdImpression();
                }

                @Override // 广告加载失败
                public void onAdFailed(TPAdError error) {lis.onAdFailed();}

                @Override // 广告被关闭
                public void onAdClosed(TPAdInfo tpAdInfo) {lis.onAdClosed();}

                @Override // 视频播放开始（部分广告源支持）
                public void onAdVideoStart(TPAdInfo tpAdInfo) {lis.onAdVideoStart();}

                @Override //视频播放结束（部分广告源支持）
                public void onAdVideoEnd(TPAdInfo tpAdInfo) {lis.onAdVideoEnd();}

                @Override //视频播放失败（部分广告源支持）
                public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError error) {lis.onAdVideoError();}
            });
        }
        mtpInterstitial = map_tpInterstitial.get(adId);
        mtpInterstitial.loadAd();
        //请求广告上报
        adReport(adId,AD_TYPE_INTER,EVENT_AD_REQUEST, null);
    }
    //是否有可用广告
    public boolean interstitialAdIsReady(){
        return mtpInterstitial != null && mtpInterstitial.isReady();
    }
    public void entryInterAdScenario(String sceneId) {
        if(mtpInterstitial!=null)mtpInterstitial.entryAdScenario(sceneId);
    }
    //展示
    public void interstitialAdShow(Activity act, String sceneId){
        if(mtpInterstitial!=null)mtpInterstitial.showAd(act,sceneId);
    }

//    /**
//     * 广告上报
//     * @param adId  TradPlus后台创建的广告位ID
//     * @param adType    广告类型
//     * @param eventName 事件名
//     * @param tpAdInfo  广告回调信息（只在事件-展示成功回调时上传，其他情形传null）
//     */
//    private void adReport(String adId, String adType, String eventName, TPAdInfo tpAdInfo){
//        JSONObject ob = new JSONObject();
//        try {
//            if(tpAdInfo == null){
//                ob.put("tpAdUnitId",adId);
//                ob.put("networkType",adType);
//            }else{
//                ob.put("tpAdUnitId",tpAdInfo.tpAdUnitId);
//                ob.put("networkType",tpAdInfo.networkType);
//                ob.put("adSourceName",tpAdInfo.adSourceName);
//                ob.put("adNetworkId",tpAdInfo.adNetworkId);
//                ob.put("adSourceId",tpAdInfo.adSourceId);
//                ob.put("ecpm",tpAdInfo.ecpm);
//                ob.put("sceneId",tpAdInfo.sceneId);
//                ob.put("impressionId",tpAdInfo.impressionId);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        HttpUtils.tpAdReport(eventName,ob);
//    }
//
//    private String formAdInfo(TPAdInfo tpAdInfo){
//        JSONObject ob = new JSONObject();
//        try {
//            ob.put("tpAdUnitId",tpAdInfo.tpAdUnitId);
//            ob.put("networkType",tpAdInfo.networkType);
//            ob.put("adSourceName",tpAdInfo.adSourceName);
//            ob.put("adNetworkId",tpAdInfo.adNetworkId);
//            ob.put("adSourceId",tpAdInfo.adSourceId);
//            ob.put("ecpm",tpAdInfo.ecpm);
//            ob.put("sceneId",tpAdInfo.sceneId);
//            ob.put("impressionId",tpAdInfo.impressionId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return ob.toString();
//    }
}
