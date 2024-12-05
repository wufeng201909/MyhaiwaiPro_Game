package com.msbdmy.goludo;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PayCallBack;
import com.sdk.mysdklibrary.localbeans.OrderInfo;

public class MainActivity extends Activity implements View.OnClickListener {

    private boolean mIsSuccess = false;
    private String cpid = "100079";
    private String gameid = "100221";
    private String gameKey = "56fhd5848sasuh54";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySdkApi.setDebug(true);
        MySdkApi.initSDK(this,cpid,gameid,gameKey, new InitCallBack() {
            @Override
            public void initSuccess(boolean isSuccess,String msg) {
                mIsSuccess = isSuccess;
            }
        });

        Button Login=findViewById(R.id.honorlogin);
        Button pay=findViewById(R.id.honorpay);



        Login.setOnClickListener(this);
        pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.honorlogin){
            MySdkApi.chooseLogin(this, new LoginCallBack() {
                @Override
                public void loginSuccess(String uid, String token, String acctype, String fbid) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,uid+"-"+token,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void loginFail(String s) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"loginFail"+s,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 6);

       }else if(id==R.id.honorpay){
            OrderInfo orderinfo = new OrderInfo();
            orderinfo.setAmount("7.02");
            orderinfo.setFeepoint("com.msbdmy.goludo_j1_0.990");
            orderinfo.setProductname("650 Diamonds");
            orderinfo.setTransactionId(System.currentTimeMillis()+"");
            orderinfo.setPayurl("");
            orderinfo.setExtraInfo("pay");

            MySdkApi.startPay( this, orderinfo, new PayCallBack() {
                @Override
                public void payFinish() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();


                        }
                    });

                }

                @Override
                public void payFail(final String msg) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MySdkApi.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onResume() {
        super.onResume();
        MySdkApi.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MySdkApi.onDestory();
    }

    @Override
    public void onBackPressed() {
        MySdkApi.onDestory();
        this.finish();

    }
}