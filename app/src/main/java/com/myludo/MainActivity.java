package com.myludo;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.msbdmy.goludo.FireBaseUtil;
import com.msbdmy.goludo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.AESSecurity;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.interfaces.BindPhoneCallBack;
import com.sdk.mysdklibrary.interfaces.ChangePasswordCallBack;
import com.sdk.mysdklibrary.interfaces.CheckCodeCallBack;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PayCallBack;
import com.sdk.mysdklibrary.interfaces.PhoneCodeCallBack;
import com.sdk.mysdklibrary.interfaces.SetFBTokenCallBack;
import com.sdk.mysdklibrary.interfaces.ShareCallBack;
import com.sdk.mysdklibrary.interfaces.UnBindSDKCallBack;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

public class MainActivity extends Activity {

//    private String cpid = "100079";
//    private String gameid = "100225";
//    private String gkid = "944a9a495c38f0b3";//asdk_pfdmw2_001

    private String cpid = "100079";
    private String gameid = "100122";
    private String gkid = "12fhd5748sasuh47";//asdk_pfdmw2_001


//    private String cpid = "100079";
//    private String gameid = "100221";
//    private String gkid = "56fhd5848sasuh54";//asdk_goplay_001
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MySdkApi.setDebug(true);
        MySdkApi.initSDK(this,cpid,gameid,gkid, new InitCallBack() {
            @Override
            public void initSuccess(boolean isSuccess,String msg) {
                System.out.println("initSDK--"+isSuccess+",msg:"+msg);
            }
        });


        //推送

        FireBaseUtil.pushTopic(this ,"global");

        String token=FireBaseUtil.getFBToken(this);
        System.out.println("fb-push-token----0="+token);

        Button tv = (Button)findViewById(R.id.login);
        Button tv_auto = (Button)findViewById(R.id.guestlogin);
        Button fb_auto = (Button)findViewById(R.id.facebooklogin);
        Button auto = (Button)findViewById(R.id.autologin);
        Button userdata = (Button)findViewById(R.id.userdata);
        Button share = (Button)findViewById(R.id.share);
        Button pay = (Button)findViewById(R.id.pay);
        Button bindfb = (Button)findViewById(R.id.bindfacebook);
        Button bindgg = (Button)findViewById(R.id.bindgoogle);

        EditText editText=findViewById(R.id.ed_phone);
        Button unbind=findViewById(R.id.unbind);

        EditText editText2=findViewById(R.id.ed_phone2);
        Button bindphoe=findViewById(R.id.bindPhone);

        EditText editText3=findViewById(R.id.ed_phone3);
        Button changePhone=findViewById(R.id.bindPhone);

        EditText phoneNum=findViewById(R.id.ed_phoneN);
        Button getCode=findViewById(R.id.getcode);

        EditText phoneyzm=findViewById(R.id.ed_phone4);
        EditText phonephone1=findViewById(R.id.ed_phone5);
        Button checkcode=findViewById(R.id.enter);

        //手机登录
        EditText phoneyzm2=findViewById(R.id.ed_phone6);
        EditText phonephone2=findViewById(R.id.ed_phone3);
        Button loginphone=findViewById(R.id.loginphone);

        //修改密码
        EditText phoneyzm3=findViewById(R.id.ed_phone7);
        EditText newpassword=findViewById(R.id.ed_phone8);
        Button changepassword=findViewById(R.id.changepassword);

        //忘记密码
        EditText phoneyzm4=findViewById(R.id.ed_phone9);
        EditText newpassword1=findViewById(R.id.ed_phone10);
        Button orgetPassword=findViewById(R.id.orgetPassword);

        //firebase
        Button firebaseB=findViewById(R.id.firebase);
        Button firebaseB2=findViewById(R.id.firebase2);



        Button shareFB1=findViewById(R.id.sharebt1);
        Button shareFB2=findViewById(R.id.sharebt2);

        Button welcomeFB=findViewById(R.id.welcome);

        Button fbmapBt=findViewById(R.id.fbmap);


        HashMap map=new HashMap();
        map.put("ONE","1111");
        map.put("TWO","2222");
        map.put("tree","3333");
        map.put("four","4444");
        map.put("five","5555");
        map.put("six","6666");


        Bundle bundletest=new Bundle();
        JSONObject jsonObject=new JSONObject();
        fbmapBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                String token=FireBaseUtil.getFBToken(MainActivity.this);
//                System.out.println("fb-push-token----1="+token);

//                System.out.println("fb-push-token----1="+AESSecurity.encrypt("123456789","abc"));

//                String data = "Hello World"; // 待加密的原文
//                byte[] key = KeyGenerators.secureRandom(32).generateKey();
//                byte[] ciphertext = AESSecurity.encryptGCM(data.getBytes(), key);
//                System.out.println("GCM 模式加密结果（Base64）：" + Base64Utils.encodeToString(ciphertext));
//                byte[] plaintext = AESUtils.decryptGCM(ciphertext, key);
//                System.out.println("解密结果：" + new String(plaintext));


