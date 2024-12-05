package com.sdk.mysdklibrary.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;
import com.sdk.mysdklibrary.activity.Adaper.MyListViewAdapter;
import com.sdk.mysdklibrary.impl.WalletCallback;
import com.sdk.mysdklibrary.interfaces.ApproveCallback;
import com.sdk.mysdklibrary.interfaces.GetorderCallBack;
import com.sdk.mysdklibrary.interfaces.PayConsumeCallback;
import com.sdk.mysdklibrary.interfaces.WemixRefreshCallback;
import com.sdk.mysdklibrary.interfaces.WemixSignCallback;
import com.sdk.mysdklibrary.payUtils.GoogleUtil;
import com.sdk.mysdklibrary.walletconnect.WemixUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PayActivity extends Activity implements View.OnClickListener {
    int pay_cancle_id,pay_submit_id;
    ListView gv = null;
    JSONArray jsonarr=null;
    int position=0;

    ArrayList<String[]> list_pay = new ArrayList<String[]>();
    List<String> walletChainIds = new ArrayList<>();

    private Timer tim;
    private static WalletCallback wcallback;
    private String cur_order = "sdk-null";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lay_id = ResourceUtil.getLayoutId(this,"myths_payview");
        setContentView(lay_id);
        MyGamesImpl.getInstance().setSdkact(this);
        String data = getIntent().getStringExtra("data");
        initViewData(data);

        //全面屏设置
        PhoneTool.hideSystemBars(getWindow());
        PhoneTool.useSpecialScreen(getWindow());
        //钱包初始化
        MyGamesImpl.getInstance().walletInit();
    }

    private void initViewData(String data){
        int gv_id = ResourceUtil.getId(this,"pay_lay_gridv");
        gv = findViewById(gv_id);
        pay_cancle_id = ResourceUtil.getId(this,"pay_lay_cancle");
        TextView pay_cancle = findViewById(pay_cancle_id);
        ResourceUtil.expandTouchArea(pay_cancle,ResourceUtil.dip2px(this,10));
        pay_cancle.setOnClickListener(this);

        try {
            jsonarr = new JSONObject(data).getJSONArray("data");
            for (int i = 0; i < jsonarr.length(); i++) {
                JSONObject data_item = jsonarr.getJSONObject(i);
                list_pay.add(new String[]{ResourceUtil.getJsonItemByKey(data_item,"url"),//支付图片
                        ResourceUtil.getJsonItemByKey(data_item,"pay_id"),
                        ResourceUtil.getJsonItemByKey(data_item,"remark"),//支付名称
                        ResourceUtil.getJsonItemByKey(data_item,"title"),//支付描述
                        ResourceUtil.getJsonItemByKey(data_item,"content"),//?文案
                        ResourceUtil.getJsonItemByKey(data_item,"switch1"),//官方标志
                        ResourceUtil.getJsonItemByKey(data_item,"switch2")});//百分比

                // wallet pay need chain id
                String payid=data_item.getString("pay_id");
                if("32".equals(payid)||"36".equals(payid)){
                    if(data_item.has("chainId")){
                        walletChainIds.clear();
                        String[] rChainId = data_item.getString("chainId").split("@");
                        for (String chainId : rChainId) {
                            walletChainIds.add(chainId);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MLog.a("list_pay-------->"+list_pay.size());

        final MyListViewAdapter ad = new MyListViewAdapter(this,list_pay);
        gv.setAdapter(ad);
        setListViewWidth(gv);
//        setListViewHeight(gv);//重新计算高度

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                MLog.a("position--OnItemClick------>"+position);
                PayActivity.this.onItemClick(position);
            }
        });

        if(jsonarr.length()==1){
            setMainViewGone();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    gv.setSelection(0);
                    ad.notifyDataSetChanged();
//                gv.requestFocusFromTouch();
                    gv.setSelection(0);
                    PayActivity.this.onItemClick(0);
                }
            },100);
        }
    }

    private void setListViewWidth(ListView listView){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)listView.getLayoutParams();
        if(ResourceUtil.island(this)){
            layoutParams.width = ResourceUtil.dip2px(this,535);
        }else{
            layoutParams.width = ResourceUtil.dip2px(this,335);
        }
        listView.setLayoutParams(layoutParams);
    }
    private void setListViewHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter(); //得到GridView 添加的适配器
        if(listAdapter == null){
            return;
        }
        View itemView = listAdapter.getView(0, null, listView); //获取其中的一项
        itemView.measure(0,0);
        int itemHeight = itemView.getMeasuredHeight(); //一项的高度
        LinearLayout.LayoutParams layoutParams = null; //进行布局参数的设置
