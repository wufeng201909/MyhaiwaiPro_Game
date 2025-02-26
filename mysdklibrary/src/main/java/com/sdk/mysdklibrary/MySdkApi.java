package com.sdk.mysdklibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.sdk.mysdklibrary.Net.HttpUtils;
import com.sdk.mysdklibrary.Tools.FilesTool;
import com.sdk.mysdklibrary.Tools.MLog;
import com.sdk.mysdklibrary.Tools.XfUtils;
import com.sdk.mysdklibrary.interfaces.BindFBCallBack;
import com.sdk.mysdklibrary.interfaces.BindPhoneCallBack;
import com.sdk.mysdklibrary.interfaces.ChangePasswordCallBack;
import com.sdk.mysdklibrary.interfaces.CheckCodeCallBack;
import com.sdk.mysdklibrary.interfaces.EmailCodeCallBack;
import com.sdk.mysdklibrary.interfaces.InitCallBack;
import com.sdk.mysdklibrary.interfaces.LoginCallBack;
import com.sdk.mysdklibrary.interfaces.PayCallBack;
import com.sdk.mysdklibrary.interfaces.PhoneCodeCallBack;
import com.sdk.mysdklibrary.interfaces.ProductDetailsCallBack;
import com.sdk.mysdklibrary.interfaces.ShareCallBack;
import com.sdk.mysdklibrary.interfaces.UnBindSDKCallBack;
import com.sdk.mysdklibrary.localbeans.GameArgs;
import com.sdk.mysdklibrary.localbeans.GameRoleBean;
import com.sdk.mysdklibrary.localbeans.OrderInfo;
import com.sdk.mysdklibrary.othersdk.SkipUtil;
import com.sdk.mysdklibrary.payUtils.GoogleUtil;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;


public class MySdkApi {

    private static Activity mact = null;
    private static LoginCallBack mlogincallBack;
    private static BindFBCallBack mbindcallBack;
    private static PayCallBack mpaycallBack;
    private static PhoneCodeCallBack mphonecodecallback;
    private static UnBindSDKCallBack munbindsdkcallback;
    private static BindPhoneCallBack mbindphonecallback;
    private static CheckCodeCallBack mCheckCodeCallBack;
    private static ChangePasswordCallBack mChangePasswordCallBack;


    public static LoginCallBack getLoginCallBack() {
        return mlogincallBack;
    }

    public static BindFBCallBack getBindcallBack() {
        return mbindcallBack;
    }

    public static PhoneCodeCallBack getPhoneCodecallBack() {
        return mphonecodecallback;
    }

    public static UnBindSDKCallBack getUnBindSDKCallBack() {
        return munbindsdkcallback;
    }

    public static BindPhoneCallBack getBindPhoneCallBack() {
        return mbindphonecallback;
    }

    public static CheckCodeCallBack getCheckCodeCallBack() {
        return mCheckCodeCallBack;
    }

    public static ChangePasswordCallBack getChangePasswordCallBack() {
        return mChangePasswordCallBack;
    }

    public static PayCallBack getMpaycallBack() {
        return mpaycallBack;
    }

    public static Activity getMact() {
        return mact;
    }

    public static void setDebug(boolean isdebug){
        MLog.setDebug(isdebug);
    }
    public static void initSDK(final Activity context,String cpid,String gameid,String key, final InitCallBack callBack){
        mact = context;
        if(MyApplication.context == null)MyApplication.context = context;

        GameArgs gameargs = new GameArgs();
        String gamename = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        gameargs.setCpid(cpid);
        gameargs.setGameno(gameid);
        gameargs.setKey(key);
        gameargs.setName(gamename);
        gameargs.setPublisher(FilesTool.getPublisherString()[0]);

        MLog.s("service Publisher -------> " + gameargs.getPublisher());
        MLog.s("service cpidid -------> " + cpid);
        MLog.s("service gameid -------> " + gameid);
        MLog.s("service gamekey-------> " + key);
        MLog.s("service gamename------> " + gamename);
        MyApplication.getAppContext().setGameArgs(gameargs);

        MyGamesImpl.getInstance().initSDK(context, callBack);

        SkipUtil.othInit(context);
    }

    public static void onCreate(Activity act,Bundle savedInstanceState){
        System.out.println("---onCreate---"+act+"--"+savedInstanceState);
    }
    /**
     * activity onResume
     */
    public static void onResume(){
        MyGamesImpl.getInstance().onResume();
        SkipUtil.othOnResume(mact);
    }

    /**
     * activity onPause
     */
    public static void onPause(){
        SkipUtil.othOnPuse(mact);
    }

    public static void onStop(){

    }

    /**
     * activity onDestory
     */
    public static void onDestory(){
        MyGamesImpl.getInstance().onDestory();
        SkipUtil.othDestroy(mact);
        XfUtils.getInstance().dismiss();
    }

