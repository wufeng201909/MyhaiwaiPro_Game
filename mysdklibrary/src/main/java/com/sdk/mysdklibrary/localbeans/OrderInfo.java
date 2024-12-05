package com.sdk.mysdklibrary.localbeans;

import org.json.JSONObject;

public class OrderInfo {
	public enum PayType{
		Googlepay(28,false),
		Payermax(31,true),
		Walletpay(32,true),
		Pingtaipay(33,true),
		Xsollapay(34,false),
		Dlocalpay(35,true);

		private int value;

		PayType(int value,boolean isAnyAmount){
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public enum UseCase{
		Default(0,"默认值"),
		/**
		 * 兑入-ExchangeToToken
		 */
		FT_ExchangeToToken(1,"兑入"),
		/**
		 * 兑出-ExchangeToItem
		 */
		FT_ExchangeToItem(2,"兑出"),
		/**
		 * NFT exchange of the game goods
		 */
		NFT_ExchangeToToken(3,"兑入"),
		/**
		 * NFT's in-game currency exchange
		 */
		NFT_ExchangeToItem(4,"兑出");

		private int value;
		UseCase(int value,String type) {this.value = value;}
		public int getValue() {
			return value;
		}
	}

	private String transactionId="";   //交易流水
	private String amount="";			//金额
	private String productname="";        //道具
	private String feepoint="";             //计费点
	private JSONObject extrainfo=new JSONObject();      //客户端支付时间
	private String payurl="";      //客户端支付回调地址
	private boolean isAnyAmount=false;      //是否任意金额支付
	private PayType[] payTypes= {};      //支付方式集合
	private UseCase useCase = UseCase.Default;	//兑入/兑出

	public OrderInfo(){
		
	}
	
	public OrderInfo(String transactionId, String amount,String product_name,String fee_point, JSONObject extra_info,String pay_url,boolean is_anyAmount,UseCase ucase){
		this.transactionId = transactionId;
		this.amount = amount;
		this.productname = product_name;
		this.feepoint = fee_point;
		this.extrainfo = extra_info;
		this.payurl = pay_url;
		this.isAnyAmount = is_anyAmount;
		this.useCase = ucase;
	}


	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String product_name) {
		this.productname = product_name;
	}

	public String getFeepoint() {
		return feepoint;
	}

	public void setFeepoint(String fee_point) {
		this.feepoint = fee_point;
	}

	public JSONObject getExtraInfo() {
		return extrainfo;
	}

	public void setExtraInfo(JSONObject extra_info) {
		if (extra_info!=null) this.extrainfo = extra_info;
	}

	public void setPayurl(String payurl) {
		this.payurl = payurl;
	}

	public String getPayurl() {
		return payurl;
	}

	public boolean isAnyAmount() {
		return isAnyAmount;
	}

	public void setAnyAmount(boolean anyAmount) {
		isAnyAmount = anyAmount;
	}

	public String getPayTypes() {
		String types = "";
		for (int i=0;i<payTypes.length;i++) {
			types = types + payTypes[i].getValue() + ((i<payTypes.length-1)?",":"");
		}
		return types;
	}

	public void setPayTypes(PayType[] payTypes) {
		this.payTypes = payTypes;
	}

	public int getUseCase() {
		return this.useCase.getValue();
	}

	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}
}
