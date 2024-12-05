package com.sdk.mysdklibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.othersdk.SkipUtil;

import androidx.annotation.Nullable;

public class AutoLoginActivity extends Activity implements View.OnClickListener {
    int change_acc,change_acc2,auto_cancle,acc_bindfb,acc_bindgg,change_acc_logo,change_acc_text;
    String type="";
    boolean ishw = false;
    boolean isUseBind = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lay_id = ResourceUtil.getLayoutId(this,"myths_autoview");
        int lay_id_hw = ResourceUtil.getLayoutId(this,"myths_autoview_hw");
        String pub = FilesTool.getPublisherStringContent();
        isUseBind = ResourceUtil.getString(this, "myths_yk_usebind").equals("true");
        ishw = pub.startsWith("huawei");
        TextView change_acc_tv2 = null,change_acc_logotv = null;
        if (isUseBind) {//使用绑定功能
            setContentView(lay_id_hw);
            change_acc2 = ResourceUtil.getId(this,"change_acc2");
            change_acc_logo = ResourceUtil.getId(this,"change_acc_logo");
            change_acc_tv2 = (TextView)findViewById(change_acc2);
            change_acc_logotv = (TextView)findViewById(change_acc_logo);
        }else{
            setContentView(lay_id);
        }

        type = getIntent().getStringExtra("type");

        int acc_id = ResourceUtil.getId(this,"acc_tv");
        change_acc = ResourceUtil.getId(this,"change_acc");
        change_acc_text = ResourceUtil.getId(this,"change_acc_text");
        auto_cancle = ResourceUtil.getId(this,"auto_cancle");
        acc_bindfb= ResourceUtil.getId(this,"lay_fblogin");
        acc_bindgg= ResourceUtil.getId(this,"lay_gglogin");
        int ll_lay=ResourceUtil.getId(this,"ll_lay");
        int ll_lay_face=ResourceUtil.getId(this,"ll_lay_face");
        int load_log=ResourceUtil.getId(this,"load_log");
        int loading_tips_id=ResourceUtil.getId(this,"loading_tips");
        LinearLayout lo_bg_guest = (LinearLayout)findViewById(ll_lay);
        TextView lo_bg_facebook = (TextView)findViewById(ll_lay_face);
        TextView load_tv = (TextView)findViewById(load_log);
        TextView loading_tips = (TextView)findViewById(loading_tips_id);
        TextView acc_bind_tv = (TextView)findViewById(ResourceUtil.getId(this,"acc_bind_tv"));

        LinearLayout lay_bind = (LinearLayout)findViewById(ResourceUtil.getId(this,"lay_bind"));
        LinearLayout lay_fblogin = (LinearLayout)findViewById(acc_bindfb);
        LinearLayout lay_gglogin = (LinearLayout)findViewById(acc_bindgg);

        TextView acc_tv = (TextView)findViewById(acc_id);
        LinearLayout change_acc_tv = (LinearLayout)findViewById(change_acc);
        TextView auto_cancle_tv = (TextView)findViewById(auto_cancle);
        TextView change_acc_tv_text = (TextView)findViewById(change_acc_text);
        
        change_acc_tv.setOnClickListener(this);
        auto_cancle_tv.setOnClickListener(this);
        lay_fblogin.setOnClickListener(this);
        lay_gglogin.setOnClickListener(this);

        PhoneTool.auto_Login_Animator(load_tv,0.0f,720.0f,3000);

