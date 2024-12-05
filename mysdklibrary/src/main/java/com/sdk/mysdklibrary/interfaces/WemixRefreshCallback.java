package com.sdk.mysdklibrary.interfaces;

public interface WemixRefreshCallback {
    void onSuccess(String address);
    void onError(String msg);
}
