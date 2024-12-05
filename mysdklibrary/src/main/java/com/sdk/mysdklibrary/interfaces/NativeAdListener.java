package com.sdk.mysdklibrary.interfaces;

public interface NativeAdListener {
    void onAdLoaded();
    void onAdClicked();
    void onAdImpression();
    void onAdShowFailed();
    void onAdLoadFailed();
    void onAdClosed();
}
