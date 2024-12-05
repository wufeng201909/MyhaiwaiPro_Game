package com.sdk.mysdklibrary.ad;

public class AdInfo {

    private String adUnitId = "";//广告位ID
    private String networkType = "";//获取对应的广告类型。"interstitial"插屏广告、"interstitial-video"激励视频
    private String adSourceName = "";//三方广告网络名称。例如，谷歌广告返回“Admob”
    private String adNetworkId = "";//三方广告网络对应的编号，用于区分不同广告网络
    private String adSourceId = "";//三方广告位ID
    private String ecpm = "";//Ecpm美金
    private String sceneId = "";//广告场景ID
    private String impressionId = "";//标识每次广告展示的唯一标识

    public AdInfo(String adUnitId,String networkType,String adSourceName,String adNetworkId,
                  String adSourceId,String ecpm,String sceneId,String impressionId){
        this.adUnitId = adUnitId;
        this.networkType = networkType;
        this.adSourceName = adSourceName;
        this.adNetworkId = adNetworkId;
        this.adSourceId = adSourceId;
        this.ecpm = ecpm;
        this.sceneId = sceneId;
        this.impressionId = impressionId;
    }

    public String getAdUnitId() {
        return adUnitId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public String getAdSourceName() {
        return adSourceName;
    }

    public String getAdNetworkId() {
        return adNetworkId;
    }

    public String getAdSourceId() {
        return adSourceId;
    }

    public String getEcpm() {
        return ecpm;
    }

    public String getSceneId() {
        return sceneId;
    }

    public String getImpressionId() {
        return impressionId;
    }
}
