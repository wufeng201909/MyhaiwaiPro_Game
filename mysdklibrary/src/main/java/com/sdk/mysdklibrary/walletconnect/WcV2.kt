package com.sdk.mysdklibrary.walletconnect

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.sdk.mysdklibrary.Tools.ToastUtils
import com.sdk.mysdklibrary.impl.WalletCallback
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import java.util.*
import java.util.concurrent.TimeUnit

class WcV2 {
    private val TEST_NAMESPACE = "eip155"
    private val ETHEREUM_CHAIN_PREFIX = "eip155:"
    private val TEST_ETHEREUM_CHAIN = "eip155:1"
    private val TEST_ARBITRUM_CHAIN = "eip155:42161"
    private val TEST_AVALANCHE_CHAIN = "eip155:43114"
    private val WALLET_CONNECT_PROD_RELAY_URL = "relay.walletconnect.com"

    private var _con: Activity
    var isinit:Boolean = false
    private var ispayClick:Boolean = false
    private var connected:Boolean = false
    private var islogined:Boolean = false
    private var isConnectManually:Boolean = false//是否手动调起连接钱包

    private lateinit var walletCallback: WalletCallback
    private var remoteName:String = ""
    private var topic:String = ""
    private var acc:String = "0xB6d089aa7B8bD33475C5727dC8D8e395B81EF399"
    private var targetChainId: String = ""

    constructor(con: Activity){
        this._con = con
    }
    fun init(name:String,url:String,description:String,icons:List<String>,proId:String){
        val serverUrl = "wss://$WALLET_CONNECT_PROD_RELAY_URL?projectId=$proId"
        val appMetaData = Core.Model.AppMetaData(
                name = name,
                description = description,
                url = url,
                icons = icons,
                redirect = "oil-war-wc:/request" // Custom Redirect URI
        )
        CoreClient.initialize(
                relayServerUrl = serverUrl,
                connectionType = ConnectionType.MANUAL,
                application = this._con.application,
                metaData = appMetaData
        ){
            it.throwable.printStackTrace()
        }
        initClient()
    }

    private fun initClient(){
        val init = Sign.Params.Init(core = CoreClient)
        SignClient.initialize(init, onSuccess = {
            isinit = true
            println("SignClient.initialize---onSuccess")
            CoreClient.Relay.connect(fun (error){
                println("Relay.connect---")
                error.throwable.printStackTrace()
            })
        }) {
            error ->  println("initialize---error-->"+error.throwable.message)
            // Error will be thrown if there's an issue during initialization
        }

        val dappDelegate = object : SignClient.DappDelegate {
            override fun onSessionApproved(approvedSession: Sign.Model.ApprovedSession) {
                // Triggered when Dapp receives the session approval from wallet
                println("dappDelegate-----onSessionApproved")
                topic =  approvedSession.topic
                val data = approvedSession.metaData.toString()
                remoteName = approvedSession.metaData?.name.toString()
                remoteName = remoteName.split(" ")[0]
                val accsize = approvedSession.accounts.size.toString()
                acc = approvedSession.accounts[0]
                println("topic-->$topic")
                println("data-->$data")
                println("account-->$acc")
                islogined = true
                walletCallback.connectCallback(getWalletAcc(),null,getIntChainId())
            }

            override fun onSessionRejected(rejectedSession: Sign.Model.RejectedSession) {
                // Triggered when Dapp receives the session rejection from wallet
                println("dappDelegate-----onSessionRejected")
            }

            override fun onSessionUpdate(updatedSession: Sign.Model.UpdatedSession) {
                // Triggered when Dapp receives the session update from wallet
                println("dappDelegate-----onSessionUpdate")
                topic =  updatedSession.topic
            }

            override fun onSessionExtend(session: Sign.Model.Session) {
                // Triggered when Dapp receives the session extend from wallet
                println("dappDelegate-----onSessionExtend")
            }

            override fun onSessionEvent(sessionEvent: Sign.Model.SessionEvent) {
                // Triggered when the peer emits events that match the list of events agreed upon session settlement
                println("dappDelegate-----onSessionEvent")
            }

            override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
                // Triggered when Dapp receives the session delete from wallet
                println("dappDelegate-----onSessionDelete")
                islogined = false
                if(ispayClick){
                    walletCallback.payCallback(-1,"Disconnect")
                }
            }

            override fun onSessionRequestResponse(response: Sign.Model.SessionRequestResponse) {
                // Triggered when Dapp receives the session request response from wallet
                println("dappDelegate-----onSessionRequestResponse:"+response.result)
                val result_suc = response.result as? Sign.Model.JsonRpcResponse.JsonRpcResult
                val result_error = response.result as? Sign.Model.JsonRpcResponse.JsonRpcError
                if (result_suc != null) {
                    if(!ispayClick){
                        walletCallback.signCallback(getWalletAcc(),result_suc.result)
                    }else{
                        walletCallback.payCallback(0,result_suc.result)
                    }
                }else if(result_error != null){
                    if(ispayClick){
                        walletCallback.payCallback(-1,result_error.message)
                    }
                }
            }

            override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
                //Triggered whenever the connection state is changed
                println("dappDelegate-----onConnectionStateChange-->"+state.isAvailable)
                connected = state.isAvailable
                if(connected && isConnectManually){//避免重复调起连接
                    isConnectManually = false
                    if(!islogined){
                        signClientConnect()
                    } else{
                        walletCallback.connectCallback(getWalletAcc(),null,getIntChainId())
                    }
                }
            }

