package com.sdk.mysdklibrary.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.Configs;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.othersdk.SkipUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    int cancle_id,hwlogin_id,google_con_id,vklogin_id,fastlogin_id,emaillogin_id,appbazarlogin_id,fblogin_id;
    RelativeLayout loginlayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lay_id = ResourceUtil.getLayoutId(this,"myths_loginview");
        setContentView(lay_id);
        cancle_id = ResourceUtil.getId(this,"myths_cancle");
        hwlogin_id = ResourceUtil.getId(this,"hwlogin_icon");
        google_con_id = ResourceUtil.getId(this,"googlelogin_icon");
        vklogin_id = ResourceUtil.getId(this,"vklogin_icon");
        fastlogin_id = ResourceUtil.getId(this,"fastlogin_icon");
        emaillogin_id = ResourceUtil.getId(this,"emaillogin_icon");
        fblogin_id = ResourceUtil.getId(this,"fblogin_icon");
        int fblogin_log_id = ResourceUtil.getId(this,"fblogin_icon_log");
        int loginlayout_id = ResourceUtil.getId(this,"myths_loginlayout");
        int myths_log_login = ResourceUtil.getId(this,"myths_log_login");
        int lay_cancle = ResourceUtil.getId(this,"lay_cancle");
        int login_lay_id = ResourceUtil.getId(this,"login_lay");

        TextView cancle = findViewById(cancle_id);
        LinearLayout hwlogin = findViewById(hwlogin_id);
        LinearLayout googlelogin = findViewById(google_con_id);
        LinearLayout vklogin = findViewById(vklogin_id);
        LinearLayout fastlogin = findViewById(fastlogin_id);
        LinearLayout emaillogin = findViewById(emaillogin_id);
        LinearLayout fblogin = findViewById(fblogin_id);
        TextView fblogin_log = findViewById(fblogin_log_id);
        loginlayout =findViewById(loginlayout_id);

        String loginUiNew = ResourceUtil.getString(this, "myths_loginui_new");
        if(loginUiNew.equals("true")){//使用新登录ui，隐藏登录背景
            findViewById(myths_log_login).setVisibility(View.GONE);
            findViewById(lay_cancle).setVisibility(View.GONE);
            loginlayout.setBackground(null);
            //设置按钮大小
            LinearLayout.LayoutParams l_item = (LinearLayout.LayoutParams)vklogin.getLayoutParams();
            l_item.setMargins(0,2,0,0);
            l_item.width = (int)(l_item.width*1.6);
            l_item.height = (int)(l_item.height*1.65);

            vklogin.setLayoutParams(l_item);
            hwlogin.setLayoutParams(l_item);
            googlelogin.setLayoutParams(l_item);
            fastlogin.setLayoutParams(l_item);
            emaillogin.setLayoutParams(l_item);

            LinearLayout.LayoutParams fb_log_item = (LinearLayout.LayoutParams)fblogin_log.getLayoutParams();
            fb_log_item.width = (int)(fb_log_item.width*1.65);
            fb_log_item.height = (int)(fb_log_item.height*1.65);
            fblogin_log.setLayoutParams(fb_log_item);
            fblogin.setLayoutParams(l_item);
            //设置居中
            LinearLayout login_lay = findViewById(login_lay_id);
            RelativeLayout.LayoutParams l_login = (RelativeLayout.LayoutParams)login_lay.getLayoutParams();
            l_login.setMargins(0,0,0,0);
            l_login.addRule(RelativeLayout.CENTER_IN_PARENT);
            login_lay.setLayoutParams(l_login);
            //设置字体大小
            int tv_vk_id = ResourceUtil.getId(this,"tv_vk");
            int tv_hw_id = ResourceUtil.getId(this,"tv_hw");
            int tv_gg_id = ResourceUtil.getId(this,"tv_gg");
            int tv_yk_id = ResourceUtil.getId(this,"tv_yk");
            int tv_email_id = ResourceUtil.getId(this,"tv_email");
            int tv_fb_id = ResourceUtil.getId(this,"tv_fb");
            ((TextView)findViewById(tv_vk_id)).setTextSize(20);
            ((TextView)findViewById(tv_hw_id)).setTextSize(20);
            ((TextView)findViewById(tv_gg_id)).setTextSize(20);
            ((TextView)findViewById(tv_yk_id)).setTextSize(20);
            ((TextView)findViewById(tv_email_id)).setTextSize(20);
            ((TextView)findViewById(tv_fb_id)).setTextSize(20);
        }
        resetView(vklogin,hwlogin,googlelogin,emaillogin,fastlogin,fblogin);

        ResourceUtil.expandTouchArea(cancle,ResourceUtil.dip2px(this,10));
        cancle.setOnClickListener(this);
        hwlogin.setOnClickListener(this);
        googlelogin.setOnClickListener(this);
        vklogin.setOnClickListener(this);
        fastlogin.setOnClickListener(this);
        emaillogin.setOnClickListener(this);
        fblogin.setOnClickListener(this);
        //设置隐藏的日志调试开关
        fastlogin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try{
                    MLog.setDebug(true);
                    MyGamesImpl.getSharedPreferences().edit().putBoolean("isOpenDebug",true).apply();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });
        //登录界面上报
        PhoneTool.submitSDKEvent("1","enter login page");

        boolean isHwGone = hwlogin.getVisibility() == View.GONE;
        boolean isVkGone = vklogin.getVisibility() == View.GONE;
        boolean isFastGone = fastlogin.getVisibility() == View.GONE;
        boolean isEmailGone = emaillogin.getVisibility() == View.GONE;
        boolean isggVisible = googlelogin.getVisibility() == View.VISIBLE;
        boolean isFbGone = fblogin.getVisibility() == View.GONE;

        if(isHwGone&&isVkGone&&isFastGone&&isEmailGone&&isFbGone&&isggVisible){//只有google登录时直接跳转
            PhoneTool.submitSDKEvent("4","login_google");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),3);
            this.finish();
            return;
        }
