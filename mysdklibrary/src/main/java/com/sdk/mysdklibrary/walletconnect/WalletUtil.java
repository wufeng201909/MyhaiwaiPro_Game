package com.sdk.mysdklibrary.walletconnect;

import android.app.Activity;
import android.text.TextUtils;

import com.sdk.mysdklibrary.Tools.ResourceUtil;
import com.sdk.mysdklibrary.impl.WalletCallback;

import java.util.Collections;

public class WalletUtil {

    private static WalletUtil walletUtil;
    private WcV2 wc2;

    public static WalletUtil getInstance(){
        if(walletUtil == null) walletUtil = new WalletUtil();
        return walletUtil;
    }

    public void walletInit(Activity activity){
        //获取配置参数，钱包初始化
        String wcPeerMetaName = ResourceUtil.getString(activity,"wallet_connect_peer_meta_name");
        String wcPeerMetaUrl = ResourceUtil.getString(activity,"wallet_connect_peer_meta_url");
        String wcPeerMetaDescription = ResourceUtil.getString(activity,"wallet_connect_peer_meta_description");
        String wcPeerMetaIcon = ResourceUtil.getString(activity,"wallet_connect_peer_meta_icon");
        String wcPeerMetaProId = ResourceUtil.getString(activity,"wallet_connect_peer_meta_projectId");
        if(!TextUtils.isEmpty(wcPeerMetaProId))
            initWC(activity,wcPeerMetaName, wcPeerMetaUrl, wcPeerMetaDescription, wcPeerMetaIcon,wcPeerMetaProId);
    }

    //钱包初始化
    private void initWC(Activity activity,String name, String url, String description, String icon,String proId) {
        //wallet1.0
//        wcutils = new WcUtils(topic, bridgeUrl, activity);
//        wcutils.InitialSessionState(name, url, description, Collections.singletonList(icon));
        //wallet2.0
        if(wc2 == null) wc2 = new WcV2(activity);
        if(!wc2.getIsinit()) wc2.init(name, url, description, Collections.singletonList(icon), proId);
    }

    /**
     *  连接钱包
     * @param callback  回调
     * @param chainId   链id
     * @param onlyRequestLogin  是否只用于请求登录
     */
    public void connectWallet(WalletCallback callback, String chainId, boolean onlyRequestLogin){
        //wallet1.0
//        wcutils.openSocketAsync(callback, chainId,onlyRequestLogin);
        //wallet2.0
        if(wc2 != null)wc2.connectWallet(callback,chainId);
    }
    public void payWallet(String wallet_authData, String wallet_payData, String wallet_authTo, String wallet_payTo,String wallet_nonce){
        //wallet1.0
//        wcutils.pay(wallet_authData,wallet_payData,wallet_authTo,wallet_payTo,wallet_nonce);
        //wallet2.0
        if(wc2 != null)wc2.pay(wallet_payData,wallet_payTo,wallet_nonce);
    }

    //钱包签名
    public void signWallet(String nonce){
        //wallet1.0
//        wcutils.sign(nonce);
        //wallet2.0
        if(wc2 != null)wc2.sign(nonce);
    }
    //关闭连接
    public void closeConnect(){
        //wallet1.0
//        if(wcutils!=null) wcutils.closeConnect();
        //wallet2.0
        if(wc2!=null) wc2.closeConnect();
    }

}