            override fun onError(error: Sign.Model.Error) {
                // Triggered whenever there is an issue inside the SDK
                println("dappDelegate-----onError:${error.throwable.message}")
            }
        }

        SignClient.setDappDelegate(dappDelegate)
    }

    fun connectWallet(callback: WalletCallback, chainId:String){
        println("connectWallet---")
        walletCallback = callback
        //chainId为空则直接返回null
        if(TextUtils.isEmpty(chainId)){
            walletCallback.connectCallback(null,null,null)
            return
        }
        targetChainId = chainId
        if(!connected){
            println("connectWallet---1")
            isConnectManually = true
            CoreClient.Relay.connect(fun (error){
                println("Relay.connect---")
                error.throwable.printStackTrace()
            })
        } else if(!islogined){
            println("connectWallet---2")
            signClientConnect()
        } else{
            println("connectWallet---3")
            walletCallback.connectCallback(getWalletAcc(),null,getIntChainId())
        }

    }

    private fun signClientConnect(){
        val namespace: String = TEST_NAMESPACE /*Namespace identifier, see for reference: https://github.com/ChainAgnostic/CAIPs/blob/master/CAIPs/caip-2.md#syntax*/

        val chains: List<String> = listOf(ETHEREUM_CHAIN_PREFIX+targetChainId)/*List of chains that wallet will be requested for*/
//        val TEST_CHAINS = listOf(TEST_ETHEREUM_CHAIN)

        val methods: List<String> = listOf()/*List of methods that wallet will be requested for*/
        val TEST_METHODS = listOf(
                "eth_signTransaction",
                "eth_sendTransaction",
                "personal_sign",
                "eth_signTypedData",
        )
        //"wc_sessionRequest"
        val events: List<String> = listOf()/*List of events that wallet will be requested for*/
        val TEST_EVENTS = listOf("chainChanged", "accountsChanged")

        val requiredNamespaces: Map<String, Sign.Model.Namespace.Proposal> = mapOf(namespace to Sign.Model.Namespace.Proposal(chains, TEST_METHODS, TEST_EVENTS)) /*Required namespaces to setup a session*/
        val optionalNamespaces: Map<String, Sign.Model.Namespace.Proposal> = mapOf(namespace to Sign.Model.Namespace.Proposal(chains, TEST_METHODS, TEST_EVENTS)) /*Optional namespaces to setup a session*/

        val pairing: Core.Model.Pairing = CoreClient.Pairing.create() { error ->
            throw IllegalStateException("Creating Pairing failed: ${error.throwable.stackTraceToString()}")
        }!!/*Either an active or inactive pairing*/
        println("pairing.uri----->"+pairing.uri)
//        val pairingParams = Core.Params.Pair(pairing.uri)
//        CoreClient.Pairing.pair(pairingParams) { error ->
//            println("CoreClient.Pairing.pair---error")
//            error.throwable.printStackTrace()
//        }
        val expiry = (System.currentTimeMillis() / 1000) + TimeUnit.SECONDS.convert(7, TimeUnit.DAYS)
        val properties: Map<String, String> = mapOf("sessionExpiry" to "$expiry")
        val connectParams = Sign.Params.Connect(requiredNamespaces, optionalNamespaces, properties,pairing)
        SignClient.connect(connectParams,
                onSuccess = {
                    println("SignClient.connect------onSuccess")
                    triggerDeepLink(pairing.uri)
                },
                onError = {
                    println("SignClient.connect------onError")
                    it.throwable.printStackTrace()
                }
        )

    }

    fun closeConnect(){
        println("closeConnect--")
        islogined = false;
        SignClient.disconnect(Sign.Params.Disconnect(topic),
            onSuccess = {
                println("closeConnect--onSuccess")
            },
            onError = {
                println("closeConnect--error"+it.throwable.message)
            }
        )
    }

    fun pay(wallet_payData: String,
            wallet_payTo: String,
            wallet_nonce: String){
        val params = getEthSendTransaction(getWalletAcc(),wallet_payTo,wallet_payData,wallet_nonce)
        val chainId = getWalletChainId()
        println("params-->$params")
        println("chainid-->$chainId")
        ispayClick = true;
        //这里要先跳转，否则会出现横竖屏切换界面显示异常的问题
        triggerDeepLink2()
        SignClient.request(
                Sign.Params.Request(
                        topic,
                        "eth_sendTransaction",
                        params,
                        chainId),
                fun (e:Sign.Model.SentRequest) {
                    println("request---SentRequest:$e")
                },fun (e:Sign.Model.Error){
                    println("request---Error:"+e.throwable.message)
        })
    }

    fun getWalletAcc():String{
        return acc.substring(acc.lastIndexOf(":")+1)
    }

    fun getWalletChainId():String{
        return acc.substring(0,acc.lastIndexOf(":"))
    }
    fun getIntChainId():String{
        return acc.substring(acc.indexOf(":")+1,acc.lastIndexOf(":"))
    }

    fun sign(msg:String){
        val params = getPersonalSignBody(msg,getWalletAcc())
        val chainId = getWalletChainId()
        println("params-->$params")
        println("chainid-->$chainId")
        ispayClick = false;
        //这里要先跳转，否则会出现横竖屏切换界面显示异常的问题
        triggerDeepLink2()
        SignClient.request(
                Sign.Params.Request(
                        topic,
                        "personal_sign",
                        params,
                        chainId),
                fun (e:Sign.Model.SentRequest) {
                    println("request---SentRequest:$e")
                },
                fun (e:Sign.Model.Error){
                    println("request---Error:"+e.throwable.message)
                }
        )
    }

    private fun triggerDeepLink(uri:String) {
        try {
            _con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (_: ActivityNotFoundException) {
            ToastUtils.Toast("No application can handle this request. Please install a wallet app")
        }
    }

    private fun triggerDeepLink2() {
        println("-----------------------remoteName--------------------"+remoteName)
        println("-----------------------topic--------------------"+topic)
        var delay:Long = 500;
//        if(remoteName.contains("BitKeep",true)){
//            delay = 500
//        }
        Timer().schedule(object :TimerTask(){
            override fun run() {
                _triggerDeepLink2()
            }
        },delay)
    }

    private fun _triggerDeepLink2() {
        try {
            val myIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("${remoteName.lowercase()}:${topic}@2"))
            _con.startActivity(myIntent)
        } catch (_: ActivityNotFoundException) {
            ToastUtils.Toast("No application can handle this request. Please install a wallet app")
        }
    }

    fun getPersonalSignBody(msg:String,account: String): String {
        var _msg = "My email is john@doe.com - ${System.currentTimeMillis()}"
        _msg = if(TextUtils.isEmpty(msg))_msg else msg
        val mmm = _msg.encodeToByteArray()
                .joinToString(separator = "", prefix = "0x") { eachByte -> "%02x".format(eachByte) }
        return "[\"$mmm\", \"$account\"]"
    }

    fun getEthSignBody(account: String): String {
        val msg = "My email is john@doe.com - ${System.currentTimeMillis()}".encodeToByteArray()
                .joinToString(separator = "", prefix = "0x") { eachByte -> "%02x".format(eachByte) }
        return "[\"$account\", \"$msg\"]"
    }

    fun getEthSendTransaction(account: String,to:String,data:String,nonce:String): String {
        return "[{\"from\":\"$account\",\"to\":\"$to\",\"data\":\"$data\",\"nonce\":\"$nonce\"}]"
//        return "[{\"from\":\"$account\",\"to\":\"$to\",\"data\":\"$data\",\"gasLimit\":\"$gasLimit\",\"gasPrice\":\"$gasPrice\",\"value\":\"$value\",\"nonce\":\"$nonce\"}]"
//        return "[{\"from\":\"$account\",\"to\":\"0x70012948c348CBF00806A3C79E3c5DAdFaAa347B\",\"data\":\"0x\",\"gasLimit\":\"0x5208\",\"gasPrice\":\"0x0649534e00\",\"value\":\"0x01\",\"nonce\":\"0x07\"}]"
    }

    fun getEthSignTypedData(account: String): String {
        return "[\"$account\",{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"chainId\",\"type\":\"uint256\"},{\"name\":\"verifyingContract\",\"type\":\"address\"}],\"Person\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"wallet\",\"type\":\"address\"}],\"Mail\":[{\"name\":\"from\",\"type\":\"Person\"},{\"name\": \"to\",\"type\":\"Person\"},{\"name\":\"contents\",\"type\":\"string\"}]},\"primaryType\":\"Mail\",\"domain\":{\"name\":\"Ether Mail\",\"version\":\"1\",\"chainId\":\"1\",\"verifyingContract\":\"0xCcCCccccCCCCcCCCCCCcCcCccCcCCCcCcccccccC\"},\"message\":{\"from\": {\"name\":\"Cow\",\"wallet\":\"0xCD2a3d9F938E13CD947Ec05AbC7FE734Df8DD826\"},\"to\":{\"name\":\"Bob\",\"wallet\":\"0xbBbBBBBbbBBBbbbBbbBbbbbBBbBbbbbBbBbbBBbB\"},\"contents\":\"Hello, Bob!\"}}]"
    }
}