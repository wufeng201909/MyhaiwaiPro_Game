package com.sdk.mysdklibrary.ad;

import android.app.Activity;
import android.content.Context;

import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.interfaces.InterstitialAdListener;
import com.sdk.mysdklibrary.interfaces.RewardAdListener;

import org.json.JSONException;
import org.json.JSONObject;

public class AdBaseUtil {
    //上报事件
    protected final static String EVENT_AD_REQUEST = "ad_request";//请求广告
    protected final static String EVENT_AD_LOADED = "ad_loaded";//成功加载广告
    protected final static String EVENT_AD_IMPRESSION = "ad_impression";//成功展示广告
    //上报广告类型
    protected final static String AD_TYPE_REWARD = "interstitial-video";//激励
    protected final static String AD_TYPE_INTER = "interstitial";//插屏

    private static AdBaseUtil adBaseUtil;
    public static AdBaseUtil getInstance(){
        if(adBaseUtil == null){
            adBaseUtil = new AdBaseUtil();
        }
        return adBaseUtil;
    }
    public void initSDK(Context con){
    }
    public void rewardAdLoad(Activity act, String adId, RewardAdListener lis){
    }
    public boolean rewardAdIsReady(){
        return false;
    }
    public void entryRewardAdScenario(String sceneId) {
    }
    public void rewardAdShow(Activity act, String sceneId){
    }

    //插屏广告
    public void interstitialAdLoad(Activity act, String adId, InterstitialAdListener lis){
    }
    public boolean interstitialAdIsReady(){
        return false;
    }
    public void entryInterAdScenario(String sceneId) {
    }
    public void interstitialAdShow(Activity act, String sceneId){
    }

    /**
     * 广告上报
     * @param adId  后台创建的广告位ID
     * @param adType    广告类型
     * @param eventName 事件名
     * @param adInfo  广告回调信息（只在事件-展示成功回调时上传，其他情形传null）
     */
    protected void adReport(String adId, String adType, String eventName, AdInfo adInfo){
        JSONObject ob = new JSONObject();
        try {
            if(adInfo == null){
                ob.put("tpAdUnitId",adId);
                ob.put("networkType",adType);
            }else{
                ob.put("tpAdUnitId",adInfo.getAdUnitId());
                ob.put("networkType",adInfo.getNetworkType());
                ob.put("adSourceName",adInfo.getAdSourceName());
                ob.put("adNetworkId",adInfo.getAdNetworkId());
                ob.put("adSourceId",adInfo.getAdSourceId());
                ob.put("ecpm",adInfo.getEcpm());
                ob.put("sceneId",adInfo.getSceneId());
                ob.put("impressionId",adInfo.getImpressionId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.tpAdReport(eventName,ob);
    }

    protected String formAdInfo(AdInfo adInfo){
        JSONObject ob = new JSONObject();
        try {
            ob.put("tpAdUnitId",adInfo.getAdUnitId());
            ob.put("networkType",adInfo.getNetworkType());
            ob.put("adSourceName",adInfo.getAdSourceName());
            ob.put("adNetworkId",adInfo.getAdNetworkId());
            ob.put("adSourceId",adInfo.getAdSourceId());
            ob.put("ecpm",adInfo.getEcpm());
            ob.put("sceneId",adInfo.getSceneId());
            ob.put("impressionId",adInfo.getImpressionId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ob.toString();
    }
}
