package com.sdk.mysdklibrary.interfaces;

public interface RewardAdListener {
    void onAdLoaded();
    void onAdClicked();
    void onAdImpression();
    void onAdFailed();
    void onAdClosed();
    void onAdReward();
    void onAdVideoError();
    void onAdVideoStart();
    void onAdVideoEnd();
}
