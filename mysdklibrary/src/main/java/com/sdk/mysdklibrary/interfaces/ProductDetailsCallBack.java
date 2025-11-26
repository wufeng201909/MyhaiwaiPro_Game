package com.sdk.mysdklibrary.interfaces;

import com.sdk.mysdklibrary.localbeans.MyProductDetails;

import java.util.List;

public interface ProductDetailsCallBack {
    void callBack(List<MyProductDetails> details);
    void error(String msg);
}
