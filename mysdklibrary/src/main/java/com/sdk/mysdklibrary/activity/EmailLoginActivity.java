package com.sdk.mysdklibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Tools.PhoneTool;
import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.Tools.ToastUtils;
import com.sdk.mysdklibrary.interfaces.EmailCodeCallBack;

import java.lang.ref.WeakReference;

public class EmailLoginActivity extends Activity implements View.OnClickListener{
    int cancle_id,em_acc_id,em_code_id,tv_getCode_id,em_login_id;
    EditText em_acc,em_code;
    private MyHandler myHandler = null;
    String type = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lay_id = ResourceUtil.getLayoutId(this,"myths_emaillogin");
        setContentView(lay_id);
        type = getIntent().getStringExtra("type");

        int title_id = ResourceUtil.getId(this,"myths_log_elogin");
        cancle_id = ResourceUtil.getId(this,"myths_cancle");
        em_acc_id = ResourceUtil.getId(this,"ed_acc");
        em_code_id = ResourceUtil.getId(this,"ed_pas");
        tv_getCode_id = ResourceUtil.getId(this,"tv_getCode");
        em_login_id = ResourceUtil.getId(this,"em_login");

        TextView title = findViewById(title_id);
        TextView cancle = findViewById(cancle_id);
        TextView tv_getCode = findViewById(tv_getCode_id);
        TextView em_login = findViewById(em_login_id);
        em_acc = findViewById(em_acc_id);
        em_code = findViewById(em_code_id);

        if("bind".equals(type)){
            title.setText(ResourceUtil.getString(this,"myths_account_bind_email"));
            em_login.setText(ResourceUtil.getString(this,"myths_account_bind_email"));
        }

        cancle.setOnClickListener(this);
        tv_getCode.setOnClickListener(this);
        em_login.setOnClickListener(this);

        myHandler = new MyHandler(this,tv_getCode);
        if(PhoneTool.getTime_seconds()>0){//判断是否正在倒计时并显示，避免短时间内多次点击获取验证码
            tv_getCode.setClickable(false);
            PhoneTool.setHandler(myHandler);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==cancle_id) {//关闭
            this.finish();
            if(!"bind".equals(type)) MySdkApi.getLoginCallBack().loginFail("login_em_cancle");
        }else if(id==tv_getCode_id){//获取验证码
            String acc = em_acc.getText().toString();
            if(TextUtils.isEmpty(acc)) return;
            MyGamesImpl.getInstance().getEmailCode(this, acc, new EmailCodeCallBack() {
                @Override
                public void getCodeResult(boolean isSuccess, String msg) {
                    if(isSuccess){
                        //倒计时60秒
                        view.setClickable(false);
                        PhoneTool.countdown(EmailLoginActivity.this,60,myHandler);
                    }else{
                        ToastUtils.Toast(msg);
                    }
                }
            });
        }else if(id==em_login_id){//登录
            String acc = em_acc.getText().toString();
            String code = em_code.getText().toString();
            if(TextUtils.isEmpty(acc) || TextUtils.isEmpty(code)) return;
            MyGamesImpl.getInstance().emailLogin(this,acc,code,"bind".equals(type));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            if(!"bind".equals(type)) MySdkApi.getLoginCallBack().loginFail("login_em_cancle");
        }
        return super.onKeyDown(keyCode, event);
    }

    static class MyHandler extends Handler {
        WeakReference<EmailLoginActivity> weakReference;
        TextView tv_Code;
        public MyHandler(EmailLoginActivity activity, TextView tv) {
            super(activity.getMainLooper());
            weakReference = new WeakReference<>(activity);
            tv_Code = tv;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(weakReference!=null && weakReference.get()!=null){
                switch (msg.what){
                    case 0:
                        String data = (int)msg.obj+"";
                        tv_Code.setText(data);
                        break;
                    case 1:
                        String str = ResourceUtil.getString(MySdkApi.getMact(),"myths_getCode");
                        tv_Code.setText(str);
                        tv_Code.setClickable(true);
                        break;
                }
            }
        }
    }
}

