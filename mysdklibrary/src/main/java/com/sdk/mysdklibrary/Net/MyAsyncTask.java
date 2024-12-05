package com.sdk.mysdklibrary.Net;

import android.os.AsyncTask;

import com.sdk.mysdklibrary.Tools.MLog;

public class MyAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        sendData(params[0],params[1]);
        return "";
    }

    private void sendData(String url, String param){
        String result = HttpUtils.postMethod(url, param, "utf-8");
        MLog.a(result);
    }
}
