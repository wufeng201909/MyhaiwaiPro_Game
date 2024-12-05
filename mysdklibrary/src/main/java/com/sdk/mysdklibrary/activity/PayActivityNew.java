//package com.sdk.mysdklibrary.activity;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.PendingIntent;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import com.android.vending.billing.IInAppBillingService;
//import com.gaa.sdk.iap.ConsumeListener;
//import com.gaa.sdk.iap.ConsumeParams;
//import com.gaa.sdk.iap.IapResult;
//import com.gaa.sdk.iap.IapResultListener;
//import com.gaa.sdk.iap.ProductDetail;
//import com.gaa.sdk.iap.ProductDetailsListener;
//import com.gaa.sdk.iap.ProductDetailsParams;
//import com.gaa.sdk.iap.PurchaseClient;
//import com.gaa.sdk.iap.PurchaseClientStateListener;
//import com.gaa.sdk.iap.PurchaseData;
//import com.gaa.sdk.iap.PurchaseFlowParams;
//import com.gaa.sdk.iap.PurchasesListener;
//import com.gaa.sdk.iap.PurchasesUpdatedListener;
//import com.sdk.mysdklibrary.MyApplication;
//import com.sdk.mysdklibrary.MyGamesImpl;
//import com.sdk.mysdklibrary.MySdkApi;
//import com.sdk.mysdklibrary.Net.HttpUtils;
//import com.sdk.mysdklibrary.Tools.Configs;
//import com.sdk.mysdklibrary.Tools.MLog;
//import com.sdk.mysdklibrary.Tools.ResourceUtil;
//import com.sdk.mysdklibrary.activity.Adaper.MyGridViewAdapter;
//import com.sdk.mysdklibrary.interfaces.GetorderCallBack;
//import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;
//import com.sdk.mysdklibrary.localbeans.OrderInfo;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class PayActivityNew extends Activity implements View.OnClickListener, PurchasesUpdatedListener {
//    int pay_cancle_id,pay_submit_id;
//    //支付列表
//    private String paydata = "[{\"url\":\"myths_onestore\",\"pay_id\":\"29\",\"remark\":\"onestore\"}," +
//            "{\"url\":\"myths_googlepay\",\"pay_id\":\"28\",\"remark\":\"Google pay\"}]";
//    //只有googlepay
////    private String paydata = "[{\"url\":\"myths_googlepay\",\"pay_id\":\"3\",\"remark\":\"Google pay\"}]";
//    GridView gv = null;
//    JSONArray jsonarr=null;
//    int position=0;
//
//    ArrayList<String[]> list_paytype = new ArrayList<String[]>();
//
//    static IInAppBillingService mService;
//
//    ServiceConnection mServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name,
//                                       IBinder service) {
//            mService = IInAppBillingService.Stub.asInterface(service);
//            checkorder(PayActivityNew.this,mService);
//        }
//
//    };
//    public static void checkorder(Activity act,IInAppBillingService service) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Bundle ownedItems = service.getPurchases(3, act.getPackageName(), "inapp", null);
//                    int response = ownedItems.getInt("RESPONSE_CODE");
//                    if (response == 0) {
//                        ArrayList<String> ownedSkus =
//                                ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
//                        ArrayList<String> purchaseDataList =
//                                ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//                        ArrayList<String> signatureList =
//                                ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
//                        String continuationToken =
//                                ownedItems.getString("INAPP_CONTINUATION_TOKEN");
//
//                        for (int i = 0; i < purchaseDataList.size(); ++i) {
//                            String purchaseData = purchaseDataList.get(i);
//                            String signature = signatureList.get(i);
//                            System.out.println("losed-purchaseData:"+purchaseData);
//                            try {
//                                JSONObject jo = new JSONObject(purchaseData);
//                                String order = jo.getString("developerPayload");
//                                String purchaseToken = jo.getString("purchaseToken");
//                                HttpUtils.consumePurchase(service,purchaseToken);
//                                HttpUtils.consumePurchaseSDK(order,purchaseData,signature);
//
//                                MyGamesImpl.getInstance().ADJSubmit(4);
//                            }
//                            catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//    }
//
//    private static PurchaseClient mPurchaseClient;
//    private int isconnectionsuccess = 0;
//    @TargetApi(Build.VERSION_CODES.DONUT)
//    @Override
//    protected void onCreate( Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        int lay_id = ResourceUtil.getLayoutId(this,"myths_payview");
//        setContentView(lay_id);
//
//        MyGamesImpl.getInstance().setSdkact(this);
//
//        String data = getIntent().getStringExtra("data");
//        //绑定google play服务
//        Intent serviceIntent =
//                new Intent("com.android.vending.billing.InAppBillingService.BIND");
//        serviceIntent.setPackage("com.android.vending");
//        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
//
//        //绑定one store服务
//        mPurchaseClient = PurchaseClient.newBuilder(this)
//                .setListener(this)
//                .setBase64PublicKey(Configs.othersdkextdata2) // optional
//                .build();
//        connectiononetore();
//
//        int gv_id = ResourceUtil.getId(this,"pay_lay_gridv");
//        gv = (GridView)findViewById(gv_id);
//
//        pay_cancle_id = ResourceUtil.getId(this,"pay_lay_cancle");
//        ((TextView)findViewById(pay_cancle_id)).setOnClickListener(this);
//
//        try {
//            jsonarr= new JSONArray(paydata);
////            jsonarr = new JSONObject(data).getJSONArray("data");
//            for (int i = 0; i < jsonarr.length(); i++) {
//                list_paytype.add(new String[]{jsonarr.getJSONObject(i).getString("url"),jsonarr.getJSONObject(i).getString("pay_id"),jsonarr.getJSONObject(i).getString("remark")});
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        final MyGridViewAdapter ad = new MyGridViewAdapter(this,list_paytype);
//        gv.setAdapter(ad);
//        gv.setNumColumns(jsonarr.length());
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                position = i;
//                MLog.a("position-------->"+position);
//                getpayorder();
//            }
//        });
//
//        if(jsonarr.length()==1){
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    gv.setSelection(0);
//                    ad.notifyDataSetChanged();
////                gv.requestFocusFromTouch();
//                    gv.setSelection(0);
//                    getpayorder();
//                }
//            },100);
//        }
//
//
//    }
//
//    private void getpayorder() {
//        String paytypeid = list_paytype.get(position)[1];
//        HttpUtils.getpayorder(paytypeid,new GetorderCallBack() {
//            @Override
//            public void callback(final String orderid,final String feepoint,final String payconfirmurl) {
//                if(TextUtils.isEmpty(feepoint)){
//                    PayActivityNew.this.finish();
//                    MySdkApi.getMpaycallBack().payFail("feepoint is empty");
//                    return;
//                }
//                if(TextUtils.isEmpty(payconfirmurl)){
//                    PayActivityNew.this.finish();
//                    MySdkApi.getMpaycallBack().payFail("confirmurl is empty");
//                    return;
//                }
//                PayActivityNew.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
//                        if(paytypeid.equals("28")){
//                            Configs.gp_url = payconfirmurl;
//                            sharedPreferences.edit().putString("gp_url",Configs.gp_url).commit();
//                            googlepay(orderid,feepoint);
//                        }else{
//                            Configs.onestore_url = payconfirmurl;
//                            sharedPreferences.edit().putString("onestore_url",Configs.onestore_url).commit();
//                            oneStorePay(orderid,feepoint);
//                        }
//
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        if(id==pay_cancle_id){
//            this.finish();
//            MySdkApi.getMpaycallBack().payFail("pay_cancle");
//        }else if (id==pay_submit_id){
//            getpayorder();
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//            this.finish();
//            MySdkApi.getMpaycallBack().payFail("pay_cancle4");
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//    public  void googlepay(String orderid,String feepoint){
//        try {
//            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
//                    feepoint, "inapp", orderid);
//            int response = buyIntentBundle.getInt("RESPONSE_CODE");
//            MLog.a("response----->"+response);
//            if (response==0){
//
//                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
//                try {
//                    startIntentSenderForResult(pendingIntent.getIntentSender(),
//                            2707, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
//                            Integer.valueOf(0));
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }else{
//                MySdkApi.getMpaycallBack().payFail("pay_error");
//                this.finish();
//            }
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            MySdkApi.getMpaycallBack().payFail("pay_error1");
//            this.finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//            MySdkApi.getMpaycallBack().payFail("pay_error2");
//            this.finish();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2707) {
//            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
//            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
//            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
//            MLog.a("responseCode----->"+responseCode);
//            MLog.a("purchaseData----->"+purchaseData);
//            if(responseCode==0){
//                if (resultCode == RESULT_OK) {
//                    try {
//                        JSONObject jo = new JSONObject(purchaseData);
//                        String sku = jo.getString("productId");
//                        String order = jo.getString("developerPayload");
//                        String purchaseToken = jo.getString("purchaseToken");
//                        HttpUtils.consumePurchase(mService,purchaseToken);
//                        HttpUtils.consumePurchaseSDK(order,purchaseData,dataSignature);
//
//                        MyGamesImpl.getInstance().ADJSubmit(4);
//
//                        MySdkApi.getMpaycallBack().payFinish();
//                    }
//                    catch (JSONException e) {
//                        e.printStackTrace();
//                        MySdkApi.getMpaycallBack().payFail("pay_cancle3");
//                    }
//                    this.finish();
//                }else{
//                    MySdkApi.getMpaycallBack().payFail("pay_cancle2");
//                    this.finish();
//                }
//            }else{
//                MySdkApi.getMpaycallBack().payFail("pay_cancle1");
//                this.finish();
//            }
//
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mService != null) {
//            unbindService(mServiceConn);
//        }
//        if (mPurchaseClient != null) {
//            mPurchaseClient.endConnection();
//            mPurchaseClient = null;
//        }
//    }
//
//    //connection-onetore
//    private void connectiononetore(){
//        mPurchaseClient.startConnection(new PurchaseClientStateListener() {
//            @Override
//            public void onSetupFinished(IapResult iapResult) {
//                MLog.a("startConnection----onSetupFinished---->"+iapResult.getResponseCode());
//                if (iapResult.isSuccess()) {
//                    isconnectionsuccess = 1;
//                    // The PurchaseClient is ready. You can query purchases here.
//                    mPurchaseClient.queryPurchasesAsync(PurchaseClient.ProductType.INAPP, new PurchasesListener() {
//                        @Override
//                        public void onPurchasesResponse(IapResult iapResult, @Nullable List<PurchaseData> list) {
//                            if (iapResult.isSuccess() && list != null) {
//                                for (PurchaseData p : list) {
//                                    //
//                                    consumeOneStore(PayActivityNew.this,p);
//                                }
//                            }
//                        }
//                    });
//
//                } else if (iapResult.getResponseCode() == PurchaseClient.ResponseCode.RESULT_NEED_LOGIN) {
//                    isconnectionsuccess = 2;
//                    // The connection is completed successfully but login is required. You must write your login code here.
//                } else if (iapResult.getResponseCode() == PurchaseClient.ResponseCode.RESULT_NEED_UPDATE) {
//                    isconnectionsuccess = 3;
//                    // You need the required version of the ONE store service in the SDK.
//                } else {
//                    // Other error codes.
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected() {
//                connectiononetore();
//            }
//        });
//    }
//    //onestorepay
//    private void oneStorePay(String orderid, String feepoint) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(isconnectionsuccess==0){
//                    Toast.makeText(PayActivityNew.this,"service is not connected",Toast.LENGTH_SHORT).show();
//                    MySdkApi.getMpaycallBack().payFail("service is not connected");
//                    PayActivityNew.this.finish();
//                    return;
//                }
//                if(isconnectionsuccess==2){
//                    Toast.makeText(PayActivityNew.this,"need login",Toast.LENGTH_SHORT).show();
//                    mPurchaseClient.launchLoginFlowAsync(PayActivityNew.this, new IapResultListener() {
//                        @Override
//                        public void onResponse(IapResult iapResult) {
//                            if (iapResult.isSuccess()) {
//                                MLog.a("-----Loginsuccess-----");
//                                // You need to specify the scenario after successful login.
//                            }
//                            MySdkApi.getMpaycallBack().payFail("need login");
//                            PayActivityNew.this.finish();
//                        }
//                    });
//                    return;
//                }
//                if(isconnectionsuccess==3){
//                    MLog.a("-----You need the required version of the ONE store service in the SDK-----");
//                    MySdkApi.getMpaycallBack().payFail("need update version of the ONE store service in the SDK");
//                    PayActivityNew.this.finish();
//                    return;
//                }
//                List<String> products = new ArrayList<>();
//                products.add(feepoint);
//                ProductDetailsParams params = ProductDetailsParams.newBuilder()
//                        .setProductIdList(products).setProductType(PurchaseClient.ProductType.INAPP).build();
//                mPurchaseClient.queryProductDetailsAsync(params, new ProductDetailsListener() {
//                    @Override
//                    public void onProductDetailsResponse(IapResult iapResult, List<ProductDetail> productDetailList) {
//                        // Process the result.
//                        int code = iapResult.getResponseCode();
//                        MLog.a("onProductDetailsResponse----code---->"+code);
//                    }
//                });
//                OrderInfo orderinfo=MyApplication.getAppContext().getOrderinfo();
//
//                PurchaseFlowParams payparams = PurchaseFlowParams.newBuilder()
//                        .setProductId(feepoint)    // productDetail.getProductId()
//                        .setProductName(orderinfo.getProductname())
//                        .setProductType(PurchaseClient.ProductType.INAPP)
//                        .setDeveloperPayload(orderid)
//                        .build();
//                mPurchaseClient.launchPurchaseFlow(PayActivityNew.this,payparams);
//            }
//        });
//    }
//
//    @Override
//    public void onPurchasesUpdated(IapResult iapResult, @Nullable List<PurchaseData> list) {
//        int code = iapResult.getResponseCode();
//        MLog.a("onPurchasesUpdated----code---->"+code);
//        if (iapResult.isSuccess() && list != null) {
//            for (PurchaseData p : list) {
//                //
//                consumeOneStore(this,p);
//            }
//        }else{
//            MySdkApi.getMpaycallBack().payFail("code："+code);
//        }
//        PayActivityNew.this.finish();
//    }
//
//    public static void consumeOneStore(Activity act,PurchaseData p){
//        HttpUtils.consumeOneStorePurchase(p.getDeveloperPayload(), p.getOriginalJson(), p.getSignature(),
//                new PayConsumeCallback() {
//                    @Override
//                    public void result(Boolean issuc, String msg) {
//                        act.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(issuc){
//                                    MyGamesImpl.getInstance().ADJSubmit(4);
//                                    ConsumeParams params  = ConsumeParams.newBuilder().setPurchaseData(p).build();
//                                    mPurchaseClient.consumeAsync(params, new ConsumeListener() {
//                                        @Override
//                                        public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {
//                                            MLog.a("onConsumeResponse----code---->"+iapResult.getResponseCode());
//                                            if (iapResult.isSuccess()) {
//                                                MySdkApi.getMpaycallBack().payFinish();
//                                                // Process the result.
//                                            } else if (iapResult.getResponseCode() == PurchaseClient.ResponseCode.RESULT_NEED_UPDATE) {
//                                                // You need the required version of the ONE store service in the SDK.
//                                            } else {
//                                                // Other error codes.
//                                            }
//                                        }
//                                    });
//                                }else{
//                                    MySdkApi.getMpaycallBack().payFail(msg);
//                                }
//                            }
//                        });
//                    }
//                }
//        );
//    }
//}