//        if(isHwGone&&isVkGone&&isFastGone&&isEmailGone&&isAppBazarVisible){//只有AppBazar登录时直接跳转
//            PhoneTool.submitSDKEvent("20","login_AppBazar");
//            MySdkApi.chooseLogin(MySdkApi.getMact(),MySdkApi.getLoginCallBack(),10);
//            this.finish();
//            return;
//        }
        //vk-旧版
        if(SkipUtil.getVkSdk() !=null)SkipUtil.getVkSdk().initSDK(this);
        //vk-新版
        if(SkipUtil.getVkIdSdk() !=null)SkipUtil.getVkIdSdk().initSDK(this);
    }
    //ui布局修改
    private void resetView(LinearLayout vklogin, LinearLayout hwlogin, LinearLayout googlelogin, LinearLayout emaillogin, LinearLayout fastlogin, LinearLayout fblogin) {
        LinearLayout.LayoutParams l = (LinearLayout.LayoutParams)loginlayout.getLayoutParams();
        //vk登录开关-默认开启
        String vkLogin = Configs.getItem(Configs.vkLogin);
        String vkclient = ResourceUtil.getString(this, "vk_client_secret");
        if(TextUtils.isEmpty(vkclient)||vkLogin.equals("0")){
            vklogin.setVisibility(View.GONE);
            l.height = l.height-vklogin.getLayoutParams().height;
        }
        //非华为包隐藏华为登录
        String pub = FilesTool.getPublisherStringContent();
        if (!pub.startsWith("huawei")){
            hwlogin.setVisibility(View.GONE);
            l.height = l.height-hwlogin.getLayoutParams().height;
//            LinearLayout.LayoutParams l2 = (LinearLayout.LayoutParams)googlelogin.getLayoutParams();
//            l2.setMargins(0,0,0,0);
//            googlelogin.setLayoutParams(l2);
        }
        //google登录开关-默认开启
        String ggLogin = Configs.getItem(Configs.ggLogin);
        String ggId = ResourceUtil.getString(this, "pg_google_login_id");
        if(TextUtils.isEmpty(ggId)||TextUtils.isEmpty(ggLogin)||ggLogin.equals("0")){
            googlelogin.setVisibility(View.GONE);
            l.height = l.height-googlelogin.getLayoutParams().height;
        }
        //email登录开关-默认关闭
        String eLogin = Configs.getItem(Configs.eLogin);
        if(eLogin.equals("1")){
            emaillogin.setVisibility(View.VISIBLE);
            l.height = l.height+emaillogin.getLayoutParams().height;
        }
        //游客开关-默认关闭
        if("1".equals(Configs.othersdkextdata3)){
            fastlogin.setVisibility(View.VISIBLE);
            l.height = l.height+fastlogin.getLayoutParams().height;
        }
        //fb登录开关-默认关闭
        String fLogin = Configs.getItem("fbLogin");
        if(fLogin.equals("1")){
            fblogin.setVisibility(View.VISIBLE);
            l.height = l.height+fblogin.getLayoutParams().height;
        }
        //appbazar登录开关-默认关闭--appbazar包打开登录
//        if(pub.startsWith("appbazar")){
//            appbazarlogin.setVisibility(View.VISIBLE);
//            l.height = l.height+appbazarlogin.getLayoutParams().height;
//        }
        loginlayout.setLayoutParams(l);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==cancle_id){//关闭
            //登录失败上报
            PhoneTool.submitSDKEvent("16","login_cancle");
            MySdkApi.getLoginCallBack().loginFail("login_cancle");
        }else if (id==hwlogin_id) {//huawei
            PhoneTool.submitSDKEvent("7","login_huawei");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),10);
        }else if (id==appbazarlogin_id) {//appbazar
            PhoneTool.submitSDKEvent("20","login_AppBazar");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),10);
        }else if (id==fblogin_id) {//fb
            PhoneTool.submitSDKEvent("21","login_fb");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),2);
        }
        else if (id==google_con_id) {
            PhoneTool.submitSDKEvent("4","login_google");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),3);
        }else if (id==vklogin_id) {
            PhoneTool.submitSDKEvent("6","login_vk");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),9);
            if(SkipUtil.getVkSdk() == null && SkipUtil.getVkIdSdk() == null){
                this.finish();
            }else{
                //防止重复点击
                Timer tim = new Timer();
                try {
                    tim.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            PhoneTool.disDialog();
                        }
                    },3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PhoneTool.onCreateDialog(this,"","");
            }
        }else if (id==fastlogin_id){//快速进入
            PhoneTool.submitSDKEvent("5","login_youke");
            HttpUtils.fastlogin(MySdkApi.getMact());
        }else if (id==emaillogin_id) {
            PhoneTool.submitSDKEvent("19","login_email");
            MySdkApi.chooseLogin(MySdkApi.getMact(), MySdkApi.getLoginCallBack(),11);
        }
        //vk登录时不能关闭
        if(id!=vklogin_id)this.finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            //登录失败上报
            PhoneTool.submitSDKEvent("16","login_cancle_KEYCODE_BACK");
            MySdkApi.getLoginCallBack().loginFail("login_cancle");
        }
        return super.onKeyDown(keyCode, event);
    }

}
