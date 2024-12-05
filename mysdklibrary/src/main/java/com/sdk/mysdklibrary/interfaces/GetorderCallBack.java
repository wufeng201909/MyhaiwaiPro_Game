package com.sdk.mysdklibrary.interfaces;

import java.util.HashMap;

public interface GetorderCallBack {
	void callback(String orderid,String feepoint,String payconfirmurl,
				  String wallet_authData,String wallet_payData,String wallet_authTo,String wallet_payTo,String wallet_nonce,
				  HashMap<String,String> param);
}
