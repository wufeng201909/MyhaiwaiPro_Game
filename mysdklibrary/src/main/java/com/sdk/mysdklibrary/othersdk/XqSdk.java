package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;

import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.MD5Util;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;
import com.smwl.smsdk.abstrat.SMInitListener;
import com.smwl.smsdk.abstrat.SMLoginListener;
import com.smwl.smsdk.abstrat.SMLoginOutListener;
import com.smwl.smsdk.abstrat.SMPayListener;
import com.smwl.smsdk.app.SMPlatformManager;
import com.smwl.smsdk.bean.PayInfo;
import com.smwl.smsdk.bean.RoleInfo;
import com.smwl.smsdk.bean.SMUserInfo;

import org.json.JSONObject;

public class XqSdk {
    private static String app_key = "xiaoqi_appkey";
    private static String guid = "";
    private static Activity mAct;

    public static void initSDK(Activity activity) {
        mAct = activity;
        SMPlatformManager.getInstance().init(activity, app_key, new SMInitListener() {
            @Override
            public void onSuccess() {
                System.out.println("---------Init-----onSuccess----");
            }

            @Override
            public void onFail(String s) {
                System.out.println("---------Init-----onFail----"+s);
            }
        });
    }

    public static void loginSDK(Activity activity,String type) {
        mAct = activity;
        SMPlatformManager.getInstance().login(activity, new SMLoginListener() {

            @Override
            public void onLogoutSuccess() {
                SMPlatformManager.getInstance().smExitCurrent();
                if(MySdkApi.getLoginCallBack()!=null)
                    MySdkApi.getLoginCallBack().LogoutSuccess();
            }

            @Override
            public void onLoginSuccess(SMUserInfo loginInfo) {
                // 客户端登录成功后，返回给客户端token，游戏客户端传token给自己服务器去小7sdk
                // 服务器做登录验证，登陆成功后，会返回给游戏guid，每一个小7通行证可以拥有至多10
                // 个子账号（guid），guid对应了游戏中的游戏账号
                String token = loginInfo.getTokenkey();
                HttpUtils.othersdkLogin("",token,"");
            }

            @Override
            public void onLoginFailed(String arg0) {
                // 登陆失败
                MySdkApi.getLoginCallBack().loginFail(arg0);

            }

            @Override
            public void onLoginCancell(String arg0) {
                // 登陆取消
                MySdkApi.getLoginCallBack().loginFail(arg0);
            }

        });
    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        // <!-- 注意银联支付官方文档要求必须是竖屏，横屏可能会崩溃 -测试环境下微信支付会失败->
        OrderInfo orderInfo =  MyApplication.getAppContext().getOrderinfo();
        String pro_name = orderInfo.getProductname();
        PayInfo payInfo = new PayInfo();
        payInfo.setExtends_info_data(orderId);
        payInfo.setGame_orderid(orderId);
        payInfo.setNotify_id("-1");
        payInfo.setGame_role_id(roleId);
        payInfo.setGame_role_name(roleName);
        payInfo.setGame_level(rolelevel);
        payInfo.setGame_price(extra2);
        payInfo.setGame_area(zoneName);
        payInfo.setSubject(pro_name);
        //5.2.2版本不用传guid，6.0需要传
        payInfo.setGame_guid(extra1);
        String price = "";
        String sign_str = "game_area=" + zoneName+"&game_guid="+extra1+"&game_orderid=" + orderId + "&game_price=" + price+ "&subject=" + pro_name
                + extra2;

        payInfo.setGame_sign(MD5Util.getMD5String(sign_str));

        SMPlatformManager.getInstance().pay(activity, payInfo, new SMPayListener() {
            @Override
            public void onPaySuccess(Object obj) {
                MLog.a("X7——pay", "onPaySuccess=obj----" + obj);
                HttpUtils.purchaseCheck(orderId);
            }

            @Override
            public void onPayFailed(Object obj) {
                // 支付失败
                MLog.a("X7——pay", "onPayFailed=obj----" + obj);
                MySdkApi.getMpaycallBack().payFail(obj.toString());
            }

            @Override
            public void onPayCancell(Object obj) {
                MLog.a("X7——pay", "onPayCancell=obj----" + obj);
                MySdkApi.getMpaycallBack().payFail(obj.toString());
            }
        });
    }

    private static String rolelevel = "0",roleId = "",roleName = "无",zoneId = "0",
            zoneName = "无",balance = "0",vip = "0",partyName = "",power = "-1",payment = "-1",stage = "-1",
            roleCTime = ""+System.currentTimeMillis()/1000;
    public static void submitRoleData(int operator, GameRoleBean gameRoleBean) {
        rolelevel = gameRoleBean.getRoleLevel()+"";
        rolelevel = ("".equals(rolelevel))?"0":rolelevel;
        roleId = gameRoleBean.getRoleId();
        roleName = gameRoleBean.getRoleName();
        zoneId = gameRoleBean.getGameZoneId();
        zoneId = ("".equals(zoneId))?"0":zoneId;
        zoneName = gameRoleBean.getGameZoneName();
        balance = gameRoleBean.getGameCoin()+"";
        balance = ("".equals(balance))?"0":balance;
        vip = gameRoleBean.getVipLevel()+"";
        vip = ("".equals(vip))?"0":vip;
        try {
            roleCTime = gameRoleBean.getRoleCTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            power = gameRoleBean.getRoleCE();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            payment = gameRoleBean.getRoleRechargeAmount();
            payment = "0".equals(payment)?"-1":payment;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            stage = gameRoleBean.getRoleStage();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        RoleInfo info = new RoleInfo();
        info.setGame_area(zoneName);
        info.setGame_area_id(zoneId);
        info.setGame_guid(guid);
        info.setGame_role_id(roleId);
        info.setGame_role_name(roleName);
        info.setRoleLevel(rolelevel);
        info.setRoleCE(power);
        info.setRoleStage(stage);
        info.setRoleRechargeAmount(payment);
        SMPlatformManager.getInstance().smAfterChooseRoleSendInfo(mAct, info);
    }

    public static void exit(Activity activity) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                SMPlatformManager.getInstance().exitApp(new SMLoginOutListener() {

                    @Override
                    public void loginOutSuccess() {
                        SMPlatformManager.getInstance().smExitCurrent();
                        activity.finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }

                    @Override
                    public void loginOutFail(String arg0) {

                    }

                    @Override
                    public void loginOutCancel() {

                    }
                });
            }
        });
    }

    public static void logout(Activity activity) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                SMPlatformManager.getInstance().logout();
            }
        });
    }
}
