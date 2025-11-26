package com.sdk.mysdklibrary.Tools;

import android.app.Activity;

import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.hang.XfGameBall;
import com.sdk.mysdklibrary.hang.XfListener;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;

import java.util.Timer;
import java.util.TimerTask;

public class XfUtils {

    private static Class<?> clazz;
    private static XfUtils xfUtils;
    private static String autoUid = "",autoToken = "",autoAcctype = "",autoFbid = "";
    public static XfUtils getInstance(){
        try {
            clazz = Class.forName("com.sdk.mysdklibrary.hang.XfGameBall");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(xfUtils == null)xfUtils = new XfUtils();
        return xfUtils;
    }

    public void openXf(Activity act,String url){
        //初始化完之后自动调登录，测试用
        if("1".equals(Configs.getItem("isAutoLogin"))){
            MySdkApi.autoLogin(act, new LoginCallBack() {
                @Override
                public void loginSuccess(String uid, String token, String acctype, String fbid) {
                    setAutoUid(uid);
                    setAutoToken(token);
                    setAutoAcctype(acctype);
                    setAutoFbid(fbid);
                }

                @Override
                public void loginFail(String msg) {

                }

                @Override
                public void LogoutSuccess() {

                }
            });
        }
        if(clazz ==null) return;
        XfGameBall.initAndReg(act, url, new XfListener() {
            @Override
            public void clicked() {
                System.out.println("xf-clicked");
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                open(act);
            }
        },3000);
    }

    private void open(Activity act){
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XfGameBall.openBall(act);
            }
        });
    }
    public void dismiss(){
        if(clazz ==null) return;
        XfGameBall.dismiss();
    }

    public static String getAutoUid() {
        return autoUid;
    }

    public static void setAutoUid(String autoUid) {
        XfUtils.autoUid = autoUid;
    }

    public static String getAutoToken() {
        return autoToken;
    }

    public static void setAutoToken(String autoToken) {
        XfUtils.autoToken = autoToken;
    }

    public static String getAutoAcctype() {
        return autoAcctype;
    }

    public static void setAutoAcctype(String autoAcctype) {
        XfUtils.autoAcctype = autoAcctype;
    }

    public static String getAutoFbid() {
        return autoFbid;
    }

    public static void setAutoFbid(String autoFbid) {
        XfUtils.autoFbid = autoFbid;
    }
}