                System.out.println("fb-push-token----1="+AESSecurity.decrypt("Kryq6hUWArUPukI4wqj6DgRpujFH9wp6Cm/8lgA4TDsSvbeN0mYz/an4","abc"));
                com.sdk.mysdklibrary.Tools.FireBaseUtil.getFBToken(MainActivity.this, new SetFBTokenCallBack() {
                    @Override
                    public void setSuccess(String token) {
                        System.out.println(" com.sdk.mysdklibrary.Tools.FireBaseUtil.getFBToken----setSuccess="+token);
                        MySdkApi.setfirebaseid(MainActivity.this,token);
                    }

                    @Override
                    public void setFail(String msg) {
                        System.out.println(" com.sdk.mysdklibrary.Tools.FireBaseUtil.getFBToken----setFail="+token);
                    }
                });

//                MySdkApi.setfirebaseid(MainActivity.this,token);

                startActivity(new Intent(MainActivity.this,testpayActivity.class));
                JSONObject js=new JSONObject();
                try {
                    js.put("test","test001");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                String data="";
//                HttpURLConnection connection = null;
//                BufferedReader reader = null;
//                try{
//
//                    URL url = new URL("http://120.79.164.117:8084/outerinterface/zteventreport.php");//新建URL
//                    connection = (HttpURLConnection)url.openConnection();//发起网络请求
//                    connection.setRequestMethod("GET");//请求方式
//                    connection.setConnectTimeout(8000);//连接最大时间
//                    connection.setReadTimeout(8000);//读取最大时间
//                    InputStream in = connection.getInputStream();
//                    reader = new BufferedReader(new InputStreamReader(in));//写入reader
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while((line = reader.readLine()) != null){
//                        response.append(line);
//                    }
//
//                    data=response.toString();
//                    System.out.println("返回结果------"+response.toString());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }finally {
//                    if(reader != null){
//                        try{
//                            reader.close();
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }
//                    }
//                    if(connection != null){
//                        connection.disconnect();
//                    }
//                }


                MySdkApi.dataReport("test",js);
//                for (Object o : map.keySet()){
//                    System.out.println("key=" + o + " value=" + map.get(o));
//                    bundletest.putString(o.toString(),map.get(o).toString());
//                    try {
//                        jsonObject.put(o.toString(),map.get(o).toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

               MySdkApi.logFireBaseEventWithMap("test",map);
//
//                System.out.println("bundle--" + bundletest.getString("ONE"));
//                System.out.println("bundle--" + bundletest.getString("TWO"));
//                System.out.println("bundle--" + bundletest.getString("tree"));
//                System.out.println("bundle--" + bundletest.getString("four"));
//                System.out.println("bundle--" + bundletest.getString("five"));
//                System.out.println("bundle--" + bundletest.getString("six"));
//
//                System.out.println("jsonObject--" +jsonObject.toString());

            }
        });




        shareFB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                MySdkApi.facebookContentShare(MainActivity.this,"https://developers.facebook.com");
            }
        });

        shareFB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.facebookContentShare(MainActivity.this, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round));
            }
        });


        welcomeFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.facebookShare(MainActivity.this, "test", "fucking the work", new ShareCallBack() {
                    @Override
                    public void isSuccess(String[] info) {
                        MLog.a("facebookShare---info="+info);
                    }

                    @Override
                    public void isFailed() {
                        MLog.a("facebookShare---isFailed=");
                    }
                });
            }
        });
        firebaseB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleEvent = new Bundle();
                bundleEvent.putLong("click_time",System.currentTimeMillis());
                bundleEvent.putString("key","value");
                MySdkApi.logFireBaseEvent("click_event",bundleEvent);


                Bundle bundleEvent1 = new Bundle();
                bundleEvent1.putLong("click_time",System.currentTimeMillis());
                bundleEvent1.putString("key","value");
                bundleEvent1.putString("test","value");
                bundleEvent1.putString("tf","value");
                MySdkApi.logFireBaseEvent("fuck",bundleEvent1);

                Bundle bundleEvent2 = new Bundle();
                bundleEvent2.putLong("click_time",System.currentTimeMillis());
                bundleEvent2.putString("key","455fa");
                bundleEvent2.putString("test","6465654");
                bundleEvent2.putString("tf","6546546");
                MySdkApi.logFireBaseEvent("hello",bundleEvent2);




            }
        });

        firebaseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle databundle=new Bundle();
                databundle.putString("uid","111111111111");
                databundle.putString("user_type","222222222222");
                databundle.putString("platform","33333333");
                databundle.putString("sys_version","44444444");
                databundle.putString("device_id","5555555555");
                databundle.putString("afid","66666666666666");
                databundle.putString("ip","77777777777");
                databundle.putString("country","8888888888");
                databundle.putString("app_version","999999999");
                databundle.putString("install_channel","10");
                databundle.putString("sys_lang","11");
                databundle.putString("app_lang","12");
                databundle.putString("system_model","13");
                databundle.putString("network","14");
                databundle.putString("operator","15");
                databundle.putString("event_time","16");
                databundle.putString("login_type","17");

                Bundle databundle1=new Bundle();
                databundle1.putString("uid","1111111111111111111");

                MySdkApi.logFireBaseEvent("friends_add",databundle1);

                MySdkApi.logFireBaseEvent("share",databundle);
                MySdkApi.logFireBaseEvent("session_start",databundle);

                MySdkApi.logFireBaseEvent("session_start",databundle);

            }
        });


        orgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yzm=phoneyzm4.getText().toString();
                String newpassword=newpassword1.getText().toString();
                MySdkApi.forgetPassword(MainActivity.this, "86", "13631284454", yzm, newpassword, new ChangePasswordCallBack() {
                    @Override
                    public void Success(String msg) {
                        MLog.a("forgetPassword---Success="+msg);
                    }

                    @Override
                    public void Fail(String msg) {
                        MLog.a("forgetPassword---Success="+msg);
                    }
                });
            }
        });


        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yzm=phoneyzm3.getText().toString();
                String newPassWord=newpassword.getText().toString();
                MySdkApi.changePassword(MainActivity.this, "86", "13631284454", "123456", newPassWord, new ChangePasswordCallBack() {
                    @Override
                    public void Success(String msg) {
                        MLog.a("changePassworld---Success="+msg);
                    }

                    @Override
                    public void Fail(String msg) {
                        MLog.a("changePassworld---Fail="+msg);
                    }
                });
            }
        });


        loginphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String yzm=phoneyzm2.getText().toString();
                String phone=phonephone2.getText().toString();
                //1--首次   2 ---非首次
                if(phone.equals("1")){

                    MySdkApi.phoneLogin(MainActivity.this,
                            "86","13631284454",yzm,"123456",true,new LoginCallBack() {
                                @Override
                                public void loginSuccess(String uid, String token, String acctype, String fbid) {
                                    MLog.a("phonelogin====loginSuccess---uid="+uid+";token="+token+";acctype="+acctype+";fbid="+fbid);

                                }

                                @Override
                                public void loginFail(String msg) {
                                    MLog.a("phonelogin===loginFail---msg"+msg);
                                }
                            });

                }else if(phone.equals("2")){
                    MySdkApi.phoneLogin(MainActivity.this, "86", "13631284454", yzm, "123456", false, new LoginCallBack() {
                        @Override
                        public void loginSuccess(String uid, String token, String acctype, String fbid) {
                            MLog.a("phonelogin非首次====loginSuccess---uid="+uid+";token="+token+";acctype="+acctype+";fbid="+fbid);

                        }

                        @Override
                        public void loginFail(String msg) {
                            MLog.a("phonelogin非首次===loginFail---msg"+msg);
                        }
                    });

                }
            }
        });

        checkcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String yzm=phoneyzm.getText().toString();
                String phone=phonephone1.getText().toString();
                MySdkApi.checkPhoneCode(MainActivity.this, "86", phone, yzm, new CheckCodeCallBack() {
                    @Override
                    public void CheckSuccess(String msg) {
                        MLog.a("CheckSuccess----"+msg);
                    }

                    @Override
                    public void CheckFail(String msg) {
                        MLog.a("CheckSuccess----"+msg);
                    }
                });
