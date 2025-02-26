//package com.sdk.demo.myhaiwai;
//
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.msbdmy.goludo.R;
////import com.crazymaplestudio.theatertycoon.R;
//
//import com.sdk.mysdklibrary.MySdkApi;
//import com.sdk.mysdklibrary.interfaces.InitCallBack;
//import com.sdk.mysdklibrary.interfaces.LoginCallBack;
//import com.sdk.mysdklibrary.interfaces.PayCallBack;
//import com.sdk.mysdklibrary.interfaces.ShareCallBack;
//import com.sdk.mysdklibrary.localbeans.GameRoleBean;
//import com.sdk.mysdklibrary.localbeans.OrderInfo;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MainActivity extends Activity {
//
////    private String cpid = "100079";
////    private String gameid = "100225";
////    private String gkid = "944a9a495c38f0b3";//asdk_pfdmw2_001
//
//    private String cpid = "100079";
//    private String gameid = "100122";
//    private String gkid = "12fhd5748sasuh47";//asdk_pfdmw2_001
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_main);
//
//        MySdkApi.setDebug(true);
//        MySdkApi.initSDK(this,cpid,gameid,gkid, new InitCallBack() {
//            @Override
//            public void initSuccess(boolean isSuccess,String msg) {
//                System.out.println("initSDK--"+isSuccess+",msg:"+msg);
//            }
//        });
//        Button tv = (Button)findViewById(R.id.login);
//        Button tv_auto = (Button)findViewById(R.id.guestlogin);
//        Button fb_auto = (Button)findViewById(R.id.facebooklogin);
//        Button auto = (Button)findViewById(R.id.autologin);
//        Button userdata = (Button)findViewById(R.id.userdata);
//        Button share = (Button)findViewById(R.id.share);
//        Button pay = (Button)findViewById(R.id.pay);
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySdkApi.openLogin(MainActivity.this, new LoginCallBack() {
//                    @Override
//                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                MainActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        },3000);
//
//                    }
//
//                    @Override
//                    public void loginFail(final String msg) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//        });
//        tv_auto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySdkApi.chooseLogin(MainActivity.this,new LoginCallBack() {
//                    @Override
//                    //acctype-账号类型：0-游客；1-facebook
//                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                MainActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        },3000);
//
//                    }
//
//                    @Override
//                    public void loginFail(final String msg) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    }
//                },1);
//            }
//        });
//        fb_auto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySdkApi.chooseLogin(MainActivity.this,new LoginCallBack() {
//                    @Override
//                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                MainActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        },3000);
//
//                    }
//
//                    @Override
//                    public void loginFail(final String msg) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    }
//                },3);
//            }
//        });
//
//        auto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySdkApi.autoLogin(MainActivity.this, new LoginCallBack() {
//                    @Override
//                    public void loginSuccess(final String uid, final String token,String acctype, String fbid) {
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                MainActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this,uid+"-"+token+"-"+acctype,Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        },3000);
//                    }
//
//                    @Override
//                    public void loginFail(final String msg) {
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//            }
//        });
//
//        userdata.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GameRoleBean roleBean = new GameRoleBean();
//                roleBean.setGameZoneId("1");
//                roleBean.setGameZoneName("名扬");
//                roleBean.setRoleId("123");
//                roleBean.setRoleName("萝卜");
//                roleBean.setRoleLevel(10);
//                roleBean.setVipLevel(1);
//                roleBean.setRoleCTime("角色的创建时间");
//                //0=create role; 1=enter game; 2=role update
//                MySdkApi.submitRoleData(1,roleBean);
//
//                Toast.makeText(MainActivity.this,"已上报",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySdkApi.facebookShare(MainActivity.this, "share", "Come play this level with me", new ShareCallBack() {
//                    @Override
//                    public void isSuccess(String[] info) {
//                        //邀请的fb好友的fb id集合
//                    }
//
//                    @Override
//                    public void isFailed() {
//
//                    }
//                });
//            }
//        });
//
//        pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                OrderInfo orderinfo = new OrderInfo();
//                orderinfo.setAmount("6.00");
//                orderinfo.setFeepoint("product_0.99_pfdmw2");
//                orderinfo.setProductname("6元钻石");
//                orderinfo.setTransactionId(System.currentTimeMillis()+"");
//                orderinfo.setPayurl("");
//
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
//            }
//        });
//
//        makeKeyHasn();
//
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        MySdkApi.onActivityResult(requestCode, resultCode, data);
//
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        MySdkApi.onDestory();
//    }
//
//    @Override
//    public void onBackPressed() {
//        MySdkApi.onDestory();
//        this.finish();
//
//    }
//
//    private void makeKeyHasn() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//    }
//}
