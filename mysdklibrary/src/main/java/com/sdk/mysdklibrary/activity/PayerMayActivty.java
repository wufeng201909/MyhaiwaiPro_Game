package com.sdk.mysdklibrary.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;

import androidx.annotation.Nullable;

public class PayerMayActivty extends Activity {
    private ImageView imageView_cancle,webview_load;
    private WebView webView_payerMax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int lay_id = ResourceUtil.getLayoutId(this,"myths_payermax");
        setContentView(lay_id);

        //全面屏设置
        PhoneTool.hideSystemBars(getWindow());
        PhoneTool.useSpecialScreen(getWindow());

        int imageView_cancle_id = ResourceUtil.getId(this,"cancel_payermax");
        int webView_payerMax_id = ResourceUtil.getId(this,"webview_payermax");
        int webview_load_id = ResourceUtil.getId(this,"webview_load");
        imageView_cancle=findViewById(imageView_cancle_id);
        webView_payerMax=findViewById(webView_payerMax_id);
        webview_load=findViewById(webview_load_id);

        ObjectAnimator animator=ObjectAnimator.ofFloat(webview_load,"rotation",0F,360F);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();

        Intent dataIntent=getIntent();
        String orderid=dataIntent.getStringExtra("orderid");
        String feepoint=dataIntent.getStringExtra("feepoint");
        String payconfirmurl=dataIntent.getStringExtra("url");
        String url=payconfirmurl.replace("\\","");


        imageView_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayerMayActivty.this.finish();
                MySdkApi.getMpaycallBack().payFail("pay_cancle4");
                MyGamesImpl.getInstance().getSdkact().finish();
            }
        });
//
//        WebSettings webSettings = webView_payerMax.getSettings();
//
//        webSettings.setJavaScriptEnabled(true);// 是否开启JS支持
//        //webSettings.setPluginsEnabled(true);// 是否开启插件支持
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 是否允许JS打开新窗口
//
//        webSettings.setUseWideViewPort(true);// 缩放至屏幕大小
//        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕大小
//        webSettings.setSupportZoom(true);// 是否支持缩放
//        webSettings.setBuiltInZoomControls(true);// 是否支持缩放变焦，前提是支持缩放
//        webSettings.setDisplayZoomControls(false); //是否隐藏缩放控件
//
//
//        webSettings.setAllowFileAccess(true);// 是否允许访问文件
//        webSettings.setDomStorageEnabled(true);// 是否节点缓存
//        webSettings.setDatabaseEnabled(true);// 是否数据缓存
//        webSettings.setAppCacheEnabled(true);// 是否应用缓存
////        webSettings.setAppCachePath(uri);// 设置缓存路径
//
//        webSettings.setLoadsImagesAutomatically(false);// 是否自动加载图片
//        webSettings.setDefaultTextEncodingName("UTF-8");// 设置编码格式
//        webSettings.setNeedInitialFocus(true);// 是否需要获取焦点
//        webSettings.setGeolocationEnabled(false);// 设置开启定位功能
//        webSettings.setBlockNetworkLoads(false);// 是否从网络获取资源
//
//        //设置自适应屏幕，两者合用
//        webView_payerMax.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
//        webView_payerMax.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webView_payerMax.loadUrl(url);



        TestWebViewClient  testWebViewClient=new TestWebViewClient(this,webView_payerMax,webview_load,orderid);
        webView_payerMax.setWebViewClient(testWebViewClient);

        WebSettings webSettings = webView_payerMax.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON); //enable plugin. Ex: flash. deprecated on API 18
        //whether the zoom controls display on screen.
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        //disable the webview font size changes according the phone font size.
        webSettings.setTextZoom(100);
        webSettings.setSaveFormData(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView_payerMax, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        try {
            webView_payerMax.removeJavascriptInterface("searchBoxJavaBridge_");
            webView_payerMax.removeJavascriptInterface("accessibility");
            webView_payerMax.removeJavascriptInterface("accessibilityTraversal");
        } catch (Exception e) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView_payerMax.enableSlowWholeDocumentDraw();
        }
        MLog.a("payerMax --url------>"+url);

        webView_payerMax.loadUrl(url);
//        MLog.a("payerMax --shouldOverrideUrlLoading------>"+testWebViewClient.shouldOverrideUrlLoading(webView_payerMax,url));


//        webView.loadUrl("https://cashier-uat.shareitpay.in/index.html#/cashier/home?merchantId=SP32329220&orderId=1b6dcffb4319f41baf978495cc79c506&token=OsTkZLeI7HkdQFd9caszAkNTB%2BGTPYWAgZwLK5PhJ4ejakn%2FxFaNxCpVeIyZR%2BQ1zOrglfLko11VfXFz2RvFr%2B%2Bi9a4Bjayibsr2QsDIm9XD4f2LdOkDnSB9V4bQDPHS&tradeNo=TM001000202205160001363888200000&identifyId=1446979&language=&usePayResultType=1&frontCallBackUrl=http%3A%2F%2F47.242.233.94%3A8083%2Fpay%2Fasdkpay%2Fpayermaxcallback.phpm");



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            MySdkApi.getMpaycallBack().payFail("pay_cancle4");
            MyGamesImpl.getInstance().getSdkact().finish();
        }
        return super.onKeyDown(keyCode, event);

    }
}