    public static void onNewIntent(Intent intent){
        System.out.println("---onNewIntent---");
        SkipUtil.othNewIntent(mact,intent);
    }

    /**
     * activity onActivityResult
     * @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        MyGamesImpl.getInstance().onActivityResult(requestCode, resultCode, data);
        SkipUtil.othActivityResult(mact,requestCode,resultCode,data);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        SkipUtil.onRequestPermissionsResult(activity,requestCode,permissions,grantResults);
    }

    /**
     * activity onSaveInstanceState google登录需要用到
     * @param outState
     */
    public static void onSaveInstanceState(Bundle outState){
     //   MythsGamesImpl.getInstance().onSaveInstanceState(outState);
    }

    /**
     * 在游戏Activity中的onBackPressed方法中调用
     * @param context
     */
    public static void onBackPressed(Activity context){
     //   MythsGamesImpl.getInstance().onBackPressed(context);
    }

    /**
     * 选择登录
     * @param context
     * @param callBack
     * @param mode//1-游客;2-facebook;3-google;4-apple;5-twitter;6-honor;7-wallet
     */
    public static void chooseLogin(Activity context,LoginCallBack callBack,int mode){
        mact = context;
        mlogincallBack = callBack;
        if (mode==1){
            HttpUtils.fastlogin(context);
        }else if (mode==2){
            MyGamesImpl.getInstance().openfacebookLogin(context,callBack,"");
        }else if (mode==3){
            MyGamesImpl.getInstance().opengoogleLogin(context,callBack,"");
        }else if (mode==4){
            MyGamesImpl.getInstance().openappleLogin(context,callBack);
        }else if (mode==5){
            MyGamesImpl.getInstance().openTwitterLogin(context,callBack);
        }else if (mode==6){//荣耀
            MyGamesImpl.getInstance().openHonorLogin(context,callBack);
        }else if (mode==7){//钱包登录
            MyGamesImpl.getInstance().openWalletLogin(context,callBack);
        }else if (mode==8) {//wemix登录
            MyGamesImpl.getInstance().wemixLogin(context,callBack);
        }else if (mode==9){//vk登录
            SkipUtil.VKLogin(context);
        }else if (mode==10) {//第三方登录 hw登录
            SkipUtil.othLogin(context,"");
        }else if (mode==11) {//email登录
            MyGamesImpl.getInstance().openEmailLogin(context,callBack,"");
        }

    }

    /**
     *自动登录
     * @param context
     * @param callBack
     */
    public static void autoLogin(Activity context,LoginCallBack callBack){
        mact = context;
        mlogincallBack = callBack;
        MyGamesImpl.getInstance().autoLogin(context);
    }

    /**
     * 登录接口，呼出登录界面
     * @param context
     * @param callBack
     */
    public static void openLogin(Activity context,final LoginCallBack callBack){
        mact = context;
        mlogincallBack = callBack;
        MyGamesImpl.getInstance().autoLogin(context);
//        MyGamesImpl.getInstance().openLogin(context);
    }

    public static void acclogin(final Activity act,String name,String password,LoginCallBack callBack) {
        mact = act;
        mlogincallBack = callBack;
        MyGamesImpl.getInstance().acclogin(act,name,password);
    }

    //type 1-Facebook  2-Google  3-apple  4-twitter 5-第三方渠道
    public static void bindSDK(Activity context, int type,LoginCallBack callBack){
        mlogincallBack = callBack;
        if (type==1){
            MyGamesImpl.getInstance().openfacebookLogin(context,callBack,"bind");
        }else if (type==2){
            MyGamesImpl.getInstance().opengoogleLogin(context,callBack,"bind");
        }else if (type==3){
            MyGamesImpl.getInstance().openappleLogin(context,callBack);
        }else if (type==4){
            MyGamesImpl.getInstance().openTwitterLogin(context,callBack);
        }else if (type==5){
            SkipUtil.othLogin(context,"bind");
        }

    }

    //type 1-Facebook  2-Google  3-apple  4-twitter
//    public static void bindSDK(Activity context, int type,BindFBCallBack callBack){
//        if (type==1){
//            bindFB(context,callBack);
//        }else if (type==2){
//            bindGoolge(context,callBack);
//        }else if (type==3){
//            bindApple(context,callBack);
//        }else if (type==4){
//
//        }
//    }

    public static void bindFB(Activity context, BindFBCallBack callBack){
        mbindcallBack = callBack;
        MyGamesImpl.getInstance().bindfacebook(context,callBack);
    }


    public static void bindGoolge(Activity context, BindFBCallBack callBack){
        mbindcallBack = callBack;
        MyGamesImpl.getInstance().bindGoolge(context,callBack);
    }

    public static void bindApple(Activity context, BindFBCallBack callBack){
        mbindcallBack = callBack;
        MyGamesImpl.getInstance().bindApple(context,callBack);
    }

