package demo;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.msbdmy.goludo.R;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PhoneCodeCallBack;
import com.sdk.mysdklibrary.interfaces.ShareCallBack;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

//    private String cpid = "100079";
//    private String gameid = "100225";
//    private String gkid = "944a9a495c38f0b3";//asdk_pfdmw2_001

    private String cpid = "100079";
    private String gameid = "100122";
    private String gkid = "12fhd5748sasuh47";//asdk_pfdmw2_001
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
        Button tv = (Button)findViewById(R.id.login);
        Button tv_auto = (Button)findViewById(R.id.guestlogin);
        Button fb_auto = (Button)findViewById(R.id.facebooklogin);
        Button auto = (Button)findViewById(R.id.autologin);
        Button userdata = (Button)findViewById(R.id.userdata);
        Button share = (Button)findViewById(R.id.share);
        Button pay = (Button)findViewById(R.id.pay);
        Button bindfb = (Button)findViewById(R.id.bindfacebook);
        Button bindgg = (Button)findViewById(R.id.bindgoogle);

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

                    @Override
                    public void LogoutSuccess() {

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

                    @Override
                    public void LogoutSuccess() {

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

                    @Override
                    public void LogoutSuccess() {

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

                    @Override
                    public void LogoutSuccess() {

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

                    @Override
                    public void LogoutSuccess() {

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

                    @Override
                    public void LogoutSuccess() {

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
                orderinfo.setFeepoint("product_0.99_pfdmw2");
                orderinfo.setProductname("6元钻石");
                orderinfo.setTransactionId(System.currentTimeMillis()+"");
                orderinfo.setPayurl("");
                JSONObject extra = new JSONObject();
                try {
                    extra.put("confID","");
                    extra.put("type","");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                orderinfo.setExtraInfo(extra);

                MySdkApi.getPhoneCode(MainActivity.this, "86","13631284454", new PhoneCodeCallBack() {
                    @Override
                    public void getCodeSuccess(boolean isSuccess, String msg) {

                        MLog.a("code---"+msg);

                    }


                });

//                MySdkApi.startPay(MainActivity.this, orderinfo, new PayCallBack() {
//                    @Override
//                    public void payFinish() {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//                                Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();
//
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void payFail(final String msg) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
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
