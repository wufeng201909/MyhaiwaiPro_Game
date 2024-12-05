package com.sdk.mysdklibrary.interfaces;

public interface BannerAdListener {
    void onAdLoaded();
    void onAdClicked();
    void onAdImpression();
    void onAdLoadFailed();
    void onAdClosed();
}
