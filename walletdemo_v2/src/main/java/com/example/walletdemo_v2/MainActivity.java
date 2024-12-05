package com.example.walletdemo_v2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.walletconnect.WcV2;

import java.util.Collections;

public class MainActivity extends Activity {
    WcV2 wcv2 = null;
    TextView tv_connectWallet,tv_sign,tv_pay;
    String proId_war = "60781af533e74066b5d82e5a6353698d";

    String proId_com = "6f829cd27b85ed97cdf6087b4fb3b0e3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_connectWallet = findViewById(ResourceUtil.getId(this,"tv_connectWallet"));
        tv_sign = findViewById(ResourceUtil.getId(this,"tv_sign"));
        tv_pay = findViewById(ResourceUtil.getId(this,"tv_pay"));
        tv_connectWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wcv2.initConnect();
            }
        });
        tv_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "123";
                wcv2.sign(msg);
            }
        });
        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = wcv2.getEthSendTransaction(wcv2.getAcc(),
                        "0x70012948c348CBF00806A3C79E3c5DAdFaAa347B",
                        "0x123",
                        "0x01");
                wcv2.pay(param);
            }
        });
        wcv2 = new WcV2(this);
        wcv2.init("Oil War","https://oilwar.io","Oil War Up up",
                Collections.singletonList("http://oilwar.io/assets/logo.b3ef6605.png"),
                proId_war);
    }

}