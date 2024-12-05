package com.myludo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msbdmy.goludo.R;
import com.sdk.mysdklibrary.activity.TestWebViewClient;

import androidx.annotation.Nullable;

public class testpayActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (d.getHeight() * 0.6);   //高度设置为屏幕的0.7
//        p.width = (int) (d.getWidth() * 0.7);    //宽度设置为屏幕的0.7
//        getWindow().setAttributes(p);     //设置生效


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int hei=displayMetrics.heightPixels;
        int wid=displayMetrics.widthPixels;
        //根布局

        LinearLayout layout=new LinearLayout(this);
//        layout.setLayoutParams(new LinearLayout.LayoutParams((int)(wid*0.8),(int)(hei*0.6)));

        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
//        layout.setBackgroundColor(Color.TRANSPARENT);
//        layout.getBackground().setAlpha(100);
//         layout.setAlpha(0);
        LinearLayout linearLayout=new LinearLayout(this);




        linearLayout.setPadding(20,20,20,20);
        linearLayout.setGravity(1);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackground(getDrawable(R.drawable.wxpay));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams((int)(wid*0.8),(int)(hei*0.6)));


        ImageView imageView=new ImageView(this);
        imageView.setImageResource(R.drawable.cancle);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testpayActivity.this.finish();
            }
        });

        linearLayout.setGravity(Gravity.RIGHT);
        linearLayout.addView(imageView);



        WebView webView=new WebView(this);
        TestWebViewClient  testWebViewClient=new TestWebViewClient(this,webView,null, "orderid");
        webView.loadUrl("http://www.baidu.com");
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(testWebViewClient);
        //设置自适应屏幕，两者合用
        webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
        webView.getSettings().setLoadWithOverviewMode(true);


        linearLayout.addView(webView);

        layout.addView(linearLayout);

        setContentView(layout);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      System.out.println(event);
        return super.onTouchEvent(event);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println(keyCode);
        System.out.println(event);
        return super.onKeyDown(keyCode, event);
    }
}
