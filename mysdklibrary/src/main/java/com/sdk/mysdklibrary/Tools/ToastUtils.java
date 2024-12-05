package com.sdk.mysdklibrary.Tools;

import android.widget.Toast;

import com.sdk.mysdklibrary.MySdkApi;

public class ToastUtils {

    public static void Toast(final String msg){
        MySdkApi.getMact().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MySdkApi.getMact(), msg,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
