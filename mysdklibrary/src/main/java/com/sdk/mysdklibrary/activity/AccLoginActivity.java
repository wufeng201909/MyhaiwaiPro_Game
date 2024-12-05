package com.sdk.mysdklibrary.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.ResourceUtil;

import androidx.annotation.Nullable;

public class AccLoginActivity extends Activity implements View.OnClickListener {
    int ed_acc_id,ed_pas_id,acc_login_id,change_loginway_id;
    EditText ed_acc,ed_pas;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lay_id = ResourceUtil.getLayoutId(this,"myths_accloginview");
        setContentView(lay_id);

        ed_acc_id = ResourceUtil.getId(this,"ed_acc");
        ed_pas_id = ResourceUtil.getId(this,"ed_pas");
        acc_login_id = ResourceUtil.getId(this,"acc_login");
        change_loginway_id = ResourceUtil.getId(this,"change_loginway");

        ed_acc = (EditText)findViewById(ed_acc_id);
        String acc= MyGamesImpl.getSharedPreferences().getString("myths_oldacc_name","");
        if(!"".equals(acc)){
            ed_acc.setText(acc);
        }
        ed_pas = (EditText)findViewById(ed_pas_id);
        TextView acc_login = (TextView)findViewById(acc_login_id);
        TextView change_loginway = (TextView)findViewById(change_loginway_id);

        acc_login.setOnClickListener(this);
        change_loginway.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==acc_login_id){//登录
            String acc = ed_acc.getText().toString();
            String pas = ed_pas.getText().toString();
            if("".equals(acc.trim())){
                int tip1 = ResourceUtil.getStringId(this,"myths_account_tips3");
                Toast.makeText(this,getResources().getString(tip1),Toast.LENGTH_SHORT).show();
                return;
            }else if("".equals(pas.trim())){
                int tip2 = ResourceUtil.getStringId(this,"myths_account_tips4");
                Toast.makeText(this,getResources().getString(tip2),Toast.LENGTH_SHORT).show();
                return;
            }
            HttpUtils.acclogin(this,acc,pas);

        }else if(id==change_loginway_id){//切换登录方式
            this.finish();
            MyGamesImpl.getInstance().openLogin(MySdkApi.getMact());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            MySdkApi.getLoginCallBack().loginFail("login_cancle");
        }
        return super.onKeyDown(keyCode, event);
    }
}