//        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,itemHeight*2);
        layoutParams = (LinearLayout.LayoutParams)listView.getLayoutParams();
        if(listView.getCount()<=3){
            layoutParams.height=(itemHeight+10)*listView.getCount();
        }else{
            layoutParams.height=(int)((itemHeight+10)*3.5);
        }
        System.out.println("listView.Height--->"+layoutParams.height);
        listView.setLayoutParams(layoutParams);

        int lay_payview_id = ResourceUtil.getId(this,"lay_payview");
        int pay_lay_id = ResourceUtil.getId(this,"pay_lay");
        LinearLayout lay_payview = findViewById(lay_payview_id);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)lay_payview.getLayoutParams();
        lp.height = layoutParams.height+230;
        LinearLayout pay_lay = findViewById(pay_lay_id);
        RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)pay_lay.getLayoutParams();
        lp2.height = layoutParams.height+200;
        System.out.println("lp.height--->"+lp.height);
        lay_payview.setLayoutParams(lp);
        pay_lay.setLayoutParams(lp2);
    }
    private void setMainViewGone(){
        int lay_payview_id = ResourceUtil.getId(this,"lay_payview");
        LinearLayout lay_payview = findViewById(lay_payview_id);
        lay_payview.setVisibility(View.GONE);
    }

    private void onItemClick(int position) {
        if("32".equals(list_pay.get(position)[1])||"36".equals(list_pay.get(position)[1])){//钱包支付，预先连接钱包再下单
            createWCallback();
            if(tim == null) tim = new Timer();
            try {
                tim.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        PhoneTool.disDialog();
                    }
                },5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PhoneTool.onCreateDialog(MyGamesImpl.getInstance().getSdkact(),"","");
            MyGamesImpl.getInstance().connectWallet(wcallback,
                    walletChainIds.size() >0 ? walletChainIds.get(0):null,false);
        } else if("38".equals(list_pay.get(position)[1])){
            long timestamp_now = System.currentTimeMillis();
            long timestamp_login = MyGamesImpl.getSharedPreferences().getLong("myths_wemixtimestamp",0);
            long expires = MyGamesImpl.getSharedPreferences().getLong("myths_wemixexpires",0);
            if((timestamp_now-timestamp_login)/1000+20>expires){//从wemix登录到支付的时间间隔大于token的有效期
                WemixUtil.getInstance().RefreshToken(PayActivity.this, new WemixRefreshCallback() {
                    @Override
                    public void onSuccess(String address) {
                        getpayorder(address,"");
                    }

                    @Override
                    public void onError(String msg) {
                        showDia(msg,PromptDialog.PAY_WALLET_FAILED,false);
                    }
                });
            }else {
                getpayorder(MyGamesImpl.getSharedPreferences().getString("myths_wemixaddress",""), "");
            }
        } else if("23".equals(list_pay.get(position)[1])){//沙箱支付
            String proId = MyApplication.getAppContext().getOrderinfo().getFeepoint();
            String money = MyApplication.getAppContext().getOrderinfo().getAmount();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sandbox payment");
            builder.setMessage("Product ID："+proId+"\nMoney："+money);
            builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    getpayorder("","");
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PayActivity.this.payFail(1003,"Cancel sandbox payment");
                        }
                    });

            builder.setCancelable(false);
            builder.create().show();
        } else {
            getpayorder("","");
        }
    }

    private void createWCallback(){
        wcallback = new WalletCallback() {

            @Override
            public void signCallback(@Nullable String address, @NonNull String sign) {

            }

            @Override
            public boolean connectCallback(@Nullable String walletaddress,Integer chainId,String chainIdStr) {
                PhoneTool.disDialog();
                try {
                    tim.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(walletChainIds.contains(chainIdStr)){
                    if(walletaddress!=null){
                        getpayorder(walletaddress,chainIdStr);
                    }else{
                        PayActivity.this.payFail(1000,"wallet connect failed");
                    }
                    return true;
                }else{
                    wcallback.payCallback(-10,list_pay.get(position)[4]);
                    return false;
                }
            }

            @Override
            public void payCallback(int code, @Nullable String msg) {
                if(code==0){//钱包支付完成
                    showDia(ResourceUtil.getString(PayActivity.this,"myths_paywallet_suc"),PromptDialog.PAY_WALLET_SUC, false);
                }else{//钱包支付失败
                    showDia(msg,PromptDialog.PAY_WALLET_FAILED,false);
                }
                MyGamesImpl.getInstance().closeConnect();
            }
        };
    }

    private void showDia(String con, String type, boolean b){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PromptDialog promptDialog = new PromptDialog(PayActivity.this,
                        con,
                        ResourceUtil.getStyleId(PayActivity.this,"ay_dialog_style"),
                        type);
                promptDialog.setCancelable(b);// 点击返回键或者空白处消失
                promptDialog.setClickListener(new PromptDialog.ClickInterface() {
                    @Override
                    public void doCofirm() {
                        promptDialog.dismiss();
                        if(type == PromptDialog.PAY_WALLET_SUC){
                            if(MySdkApi.getMpaycallBack()!=null)
                                MySdkApi.getMpaycallBack().payFinish();
                            PayActivity.this.finish();
                        }else if(type == PromptDialog.PAY_WALLET_FAILED){
                            PayActivity.this.payFail(1001,con);;
                        }
                    }
                });
                promptDialog.show();
            }
        });
    }

    private void getpayorder(String walletaddress, String chainId) {
        String paytypeid = list_pay.get(position)[1];
        System.out.println("getpayorder---paytypeid-->"+paytypeid);
        if(cur_order.equals(MyApplication.getAppContext().getOrderinfo().getTransactionId())){
            ToastUtils.Toast("Please re-order");
            PayActivity.this.finish();
            MySdkApi.getMpaycallBack().payFail("Please re-order");
            MyGamesImpl.getInstance().closeConnect();
            return;
        }
        HttpUtils.getpayorder(paytypeid,walletaddress,chainId,new GetorderCallBack() {
            @Override
            public void callback(final String orderid, final String feepoint, final String payconfirmurl,
                                 String wallet_authData, String wallet_payData, String wallet_authTo, String wallet_payTo, String wallet_nonce,
                                 HashMap<String,String> wemix_param) {
                cur_order = MyApplication.getAppContext().getOrderinfo().getTransactionId();
                //固定金额时需要传计费点
                if(!MyApplication.getAppContext().getOrderinfo().isAnyAmount()){
                    if(TextUtils.isEmpty(feepoint)){
                        PayActivity.this.finish();
                        MySdkApi.getMpaycallBack().payFail("feepoint is empty");
                        return;
                    }
                }

                PayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences =MyGamesImpl.getSharedPreferences();
                        if(paytypeid.equals("28")){//google支付
                            if(!GoogleUtil.getInstance().isGoogleOk()){
                                payFail(666,"startConnection:"+"null-billingResult");
                                return;
                            }

                            if(TextUtils.isEmpty(payconfirmurl)){
                                showDia(ResourceUtil.getString(PayActivity.this,"myths_no_url"),PromptDialog.PAY_WALLET_FAILED,false);
                                return;
                            }
                            Configs.gp_url = payconfirmurl;
                            sharedPreferences.edit().putString("gp_url",Configs.gp_url).apply();
                            GoogleUtil.getInstance().querySkuDetailsAndPay(PayActivity.this,orderid,feepoint);
                        }else if (paytypeid.equals("31") || paytypeid.equals("34") || paytypeid.equals("35") || paytypeid.equals("39") || (paytypeid.startsWith("5")&&paytypeid.length()==2)){//enjoy;payermax;xdLocal;durk
                            if(TextUtils.isEmpty(payconfirmurl)){
                                showDia(ResourceUtil.getString(PayActivity.this,"myths_no_url"),PromptDialog.PAY_WALLET_FAILED,false);
                                return;
                            }
                            payerMaxPay(orderid,feepoint,payconfirmurl);
                        }else if (paytypeid.equals("32")||paytypeid.equals("36")){//钱包支付
                            if(Configs.isTestwallet()){//测试
                                MyGamesImpl.getInstance().payWallet("",Configs.getPayData(),"",Configs.getPayTo(),"");
                            }else{
                                MyGamesImpl.getInstance().payWallet(wallet_authData,wallet_payData,wallet_authTo,wallet_payTo,wallet_nonce);
                            }
                        }else if (paytypeid.equals("33")) {//平台币支付
                            PayActivity.this.finish();
                            MySdkApi.getMpaycallBack().payFinish();
                        }else if (paytypeid.equals("23")) {//沙箱测试
                            PayActivity.this.showDia(ResourceUtil.getString(PayActivity.this, "myths_sandboxPay_suc"), PromptDialog.PAY_WALLET_SUC, false);
                        }
                        else if (paytypeid.equals("38")) {//wemix支付
                            if(TextUtils.isEmpty(payconfirmurl)){
                                showDia(ResourceUtil.getString(PayActivity.this,"myths_no_url"),PromptDialog.PAY_WALLET_FAILED,false);
                                return;
                            }
                            String wemix_contract = wemix_param.get("wemix_contract");
                            String wemix_column = wemix_param.get("wemix_column");
                            String wemix_fee = wemix_param.get("wemix_fee");
                            String useraddress = MyGamesImpl.getSharedPreferences().getString("myths_wemixaddress","");
                            String wemix_limit = wemix_param.get("wemix_limit");
                            String wemix_approvedhash = wemix_param.get("wemix_approvedhash");
                            if(!"1".equals(wemix_limit)){//不需要授权
                                wemixPay(wemix_contract,useraddress,payconfirmurl,wemix_column,wallet_nonce,wemix_fee,orderid,wemix_approvedhash);
                                return;
                            }
                            //需要先授权，再发起支付签名
                            String wemix_approveurl = wemix_param.get("wemix_approveurl");
                            WemixUtil.getInstance().sign(PayActivity.this, useraddress, wemix_approvedhash, new WemixSignCallback() {
                                @Override
                                public void onSuccess(String sign) {
                                    handleApproveSign(wemix_approveurl,payconfirmurl,sign,wemix_contract,useraddress,wallet_nonce,wemix_fee,wemix_column,orderid);
                                }

                                @Override
                                public void onError(String msg) {
                                    showDia(msg,PromptDialog.PAY_WALLET_FAILED,false);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void payerMaxPay(String orderid, String feepoint, String payconfirmurl) {
        Intent dataIntent =new Intent();
        dataIntent.putExtra("orderid",orderid);
        dataIntent.putExtra("feepoint",feepoint);
        dataIntent.putExtra("url",payconfirmurl);
        dataIntent.setClass(PayActivity.this,PayerMayActivty.class);
        startActivity(dataIntent);
    }
    private void handleApproveSign(String approveurl,String payurl,String usersign,String contract,String useraddress,String nonce,String fee,String column,String orderid){
        HttpUtils.handleApproveSign(approveurl,usersign,useraddress,contract,orderid,column,fee,new ApproveCallback(){

            @Override
            public void result(Boolean issuc, String msgorhash) {
                if(issuc) {//wemix钱包授权成功-走支付流程
                    wemixPay(contract,useraddress,payurl,column,nonce,fee,orderid,msgorhash);
                }else{//授权失败-wemix钱包支付失败
                    showDia(msgorhash,PromptDialog.PAY_WALLET_FAILED,false);
                }
            }
        });
    }

    private void wemixPay(String contract,String useraddress,String url,String column,String nonce,String fee,String orderid, String hash){
        WemixUtil.getInstance().sign(PayActivity.this, useraddress, hash, new WemixSignCallback() {
            @Override
            public void onSuccess(String sign) {
                System.out.println("sign---"+sign);
                handleWemixPayParam(url,sign,contract,useraddress,nonce,column,fee,orderid);
            }

            @Override
            public void onError(String msg) {
                showDia(msg,PromptDialog.PAY_WALLET_FAILED,false);
            }
        });
    }

    private void handleWemixPayParam(String url,String sign,String contract,String useraddress,String nonce,String column,String fee,String orderid){
        HttpUtils.handleWemixPayParam(url,sign,contract,useraddress,nonce,column,fee,orderid, new PayConsumeCallback() {
            @Override
            public void result(Boolean issuc, String msg) {
                if(issuc){//wemix钱包支付完成
                    showDia(ResourceUtil.getString(PayActivity.this,"myths_paywallet_suc"),PromptDialog.PAY_WALLET_SUC, false);
                }else{//wemix钱包支付失败
                    showDia(msg,PromptDialog.PAY_WALLET_FAILED,false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==pay_cancle_id){
            this.finish();
            MySdkApi.getMpaycallBack().payFail("pay_cancle");
            MyGamesImpl.getInstance().closeConnect();
        }else if (id==pay_submit_id){
            MLog.a("pay_submit_id-------->");
            MLog.a("position--pay_submit_id------>"+position);
            getpayorder("", null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            MySdkApi.getMpaycallBack().payFail("pay_cancle4");
            MyGamesImpl.getInstance().closeConnect();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            PhoneTool.disDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void payFail(int responseCode,String debugMessage){
        MySdkApi.getMpaycallBack().payFail("code:"+responseCode+",msg:"+debugMessage);
        MyGamesImpl.getInstance().getSdkact().finish();
    }
}