//                MySdkApi.CheckPhoneCode(Activity context, String areaCode, String phoneNum, String code, CheckCodeCallBack callBack)
            }
        });




        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=phoneNum.getText().toString();

                MySdkApi.getPhoneCode(MainActivity.this,"86", phone, new PhoneCodeCallBack() {
                    @Override
                    public void getCodeSuccess(boolean isSuccess, String msg) {
                        MLog.a("isSuccess----"+isSuccess+";msg---"+msg);
                    }
                });

            }
        });


        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=editText3.getText().toString();
                String phone=phoneNum.getText().toString();
                MySdkApi.bindPhone(MainActivity.this,2,"86", phone, code, "123456", new BindPhoneCallBack() {
                    @Override
                    public void bindSuccess(String action) {
                        MLog.a(action);
                    }

                    @Override
                    public void bindFail(String msg) {
                        MLog.a(msg);
                    }
                });


            }
        });


        bindphoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=editText2.getText().toString();
                MySdkApi.bindPhone(MainActivity.this,1, "86","13631284454", code, "123456", new BindPhoneCallBack() {
                    @Override
                    public void bindSuccess(String action) {
                        MLog.a(action);
                    }

                    @Override
                    public void bindFail(String msg) {
                        MLog.a(msg);
                    }
                });
            }
        });



        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=editText.getText().toString();
                MySdkApi.unBindSDK(MainActivity.this,"86", "13631284454", phone, 1, new UnBindSDKCallBack() {
                    @Override
                    public void unBindSuccess(boolean isSuccess, String msg) {

                    }
                });

            }
        });

        bindfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1-Facebook  2-Google  3-apple  4-twitter
                MySdkApi.bindSDK(MainActivity.this, 1, new LoginCallBack() {
                    @Override
                    public void loginSuccess(String uid, String token, String acctype, String fbid) {
                        MLog.a("bindfbloginback---uid="+uid+";token="+token+";acctype="+acctype+";fbid="+fbid);
                    }

                    @Override
                    public void loginFail(String msg) {
                        MLog.a("bindfbFail---msg="+msg);
                    }
                });
            }
        });

        bindgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.bindSDK(MainActivity.this, 2, new LoginCallBack() {
                    @Override
                    public void loginSuccess(String uid, String token, String acctype, String fbid) {
                        MLog.a("bindggloginback---uid="+uid+";token="+token+";acctype="+acctype+";fbid="+fbid);
                    }

                    @Override
                    public void loginFail(String msg) {
                        MLog.a("bindggFail---msg="+msg);
                    }
                });
            }
        });



        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.openLogin(MainActivity.this, new LoginCallBack() {
                    @Override
                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {

                        MLog.a("openLogin---uid="+uid+";token="+token+";acctype="+acctype+";fbid="+fbid);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype+"-"+fbid,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },3000);

                    }

                    @Override
                    public void loginFail(final String msg) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.chooseLogin(MainActivity.this,new LoginCallBack() {
                    @Override
                    //acctype-账号类型：0-游客；1-facebook
                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },3000);

                    }

                    @Override
                    public void loginFail(final String msg) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                },1);
            }
        });
        fb_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.chooseLogin(MainActivity.this,new LoginCallBack() {
                    @Override
                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },3000);

                    }

                    @Override
                    public void loginFail(final String msg) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                },3);
            }
        });

        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.autoLogin(MainActivity.this, new LoginCallBack() {
                    @Override
                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },3000);
                    }

                    @Override
                    public void loginFail(final String msg) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        userdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameRoleBean roleBean = new GameRoleBean();
                roleBean.setGameZoneId("1");
                roleBean.setGameZoneName("名扬");
                roleBean.setRoleId("123");
                roleBean.setRoleName("萝卜");
                roleBean.setRoleLevel(10);
                roleBean.setVipLevel(1);
                roleBean.setRoleCTime("角色的创建时间");
                //0=create role; 1=enter game; 2=role update
                MySdkApi.submitRoleData(1,roleBean);

                Toast.makeText(MainActivity.this,"已上报",Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject param_js = new JSONObject();
                        try {
                            param_js.put("phone","13631284454");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String param = param_js.toString();
                        final String result = HttpUtils.postMethod(Configs.accountserver+"gameparam=isphonebunded", param, "utf-8");
                        MLog.a(result);
                    }
                }).start();

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySdkApi.facebookShare(MainActivity.this, "share", "Come play this level with me", new ShareCallBack() {
                    @Override
                    public void isSuccess(String[] info) {
                        //邀请的fb好友的fb id集合
                    }

                    @Override
                    public void isFailed() {

                    }
                });
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderInfo orderinfo = new OrderInfo();
                orderinfo.setAmount("6.00");
                orderinfo.setFeepoint("com.msbdmy.goludo_j1_0.99");
                orderinfo.setProductname("6元钻石");
                orderinfo.setTransactionId(System.currentTimeMillis()+"");
                orderinfo.setPayurl("");

//                MySdkApi.getPhoneCode(MainActivity.this, "86","13631284454", new PhoneCodeCallBack() {
//                    @Override
//                    public void getCodeSuccess(boolean isSuccess, String msg) {
//
//                        MLog.a("code---"+msg);
//
//                    }
//
//
//                });

                MySdkApi.startPay(MainActivity.this, orderinfo, new PayCallBack() {
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
        });

        makeKeyHasn();




        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TEST:", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TEST:",  token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MySdkApi.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String token=FireBaseUtil.getFBToken(this);
        System.out.println("fb-push-token----3="+token);
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

    private void makeKeyHasn() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


}
