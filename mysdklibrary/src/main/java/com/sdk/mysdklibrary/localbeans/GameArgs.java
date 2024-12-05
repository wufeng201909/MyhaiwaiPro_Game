package com.sdk.mysdklibrary.localbeans;

import android.os.Parcel;
import android.os.Parcelable;

public class GameArgs implements Parcelable {
	private String key = "";
	private String prefixx = "";// 文件前缀
	private String cpid = "";// cpid
	private String gameno = "";// 游戏编号
	private String name = "";// 游戏名字
	private String Publisher = "";

	public String getPublisher() {
		return Publisher;
	}

	public void setPublisher(String publisher) {
		Publisher = publisher;
	}

	private String self = "";// 自己数据

	private String account_id = "0";
	private String session_id = "0";

	private String customorderid = "";// 商户订单
	private String callbackurl = "www.baidu.com";// 游戏服务器回调
	private String sum = "0";// 充值金额
	private String desc = "0";// 商品描述

	/**
	 * 记录是否要升级 0 error 1 except 2 sv 3 bv 4 noneed 5 not knowed
	 */
	private int updateornot = -1;
	/**
	 * 游戏是否初始化
	 */
	private boolean init = false;

	public String getPrefixx() {
		return prefixx;
	}

	public void setPrefixx(String prefixx) {
		this.prefixx = prefixx;
	}

	public String getCpid() {
		return cpid;
	}

	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	public String getGameno() {
		return gameno;
	}

	public void setGameno(String gameno) {
		this.gameno = gameno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public int getUpdateornot() {
		return updateornot;
	}

	public void setUpdateornot(int updateornot) {
		this.updateornot = updateornot;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public String getCustomorderid() {
		return customorderid;
	}

	public void setCustomorderid(String customorderid) {
		this.customorderid = customorderid;
	}

	public String getCallbackurl() {
		return callbackurl;
	}

	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(key);
		dest.writeString(prefixx);
		dest.writeString(cpid);
		dest.writeString(gameno);
		dest.writeString(name);
		dest.writeString(self);
		dest.writeInt(updateornot);
		dest.writeValue(init);
		dest.writeString(account_id);
		dest.writeString(session_id);
		dest.writeString(customorderid);
		dest.writeString(callbackurl);
		dest.writeString(sum);
		dest.writeString(desc);
	}

	public static final Creator<GameArgs> CREATOR = new Creator<GameArgs>() {

		@Override
		public GameArgs createFromParcel(Parcel source) {
			GameArgs gameargs = new GameArgs();
			gameargs.key = source.readString();
			gameargs.prefixx = source.readString();
			gameargs.cpid = source.readString();
			gameargs.gameno = source.readString();
			gameargs.name = source.readString();
			gameargs.self = source.readString();
			gameargs.updateornot = source.readInt();
			gameargs.init = (Boolean) source.readValue(Boolean.class.getClassLoader());
			gameargs.account_id = source.readString();
			gameargs.session_id = source.readString();
			gameargs.customorderid = source.readString();
			gameargs.callbackurl = source.readString();
			gameargs.sum = source.readString();
			gameargs.desc = source.readString();
			return gameargs;
		}

		@Override
		public GameArgs[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GameArgs[size];
		}

	};

}