        SharedPreferences sharedPreferences = MyGamesImpl.getSharedPreferences();
        String acc="";
        if ("guest".equals(type)){
            acc_tv.setVisibility(View.VISIBLE);
            acc_bind_tv.setVisibility(isUseBind?View.VISIBLE:View.GONE);
            if (isUseBind) {
                change_acc_tv2.setVisibility(View.VISIBLE);
                change_acc_tv2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                change_acc_tv2.getPaint().setAntiAlias(true);//抗锯齿
                change_acc_tv2.setOnClickListener(this);
                change_acc_logotv.setBackgroundResource(ResourceUtil.getDrawableId(this,ishw?"myths_logo_hw":"myths_logo_email"));
                change_acc_tv.setBackgroundResource(ResourceUtil.getDrawableId(this,ishw?"myths_btn_hw":"myths_btn_zt"));
                change_acc_tv_text.setText(ResourceUtil.getString(this,ishw?"myths_account_bind_hw":"myths_account_bind_email"));
            }
            lay_bind.setVisibility(View.GONE);
            lo_bg_facebook.setVisibility(View.GONE);
            acc=sharedPreferences.getString("myths_youke_name","");
            CharSequence charSequence;
            String str1 = acc_tv.getText().toString()+"<font color = '#FF4500'>"+acc+"</font>";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                charSequence = Html.fromHtml(str1, Html.FROM_HTML_MODE_LEGACY);
            } else {
                charSequence = Html.fromHtml(str1);
            }
            acc_tv.setText(charSequence);
            
        }else if("facebook".equals(type)||"google".equals(type)||"wallet".equals(type)||"wemix".equals(type)||"vk".equals(type)||"othersdk".equals(type)){
            lo_bg_guest.setBackground(null);
            lo_bg_facebook.setVisibility(View.VISIBLE);
            lo_bg_facebook.setBackground(null);
            int fb_bg = ResourceUtil.getDrawableId(this,"myths_fblogin_big");
            int goole_bg = ResourceUtil.getDrawableId(this,"myths_gglogin_big");
            if("facebook".equals(type)){
                acc=sharedPreferences.getString("myths_fbname","");
                lo_bg_facebook.setBackgroundResource(fb_bg);
            }else if("google".equals(type)){
                acc=sharedPreferences.getString("myths_googleid","");
                lo_bg_facebook.setBackgroundResource(goole_bg);
            }else if("wallet".equals(type)){
                int wc_bg = ResourceUtil.getDrawableId(this,"myths_wclogin");
                acc=sharedPreferences.getString("myths_walletid","");
                if(acc.length()>15){
                    acc = acc.substring(0,15)+"...";
                }
                lo_bg_facebook.setBackgroundResource(wc_bg);
            }else if("wemix".equals(type)){
                lo_bg_facebook.setVisibility(View.GONE);
                acc=sharedPreferences.getString("myths_wemixaddress","");
                if(acc.length()>15){
                    acc = acc.substring(0,15)+"...";
                }
            }else if("vk".equals(type)){
                int vk_log = ResourceUtil.getDrawableId(this,"myths_vklogin");
                lo_bg_facebook.setBackgroundResource(vk_log);
                acc=sharedPreferences.getString("accountid","");
            }
            else if("othersdk".equals(type)){
                String Publisher = FilesTool.getPublisherStringContent().split("sdk_")[0];
                int sdk_log = ResourceUtil.getDrawableId(this,"myths_"+Publisher+"login");
                lo_bg_facebook.setBackgroundResource(sdk_log);
                acc=sharedPreferences.getString("accountid","");
            }
            CharSequence charSequence;
            String str1 = "<font color = '#FF4500'>"+acc+"</font>"+loading_tips.getText().toString();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                charSequence = Html.fromHtml(str1, Html.FROM_HTML_MODE_LEGACY);
            } else {
                charSequence = Html.fromHtml(str1);
            }
            loading_tips.setText(charSequence);
            acc_tv.setVisibility(View.GONE);
            acc_bind_tv.setVisibility(View.GONE);
        }else if("oldacc".equals(type)||"email".equals(type)){
            lo_bg_guest.setBackground(null);
            lo_bg_facebook.setBackground(null);
            int acc_logo_id =ResourceUtil.getDrawableId(this,"myths_acclogin");
            lo_bg_facebook.setBackgroundResource(acc_logo_id);
            lo_bg_facebook.setVisibility(View.GONE);
            if("oldacc".equals(type)){
                acc=sharedPreferences.getString("myths_oldacc_name","");
            }else{
                acc=sharedPreferences.getString("myths_email","");
            }
            CharSequence charSequence;
            String str2 = "<font color = '#FF4500'>"+acc+"</font>"+loading_tips.getText().toString();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                charSequence = Html.fromHtml(str2, Html.FROM_HTML_MODE_LEGACY);
            } else {
                charSequence = Html.fromHtml(str2);
            }
            loading_tips.setText(charSequence);
            acc_tv.setVisibility(View.GONE);
            acc_bind_tv.setVisibility(View.GONE);
        }
        //进入到自动登录界面上报
        PhoneTool.submitSDKEvent("2","enter autoLogin page: "+type);
        //3秒后自动登录
        PhoneTool.autoLogin(this,2,type);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        PhoneTool.setAutoLogin_time_milliseconds(-1);
        if (id==change_acc && isUseBind && "guest".equals(type)){
            this.finish();
            HttpUtils.acclogin("guest");
            //跳转绑定
            if(ishw){
                SkipUtil.othLogin(MySdkApi.getMact(),"bind");
            }else{
                MyGamesImpl.getInstance().openEmailLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),"bind");
            }
            return;
        }
        if (id==change_acc ||id==change_acc2){
            //切换按钮上报
            PhoneTool.submitSDKEvent("3",type);
            if("facebook".equals(type)){
                MyGamesImpl.getInstance().logout();
            }else if("oldacc".equals(type)){
                MyGamesImpl.getSharedPreferences().edit().putString("myths_oldacc_name","").apply();
                MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
            }else if("guest".equals(type)){
            }else if("email".equals(type)){
                MyGamesImpl.getSharedPreferences().edit().putString("myths_email","").apply();
                MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
            }else if("google".equals(type)){
                MyGamesImpl.getInstance().googlelogout();
            }else if("wallet".equals(type)){
                MyGamesImpl.getInstance().walletlogout();
            }else if("wemix".equals(type)){
                MyGamesImpl.getInstance().wemixlogout();
                this.finish();
                MyGamesImpl.getInstance().wemixLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack());
                return;
            }else{
                MyGamesImpl.getSharedPreferences().edit().putString("myths_auto_type", "").apply();
            }
            this.finish();
            Intent itn = new Intent(MySdkApi.getMact(), LoginActivity.class);
            itn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MySdkApi.getMact().startActivity(itn);
        }else if (id==auto_cancle){
            this.finish();
            //登录失败上报
            PhoneTool.submitSDKEvent("16","autologin_cancle");
            MySdkApi.getLoginCallBack().loginFail("login_cancle");
        }else if (id==acc_bindfb){
            this.finish();
            //游客登录界面账号升级按钮上报
//            PhoneTool.submitEvent("14","");
            MyGamesImpl.getInstance().openfacebookLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),"bind");
        }else if (id==acc_bindgg){
            this.finish();
            //游客登录界面账号升级按钮上报
//            PhoneTool.submitEvent("14","");
            MyGamesImpl.getInstance().opengoogleLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),"bind");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            PhoneTool.setAutoLogin_time_milliseconds(-1);
            this.finish();
            //登录失败上报
            PhoneTool.submitSDKEvent("16","autologin_cancle_KEYCODE_BACK");
            MySdkApi.getLoginCallBack().loginFail("login_cancle");
        }
        return super.onKeyDown(keyCode, event);
    }
}
