package com.sdk.mysdklibrary.interfaces;

public interface InterstitialAdListener {
    void onAdLoaded();
    void onAdClicked();
    void onAdImpression();
    void onAdFailed();
    void onAdClosed();
    void onAdVideoError();
    void onAdVideoStart();
    void onAdVideoEnd();
}
