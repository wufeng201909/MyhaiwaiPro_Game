package com.sdk.mysdklibrary.impl

interface WalletCallback {
    public open fun connectCallback(address: String?,chainId: Int?,chainIdStr:String?=null): Boolean
    public open fun payCallback(code: Int, msg: String?)
    public open fun signCallback(address: String?,sign: String)
}