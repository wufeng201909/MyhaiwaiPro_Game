package com.sdk.mysdklibrary.othersdk;

import android.app.Activity;
import android.text.TextUtils;

import com.ld.sdk.LDSdkManager;
import com.ld.sdk.core.bean.GameRoleInfo;
import com.ld.sdk.core.bean.LdGamePayInfo;
import com.ld.sdk.internal.LDCallback;
import com.ld.sdk.internal.LDException;
import com.ld.sdk.internal.LDExitCallback;
import com.ld.sdk.internal.LDLoginCallback;
import com.ld.sdk.internal.LDNotLoginException;
import com.ld.sdk.internal.LDPayCallback;
import com.sdk.mysdklibrary.MyApplication;
import com.sdk.mysdklibrary.MyGamesImpl;
import com.sdk.mysdklibrary.MySdkApi;
import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class LeidianSdk {
    private static Activity mAct;
    public static void initSDK(Activity activity) {
        mAct = activity;
        LDSdkManager.getInstance().init(activity, new LDCallback<Boolean>() {

            @Override
            public void done(Boolean status, @Nullable LDException e) {
                MLog.a("ld-init:"+status);
                if (e == null) {//使用e来判断成功还是失败
                    //成功：此时status=true
                    ldSilentLogin(activity);
                } else {
                    //失败:此时status=false，失败原因：e.toString()
                    MLog.a("ld-init-onError:msg:"+e.toString());
                }
            }
        });

    }

    private static void ldSilentLogin(Activity activity){
        /**
         * 静默登录：未登录或者登录接口失败仅回调给业务方，不做其他额外处理
         * 该方法登录成功后，需要游戏方手动调一次showFloatView方法来显示悬浮窗
         */
        LDSdkManager.getInstance().silentLogin(activity, new LDLoginCallback() {
            @Override
            public void onSuccess(String cpUserId, String cpToken) {
                //登录成功后，需要游戏方手动调一次showFloatView方法来显示悬浮窗
                LDSdkManager.getInstance().showFloatView(activity);
            }

            @Override
            public void onError(String error) {
                //error-失败原因
                MLog.a("ld-silentLogin-onError:msg:"+error);
            }

            @Override
            public void onLogout() {
                // SDK内部退出登录，需要游戏方处理返回到游戏登录界面
            }
        });

    }

    private static void loginSDK(Activity activity) {
        LDSdkManager.getInstance().showLoginView(activity, new LDLoginCallback() {
            @Override
            public void onSuccess(String cpUserId, String cpToken) {
                //登录成功,此处只做登录验证，不保存渠道登录信息，extend传0表示不保存渠道登录信息
                HttpUtils.othersdkLogin(cpUserId, cpToken,"0");
            }

            @Override
            public void onError(String error) {
                //登录失败：包含了取消登录（比如点击了登录窗口的关闭按钮，那error会返回:ld login cancel）
                //其中error代表具体的失败原因
                MLog.a("ld-showLoginView-onError:msg:"+error);
            }

            @Override
            public void onLogout() {
                // SDK内部退出登录，需要游戏方处理返回到游戏登录界面
                if(MySdkApi.getLoginCallBack()!=null)
                    MySdkApi.getLoginCallBack().LogoutSuccess();
            }
        });
    }

    private static void reportCpLogin(String userId){
        /**
         * 如果CP方有自己的登录体系，那么在CP方登录成功后需要调用以下方法来上报CP登录行为，帮助雷电渠道统计CP方的日活等信息
         * @param userId 游戏自身登录体系中的用户id
         */
        LDSdkManager.getInstance().reportCpLogin(userId, new LDCallback<Boolean>() {
            @Override
            public void done(Boolean aBoolean, @Nullable LDException e) {
                if (e == null) {//使用e来判断成功还是失败
                    //成功
                } else {
                    //失败原因：e.toString()
                    MLog.a("ld-reportCpLogin-done:msg:"+e.toString());
                }
            }
        });

    }

    public static void paySDK(Activity activity, String orderId, final String paynotifyurl,String extra1,String extra2) {
        String uid = MyGamesImpl.getSharedPreferences().getString("accountid","");
        OrderInfo orderInfo = MyApplication.getAppContext().getOrderinfo();
        LdGamePayInfo ldGamePayInfo = new LdGamePayInfo();
        ldGamePayInfo.tradeName = orderInfo.getProductname();          //页面展示的商品名称
        ldGamePayInfo.cpOrderId = orderId;          // cp订单id 字符串类型
        ldGamePayInfo.productId = extra1;           //产品id
        ldGamePayInfo.cpUserId = uid;               //cp用户id
        String currencyType = extra2.split("\\|")[0];
        BigDecimal priceBigDecimal;
        try{
            priceBigDecimal = new BigDecimal(extra2.split("\\|")[1]);
        }catch (Exception e){
            priceBigDecimal = new BigDecimal(orderInfo.getAmount());
        }
        long commodityPrice = priceBigDecimal.multiply(BigDecimal.valueOf(100)).longValue();
        ldGamePayInfo.currencyType = TextUtils.isEmpty(currencyType)?"USD":currencyType;    //当前游戏页面显示货币类型例如:TWD,USD,HKD等
        ldGamePayInfo.commodityPrice = commodityPrice;//当前游戏商品显示的价格，分为单位，long类型，例如如果是游戏显示0.99USD这里需要传99
        //该透传参数需要sdk_v2.2.0版本才支持，请注意更新sdk版本
        ldGamePayInfo.transparentParams = orderId;  //透传参数，该字段的值将在服务端SDK的支付成功的回调方法中返回
        LDSdkManager.getInstance().showChargeView(activity, ldGamePayInfo, new LDPayCallback() {
            @Override
            public void onSuccess(String userId, String orderId) {
                //支付成功,二次确认
                HttpUtils.purchaseCheck(orderId);
            }

            @Override
            public void onError(LDException e) {
                MLog.a("ld-showChargeView-onError:"+e.toString());
                MySdkApi.getMpaycallBack().payFail("onError:"+e.toString());
                if (e instanceof LDNotLoginException) {
                    //LDNotLoginException表示还没有登录，则可以在这里调用showLoginView方法来先打开登录页面，进行SDK的登录操作
                    loginSDK(activity);
                }
            }

            @Override
            public void onCancel() {
                //取消支付
                MySdkApi.getMpaycallBack().payFail("onCancel");
            }
        });
    }

    public static void submitRoleData(int operator, GameRoleBean gameRoleBean) {
//        if(operator==0 || operator==1){//创建角色和登录后上报
            reportCpLogin(MyGamesImpl.getSharedPreferences().getString("accountid",""));
            GameRoleInfo ldGameInfo = new GameRoleInfo();
            ldGameInfo.serverId = gameRoleBean.getGameZoneId();      // 服务器id
            ldGameInfo.serverName = gameRoleBean.getGameZoneName(); // 服务器名字
            ldGameInfo.roleId = gameRoleBean.getRoleId();     // 角色id
            ldGameInfo.roleName = gameRoleBean.getRoleName();     // 角色名字
            ldGameInfo.roleType = "0";     // 角色类型，例如：战士，魔法师，弓箭手
            ldGameInfo.level = gameRoleBean.getRoleLevel()+"";         // 等级，如果没有，可传固定字符串"0"
            ldGameInfo.money = gameRoleBean.getGameCoin()+""; // 游戏金币(使用现实货币能直接兑换的游戏货币)，如果没有，可传固定字符串"0"
            ldGameInfo.partyName = "0";    // 公会，如果没有，可传固定字符串"0"
            String roleCE = gameRoleBean.getRoleCE();
            ldGameInfo.powerNum = "-1".equals(roleCE)?0:Integer.parseInt(roleCE);     // 角色战斗力，如果没有，可传固定字符串"0"
            int vipL = gameRoleBean.getVipLevel();
            ldGameInfo.vipLevel = vipL==-1?0:vipL;         // vip等级，如果没有，可传固定字符串"0"
            LDSdkManager.getInstance().enterGame(mAct, ldGameInfo, new LDCallback<Boolean>() {
                @Override
                public void done(Boolean t, @Nullable LDException e) {
                    MLog.a("ld-enterGame-done:"+t);
                    if (e == null) {//使用e来判断成功还是失败
                        //成功
                    } else {
                        //失败原因：e.toString()
                        MLog.a("ld-enterGame-done:msg:"+e.toString());
                    }
                }
            });
//        }
    }

    public static void onResume(Activity act) {
        LDSdkManager.getInstance().onResume(act);
    }

    public static void onPause(Activity act) {
        LDSdkManager.getInstance().onPause(act);
    }

    public static void onDestroy(Activity act) {
        LDSdkManager.getInstance().unInit(act);
    }

    public static void exit(Activity act) {
        LDSdkManager.getInstance().showExitView(act, new LDExitCallback() {
            @Override
            public void done(boolean isExit) {
                if (isExit) {
                    //点了退出游戏的按钮，此时游戏方可选择退出游戏
                    act.finish();
                    System.exit(0);
                }
            }
        });

    }
}
