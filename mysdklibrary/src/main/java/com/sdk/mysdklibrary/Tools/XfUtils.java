package com.sdk.mysdklibrary.Tools;

import android.app.Activity;

import com.sdk.mysdklibrary.hang.XfGameBall;
import com.sdk.mysdklibrary.hang.XfListener;

import java.util.Timer;
import java.util.TimerTask;

public class XfUtils {

    private static Class<?> clazz;
    private static XfUtils xfUtils;
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
}
