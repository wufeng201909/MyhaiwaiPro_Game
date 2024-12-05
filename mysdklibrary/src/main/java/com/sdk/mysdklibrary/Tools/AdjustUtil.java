package com.sdk.mysdklibrary.Tools;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;

public class AdjustUtil {

    private static AdjustUtil adjustUtil;
    public static AdjustUtil getInstance(){
        if(adjustUtil == null) adjustUtil = new AdjustUtil();
        return adjustUtil;
    }

    public void ADJSubmit(int type,String... param) {
        //判断若没有配置adjust参数则不执行下面代码
        if(TextUtils.isEmpty(Configs.getAppToken())) return;
        AdjustEvent ae = null;
        if (type==0){//激活
            SharedPreferences sharedPreferences = MyGamesImpl.getSharedPreferences();
            boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
            if(isFirstRun){
                ae = new AdjustEvent(Configs.getAppToken_activation());
                ae.addCallbackParameter("event","activation");
                ae.addCallbackParameter("accountid", MyApplication.getAppContext().getGameArgs().getAccount_id());
                ae.addCallbackParameter("imei", PhoneTool.getIMEI(MyApplication.context));
                ae.addCallbackParameter("pb", MyApplication.getAppContext().getGameArgs().getPublisher());
                ae.addCallbackParameter("gameid", MyApplication.getAppContext().getGameArgs().getCpid()+MyApplication.getAppContext().getGameArgs().getGameno());
                Adjust.trackEvent(ae);
                sharedPreferences.edit().putBoolean("isFirstRun", false).commit();
            }
            return;
        }else if (type==1){//初始化
            ae = new AdjustEvent(Configs.getAppToken_init());
            ae.addCallbackParameter("event","init");
        }else if (type==2){//登录
            ae = new AdjustEvent(Configs.getAppToken_login());
            ae.addCallbackParameter("event","login");
        }else if (type==3){//广告上报
            ae = new AdjustEvent(Configs.getAppToken_adReport());
            Double money = 0D;
            try{
                money = Double.parseDouble(param[0])/1000;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            ae.setRevenue(money, "USD");
            ae.addCallbackParameter("event","adReport");
            ae.addCallbackParameter("data",param[1]);
        }else if (type==4){//支付完成上报
            ae = new AdjustEvent(Configs.getAppToken_paySuccess());
            Double money = 0D;
            try{
                money = Double.parseDouble(param[1]);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            String order=param[0];
            ae.setOrderId(order);
            ae.setRevenue(money, "USD");
            ae.addCallbackParameter("event","paySuccess");
            ae.addCallbackParameter("orderId",order);
            ae.addCallbackParameter("money",param[1]);
        }else if (type==5){
            ae = new AdjustEvent(Configs.getPurchase_notVerified());
        }else if (type==6){
            ae = new AdjustEvent(Configs.getPurchase_failed());
        }else if (type==7){
            ae = new AdjustEvent(Configs.getPurchase_unknown());
        }
        if(ae != null){
            ae.addCallbackParameter("accountid", MyApplication.getAppContext().getGameArgs().getAccount_id());
            ae.addCallbackParameter("imei", PhoneTool.getIMEI(MyApplication.context));
            ae.addCallbackParameter("pb", MyApplication.getAppContext().getGameArgs().getPublisher());
            ae.addCallbackParameter("gameid", MyApplication.getAppContext().getGameArgs().getCpid()+MyApplication.getAppContext().getGameArgs().getGameno());
        }
        Adjust.trackEvent(ae);
    }
}
