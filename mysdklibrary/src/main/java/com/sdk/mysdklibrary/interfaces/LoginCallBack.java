package com.sdk.mysdklibrary.interfaces;

public interface LoginCallBack {
	void loginSuccess(String uid, String token,String acctype,String fbid);
	void loginFail(String msg);
}