    //退出SDK当前账号
    public static void logout(Activity act){
        SkipUtil.othLogout(act);
    }

    //返回键退出接口
    public static void exitApp(Activity act){
        SkipUtil.othQuit(act);
    }

    // 有效点击事件
    // 3秒内重复点击无效
    private static long lastClickTime;
    private static boolean isValidHits() {
        if (System.currentTimeMillis() - lastClickTime > 3000) {
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * 启动支付 -- 打开支付界面
     * @param act
     * @param callBack
     */
    public static void startPay(Activity act, OrderInfo orderinfo, PayCallBack callBack){
        if (!isValidHits()) return;
        mact = act;
        mpaycallBack = callBack;
        MyApplication.getAppContext().setOrderinfo(orderinfo);
        String pub = FilesTool.getPublisherStringContent();
        if(pub.contains("_hata_")){//hata游戏传入的是美分，这里需转成美元
            String money = MyApplication.getAppContext().getOrderinfo().getAmount();
            DecimalFormat df = new DecimalFormat("0.00");
            money = df.format(Float.parseFloat(money)/100).replace(",",".");
            MyApplication.getAppContext().getOrderinfo().setAmount(money);
        }
        if (pub.startsWith("rongyaosdk")){
            System.out.println("honorPurchaseSDK  startPay rongyaosdk"   );
//            HttpUtils.getOtherPayOrder("31", new GetorderCallBack() {
//                @Override
//                public void callback(String orderid, String feepoint, String payconfirmurl) {
//
//                    MLog.a("orderid==="+orderid+";feepoint="+feepoint+";payconfirmurl="+payconfirmurl);
//                    MyGamesImpl.getInstance().openOtherPay(context,orderid,feepoint,payconfirmurl);
//                }
//            });
        }else if(pub.startsWith("asdk")){
            SkipUtil.mySdkPay();
        }else{
            SkipUtil.otherSdkPay(act);
        }
    }

    /**
     * 打开个人中心
     */
    public static void openAccountCenter(Activity context) {
    //    MythsGamesImpl.getInstance().openAccountCenter(context);
    }

    /**
     * 打开客服中心
     */
    public static void openCustomerCenter(Activity context) {
   //     MythsGamesImpl.getInstance().openCustomerCenter(context);
    }

    /**
     * 0=create role 1=enter game 2=role level
     * @param operator
     * @param gameRoleBean
     */
    public static void submitRoleData(int operator, GameRoleBean gameRoleBean) {
        MyGamesImpl.getInstance().submitRoleData(operator, gameRoleBean);
        SkipUtil.submitRoleData(operator, gameRoleBean);
    }

    /**
     * 注册广播接收器，在添加Fb分享、邀请好友、点赞功能时在初始化SDK时调用
     * @param context
     */
    public static void registerBroadcastReceiver(Activity context){
//        MythsGamesImpl.getInstance().registerBroadcastReceiver(context);
//        MyGamesImpl.getInstance().registerBroadcastReceiver(context);
    }

    //FB邀请
    public static void facebookShare(Activity context,String title,String content,ShareCallBack shareCallBack){
        MyGamesImpl.getInstance().openfacebookshare(context,title,content,shareCallBack);
    }

    //FB分享图片
    public static void facebookContentShare(Activity act,Bitmap bitmap){
        MyGamesImpl.getInstance().facebookS(act,bitmap);
    }

    //FB分享链接
    public static void facebookContentShare(Activity act,String url){
        MyGamesImpl.getInstance().facebookSURL(act,url);
    }

    //FB分享图片+文字
    public static void facebookShareMedia(Activity act,Bitmap bitmap,String url){
        MyGamesImpl.getInstance().facebookMedia(act,bitmap,url);
    }

    //注销
    public static void loginoutOther(Activity act){
        MyGamesImpl.getInstance().loginoutOther(act);
    }

    /**
     * 隐藏悬浮窗
     */
    public static void hideFloat(){
    }

    /**
     * 显示悬浮窗
     */
    public static void showFloat(){
    }

    /** * facebook  普通统计事件 * @param eventName 事件名称 */
    public static void logFBEvent(String eventName,Bundle bundle){
        MyGamesImpl.getInstance().logFBEvent(eventName,bundle);
    }

    /** * FireBase 特殊事件上报 * @param eventName 一级事件名称 */
    public static void logFireBaseEvent(String eventName,Bundle bundle){
        MyGamesImpl.getInstance().logFireBaseEvent(eventName,bundle);
    }

    /** * FireBase 特殊事件上报 * @param eventName 一级事件名称 */
    public static void logFireBaseEventWithMap(String eventName, Map<String, Object> eventMap){
        MyGamesImpl.getInstance().logFireBaseEventWithMap(eventName,eventMap);
    }

    /** * 普通统计事件 * @param eventName 事件名称 * @param eventMap 事件map */
    public static void logEventWithMap(String eventName, Map<String, Object> eventMap){
        MyGamesImpl.getInstance().logEventWithMap(eventName,eventMap);
    }

    /** * FireBase 特殊事件上报 * @param eventName 一级事件名称 */
    public static void dataReport(String eventName, JSONObject json){
        MyGamesImpl.getInstance().dataReport(eventName,json);
    }

    /**
     * sendphonecode   发送验证码
     *
     */
    public static void getPhoneCode(Activity context,String areaCode,String phoneNum, PhoneCodeCallBack callBack){
        mphonecodecallback = callBack;
        MyGamesImpl.getInstance().getPhoneCode(context,areaCode,phoneNum,callBack);
    }

    public static void unBindSDK(Activity context, String areaCode,String phoneNum, String code,int loginType, UnBindSDKCallBack callBack){
        munbindsdkcallback = callBack;
        // 1-Facebook  2-Google  3-apple  4-twitter
        if (loginType==1){
            MyGamesImpl.getInstance().unBindSDK(context,areaCode,phoneNum,code,"facebook",callBack);
        }else if (loginType==2){
            MyGamesImpl.getInstance().unBindSDK(context,areaCode,phoneNum,code,"google",callBack);
        }else if (loginType==3){
//            MyGamesImpl.getInstance().openappleLogin(context,callBack);
        }else if (loginType==4){
//            MyGamesImpl.getInstance().openTwitterLogin(context,callBack);
        }
    }

    public static void bindPhone(Activity context,int type, String areaCode,String phoneNum, String code,String password, BindPhoneCallBack callBack){
        mbindphonecallback = callBack;
        //1---绑定手机号  2------更改手机号
        if(type==1){
            MyGamesImpl.getInstance().bindPhone(context,"",areaCode,phoneNum,code,password,callBack);
        }else if(type==2){
            MyGamesImpl.getInstance().bindPhone(context,"change",areaCode,phoneNum,code,password,callBack);
        }

    }

    public static void phoneLogin(Activity context,String areaCode,String phoneNum, String code,String password,boolean isfrist, LoginCallBack callBack){
        mlogincallBack = callBack;
        if(isfrist){
            //首次手机登录
            MyGamesImpl.getInstance().phoneLogin(context,areaCode,phoneNum,code,password,callBack);
        }else{
            //非首次手机登录
            MyGamesImpl.getInstance().phoneLogin2(context,areaCode,phoneNum,password,callBack);
        }
    }

    public static void phoneLogin(Activity context,String areaCode,String phoneNum, String code, LoginCallBack callBack){
        mlogincallBack = callBack;
        MyGamesImpl.getInstance().phoneLogin(context,areaCode,phoneNum,code,"",callBack);
    }

    /**
     * 获取邮箱验证码
     * @param context
     * @param email
     */
    public static void getEmailCode(Activity context, String email, EmailCodeCallBack callBack){
        MyGamesImpl.getInstance().getEmailCode(context,email,callBack);
    }

    /**
     * 邮箱登录（无界面）
     * @param context
     * @param email
     * @param code
     * @param callBack
     */
    public static void emailLogin(Activity context,String email,String code,LoginCallBack callBack){
        mlogincallBack = callBack;
        MyGamesImpl.getInstance().emailLogin(context,email,code,false);
    }

    public static void checkPhoneCode(Activity context, String areaCode,String phoneNum, String code, CheckCodeCallBack callBack){
        mCheckCodeCallBack = callBack;
        MyGamesImpl.getInstance().checkPhoneCode(context,areaCode,phoneNum,code,callBack);
    }

    public static void changePassword(Activity context,String areaCode, String phoneNum, String oldPassword, String newPassword, ChangePasswordCallBack callBack){
        mChangePasswordCallBack =callBack;
        MyGamesImpl.getInstance().changePassword(context,areaCode,phoneNum,oldPassword,newPassword,callBack);
    }

    public static void forgetPassword(Activity context, String areaCode, String phoneNum, String verifycode, String newPassword, ChangePasswordCallBack callBack){
        mChangePasswordCallBack =callBack;
        MyGamesImpl.getInstance().forgetPassword(context,areaCode,phoneNum,verifycode,newPassword,callBack);
    }

    public static void setfirebaseid(Activity context, String firebaseid){
        MyGamesImpl.getInstance().setfirebaseid(context,firebaseid);
    }

    public static void queryProductDetails(List<String> feepoints, ProductDetailsCallBack detailsCallBack){
        GoogleUtil.getInstance().queryProductDetails(feepoints,detailsCallBack);
    }
    public static com.android.billingclient.api.BillingClient getBillingClient(){
        return GoogleUtil.getInstance().getBillingClient();
    }
}
