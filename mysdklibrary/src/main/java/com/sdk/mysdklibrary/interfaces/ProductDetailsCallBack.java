package com.sdk.mysdklibrary.interfaces;

import com.android.billingclient.api.ProductDetails;

import java.util.List;

public interface ProductDetailsCallBack {
    void callBack(List<ProductDetails.OneTimePurchaseOfferDetails> details);
    void error(String msg);
}
