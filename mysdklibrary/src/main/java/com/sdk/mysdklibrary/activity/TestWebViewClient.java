package com.sdk.mysdklibrary.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebViewClient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.MLog;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/** * create by huanting on 2018/7/20 下午6:03 */
public class TestWebViewClient extends WebViewClient {
    private static final String TAG = "TestWebViewClient";
    private Activity mContext;
    private List<String> HTTP_SCHEMES = Arrays.asList("http", "https");
    WebView mWebView;
    ImageView web_load;
    String orderId;

    boolean loadingFinished = true;
    boolean redirect = false;

    public TestWebViewClient(Activity context, WebView webView, ImageView load, String orderid) {
        this.mContext = context;
        this.mWebView = webView;
        this.web_load = load;
        this.orderId = orderid;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        loadingFinished = false;
        if(web_load!=null && web_load.getVisibility() != View.GONE){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    MySdkApi.getMact().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(web_load!=null)web_load.setVisibility(View.GONE);
                        }
                    });
                }
            },5000);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!redirect) {
            loadingFinished = true;
            //HIDE LOADING IT HAS FINISHED
            if(this.web_load!=null) this.web_load.setVisibility(View.GONE);
        } else {
            redirect = false;
        }

        System.out.println("onPageFinished----"+url);
        if(url.contains("szmsbdmy")&&(url.indexOf("szmsbdmy")<20)){
            this.mContext.finish();
            MyGamesImpl.getInstance().getSdkact().finish();
            HttpUtils.purchasePayerMax(orderId);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        MLog.a(TAG, "shouldOverrideUrlLoading url1=" + url);
        if (!loadingFinished) {
            redirect = true;
        }
        loadingFinished = false;

        if(shouldOverrideUrlLoadingInner(view, url)) {
            MLog.a(TAG,"555555");

            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request != null && request.getUrl() != null ? request.getUrl().toString() : "";
        MLog.a(TAG, "shouldOverrideUrlLoading url=" + (request != null ? request.getUrl().toString() : ""));
        if(shouldOverrideUrlLoadingInner(view, url)) {
            return true;
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        System.out.println("-----------onReceivedSslError-------------");
        handler.cancel();
    }

    /**
     * Parse the url and open it by system function.
     *   case 1: deal "intent://xxxx" url.
     *   case 2: deal custom scheme. url
     * @param view: WebView
     * @param url
     * @return
     */
    private boolean shouldOverrideUrlLoadingInner(WebView view, String url) {
        if(!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if(uri != null) {
                if ("intent".equals(uri.getScheme())) {
                    try {
                        Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                        if(intent != null) {
                            PackageManager pm = mContext.getPackageManager();
                            ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if(info != null) {
                                mContext.startActivity(Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME));
                                return true;
                            }
                            else {
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                if (!TextUtils.isEmpty(fallbackUrl)) {
                                    if(fallbackUrl.startsWith("market://"))
                                        startAppMarketWithUrl(mContext, fallbackUrl, false);
                                    else
                                        view.loadUrl(fallbackUrl);
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                if (!HTTP_SCHEMES.contains(uri.getScheme())) {
                    startUrl(mContext, url, true);
                    return true;
                }
            }
        }
        return false;
    }
    public static void startUrl(Context context, String url, boolean isNewTask) {
        if(context != null && !TextUtils.isEmpty(url)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if(isNewTask) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }
    public static boolean hasActivity(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> appList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : appList) {
            if (info.activityInfo.packageName.equals(packageName))
                return true;
        }
        return false;
    }
    public static void startAppMarketWithUrl(Context context, String url, boolean forceUseGoogle) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (forceUseGoogle || hasActivity(context, intent, "com.android.vending"))
                intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                startUrl(context, url, true);
            } catch (Exception e1) {}
        }
    }
}