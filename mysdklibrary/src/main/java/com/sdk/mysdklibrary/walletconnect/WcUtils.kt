package com.sdk.mysdklibrary.walletconnect

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.sdk.mysdklibrary.Tools.ToastUtils
import com.sdk.mysdklibrary.impl.AndroidDispatcherProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.sdk.mysdklibrary.impl.TextViewLogger
import com.sdk.mysdklibrary.impl.WalletCallback
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import walletconnect.DAppManager
import walletconnect.adapter.gson.type_adapter.JsonRpcMethodTypeAdapter
import walletconnect.adapter.gson.type_adapter.SocketMessageTypeAdapter
import walletconnect.adapter.moshi.MoshiAdapter
import walletconnect.core.DApp
import walletconnect.core.adapter.JsonAdapter
import walletconnect.core.cryptography.Cryptography.generateSymmetricKey
import walletconnect.core.cryptography.toHex
import walletconnect.core.requests.eth.EthTransaction
import walletconnect.core.session.Fresh
import walletconnect.core.session.SessionLifecycle
import walletconnect.core.session.callback.*
import walletconnect.core.session.model.InitialSessionState
import walletconnect.core.session.model.json_rpc.CustomRpcMethod
import walletconnect.core.session.model.json_rpc.EthRpcMethod
import walletconnect.core.session.model.json_rpc.JsonRpcMethod
import walletconnect.core.session_state.SessionStore
import walletconnect.core.session_state.model.ConnectionParams
import walletconnect.core.session_state.model.PeerMeta
import walletconnect.core.socket.Socket
import walletconnect.core.socket.model.SocketMessageType
import walletconnect.core.util.DispatcherProvider
import walletconnect.core.util.Logger
import walletconnect.requests.CustomRpcMethods
import walletconnect.requests.wallet.SwitchChain
import walletconnect.socket.scarlet.FlowStreamAdapter
import walletconnect.socket.scarlet.SocketManager
import walletconnect.socket.scarlet.SocketService
import walletconnect.store.file.FileSessionStore
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class WcUtils {
//    protected lateinit var logger: Logger
//    protected val dispatcherProvider: DispatcherProvider = AndroidDispatcherProvider()
//    private lateinit var _topic:String
//    private lateinit var _bridgeUrl:String
//    private lateinit var _symmetricKey:String
//    private lateinit var _con:Context
//    private lateinit var _initialSessionState: InitialSessionState
//    private lateinit var dApp: DApp
//    private val fragmentTag: String = javaClass.simpleName
//    protected lateinit var sessionLifecycle: SessionLifecycle
//
//    protected var approvedAddress: String? = null
//    protected var approvedChainId: Int? = null
//    private var targetChainId: Int? = null
//
//    private var onlyRequestLogin: Boolean? = null
//
//    private lateinit var walletCallback: WalletCallback
//    private lateinit var walletAuthData: String
//    private lateinit var walletPayData: String
//    private lateinit var walletAuthTo: String
//    private lateinit var walletPayTo: String
//    private var walletNonce: Long = 0
//    private lateinit var waitingTransactionHash: String
//    private var isConnectManually:Boolean = false//是否手动调起连接钱包
//
//    var connected:Boolean = false
//    var islogined:Boolean = false
//
//    var remoteName:String = ""
//    var nowStatus = Status.NONE
//
//    enum class Status {
//        PREPARE, APPROVE, PAY, NONE
//    }
//
//    constructor(topic:String,bridgeUrl:String,con:Context){
//        this._topic = topic
//        this._bridgeUrl = bridgeUrl
//        this._symmetricKey = generateSymmetricKey().toHex()
//        this._con = con
//    }
//    protected val connectionParams by lazy {
//        ConnectionParams(
//            topic = this._topic,
//            version = "1",
//            bridgeUrl = this._bridgeUrl,
//            symmetricKey = this._symmetricKey
//        )
//    }
//
//    public fun InitialSessionState(name:String,url:String,description:String,icons:List<String>){
//        val initialSessionState by lazy {
//            InitialSessionState(
//                connectionParams,
//                myPeerId = UUID.randomUUID().toString(),
//                myPeerMeta = PeerMeta(
//                    name = name,
//                    url = url,
//                    description = description,
//                    icons = icons
//                )
//            )
//        }
//        _initialSessionState = initialSessionState
//
//        logger = TextViewLogger()
//
//        dApp = createDApp(sessionStoreName = fragmentTag)
//        sessionLifecycle = dApp
//    }
//
//    public fun getDapp(): DApp {
//        return dApp
//    }
//
//    // region Factory
//    private fun createSocketService(url: String,
//                                    lifecycleRegistry: LifecycleRegistry
//    )
//            : SocketService {
//
//        val interceptor = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.HEADERS
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .callTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            // "https://bridge.walletconnect.org" -> i think BridgeServer responds with "missing or invalid socket data"
//            // "https://safe-walletconnect.gnosis.io" -> ping works fine
//            .pingInterval(4, TimeUnit.SECONDS)
//            .addNetworkInterceptor(interceptor)
//            .cache(null)
//            .build()
//
//        val webSocketFactory = okHttpClient.newWebSocketFactory(url)
//
//        val gson = GsonBuilder()
//            .registerTypeAdapter(SocketMessageType::class.java, SocketMessageTypeAdapter())
//            .registerTypeAdapter(JsonRpcMethod::class.java, JsonRpcMethodTypeAdapter())
//            .create()
//
//        val scarlet = Scarlet.Builder()
//            .webSocketFactory(webSocketFactory)
//            .addMessageAdapterFactory(GsonMessageAdapter.Factory(gson))
//            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
//            .backoffStrategy(
//                ExponentialBackoffStrategy(initialDurationMillis = 1_000L,
//                maxDurationMillis = 8_000L)
//            )
//            .lifecycle(lifecycleRegistry)
//            .build()
//
//        return scarlet.create(SocketService::class.java)
//    }
//
//    private fun createSocket()
//            : Socket {
//        val gson = GsonBuilder()
//            .registerTypeAdapter(SocketMessageType::class.java, SocketMessageTypeAdapter())
//            .registerTypeAdapter(JsonRpcMethod::class.java, JsonRpcMethodTypeAdapter())
//            .create()
//
//        return SocketManager(
//            socketServiceFactory = { url, lifecycleRegistry -> createSocketService(url, lifecycleRegistry) },
//            gson,
//            dispatcherProvider,
//            logger
//        )
//    }
//
//    private fun createJsonAdapter()
//            : JsonAdapter {
//        /*val gson = GsonBuilder()
//                .registerTypeAdapter(SocketMessageType::class.java, SocketMessageTypeAdapter())
//                .registerTypeAdapter(JsonRpcMethod::class.java, JsonRpcMethodTypeAdapter())
//                .serializeNulls()
//                .create()
//
//        return GsonAdapter(gson)*/
//
//        val moshi = Moshi.Builder()
//            .add(walletconnect.adapter.moshi.type_adapter.SocketMessageTypeAdapter())
//            .add(walletconnect.adapter.moshi.type_adapter.JsonRpcMethodTypeAdapter())
//            .addLast(KotlinJsonAdapterFactory())
//            .build()
//        return MoshiAdapter(moshi)
//    }
//
//    private fun createSessionStore(name: String)
//            : SessionStore {
//        //val sharedPrefs = requireContext().applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
//
//        /*return SharedPrefsSessionStore(
//                sharedPrefs,
//                dispatcherProvider,
//                logger
//        )*/
//        return FileSessionStore(
//            File(this._con.filesDir, "$name.json"),
//            dispatcherProvider,
//            logger
//        )
//    }
//
//    protected fun createDApp(sessionStoreName: String)
//            : DApp {
//        return DAppManager(
//            socket = createSocket(),
//            sessionStore = createSessionStore(sessionStoreName),
//            jsonAdapter = createJsonAdapter(),
//            dispatcherProvider,
//            logger
//        )
//    }
//
//    fun onSessionCallback(callbackData: CallbackData) {
////        viewLifecycleOwner.lifecycle.coroutineScope.launch(dispatcherProvider.ui()) {
////            val stateText = updateLatestState(callbackData)
////            if (stateText != null) {
////                binding.textCallbacks.text = stateText
////            }
//
//            when (callbackData) {
//                is SessionCallback -> {
//                    when (callbackData) {
//                        is SessionCallback.SessionRequested ->{
//                            println("---SessionRequested---")
//                        }
//                        is SessionCallback.LocalSessionStateUpdated -> {
//                            var imageMyLogo:String? = null
//                            var textMyId = ""
//                            var imageRemoteLogo:String? = null
////                            var remoteName:String = ""
//                            var remoteUrl:String = ""
//                            var remoteDes:String? = null
//                            var textRemoteId = ""
//                            var textChainId = ""
//                            var textAccounts = ""
//                            if (callbackData.sessionState == null) {
//                                approvedAddress = null
//                                approvedChainId = null
//                            } else {
//                                imageMyLogo =(callbackData.sessionState!!.myPeerMeta.icons?.firstOrNull())
//                                textMyId = "Me(${callbackData.sessionState!!.myPeerId.take(6)})"
//                                imageRemoteLogo =(callbackData.sessionState!!.remotePeerMeta.icons?.firstOrNull())
//                                remoteName = (callbackData.sessionState!!.remotePeerMeta.name)
//                                remoteUrl = (callbackData.sessionState!!.remotePeerMeta.url)
//                                remoteDes = (callbackData.sessionState!!.remotePeerMeta.description)
//                                textRemoteId = "Rem.(${callbackData.sessionState!!.remotePeerId.take(6)})"
//                                textChainId = "Chain:${callbackData.sessionState!!.chainId?.toString()}"
//                                textAccounts =
//                                    "Acc:${callbackData.sessionState!!.accounts?.joinToString { it.take(6) }}"
//                                approvedAddress = callbackData.sessionState!!.accounts?.firstOrNull()
//                                approvedChainId = callbackData.sessionState!!.chainId
//                                if(approvedChainId !=null){
//                                    if(onlyRequestLogin == true){//只用于登录
//                                        islogined = true
//                                        walletCallback.connectCallback(approvedAddress,approvedChainId)
//                                    }else{//支付时逻辑
//                                        val bd: Boolean = walletCallback.connectCallback(approvedAddress,approvedChainId)
//                                        if (bd) {
//                                            islogined = true
//                                            if (nowStatus == Status.NONE) {
//                                                nowStatus = Status.PREPARE
//                                            }
//                                        }else{
//                                            println("-----------------------close()--------------------")
//                                            close()
//                                            ToastUtils.Toast("Please switch to a supported chain")
//                                            // 状态更新回调
////                                            walletCallback.payCallback(
////                                                -10,
////                                                "Please switch to a supported chain in your wallet"
////                                            )
//                                        }
//                                    }
//                                }
//                            }
//                            println(imageMyLogo)
//                            println(textMyId)
//                            println(imageRemoteLogo)
//                            println("remoteName--->$remoteName")
//                            println("remoteUrl--->$remoteUrl")
//                            println("remoteDes--->$remoteDes")
//                            println(textRemoteId)
//                            println(textChainId)
//                            println(textAccounts)
//                            println(approvedAddress)
//                            println(approvedChainId)
//                        }
//                        is SessionCallback.SessionUpdated -> {
//                            approvedAddress = callbackData.accounts?.firstOrNull()
//                            approvedChainId = callbackData.chainId
//                            println("SessionUpdated---"+approvedAddress)
//                            println("SessionUpdated---"+approvedChainId)
//                            if(onlyRequestLogin == false){
//                                val bd: Boolean = walletCallback.connectCallback(approvedAddress,approvedChainId)
//                                if (bd) {
//                                    islogined = true
//                                    if (nowStatus == Status.NONE) {
//                                        nowStatus = Status.PREPARE
//                                    }
//                                } else {
//                                    // 状态更新回调
////                                    walletCallback.payCallback(
////                                        -10,
////                                        "Please switch to a supported chain in your wallet"
////                                    )
//                                }
//                            }
//                        }
//                        else -> {}
//                    }
//                }
//                is SocketCallback -> {
//                    when (callbackData) {
//                        SocketCallback.SocketConnecting -> {
//                            println("------------------SocketConnecting-----------------")
////                            binding.textTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
//                        }
//                        SocketCallback.SocketClosed -> {
//                            println("------------------SocketClosed-----------------")
//                            connected =false
//                            islogined =false
////                            binding.textTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                        }
//                        SocketCallback.SocketConnected -> {
//                            println("------------------SocketConnected-----------------")
//                            connected = true
//                            if(isConnectManually){//避免重复调起连接
//                                isConnectManually = false
//                                if(!islogined){
//                                    sendSessionRequest(targetChainId)
//                                } else{
//                                    walletCallback.connectCallback(approvedAddress,approvedChainId)
//                                }
//                            }
////                            binding.textTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
//                        }
//                        SocketCallback.SocketDisconnected -> {
//                            println("------------------SocketDisconnected-----------------")
//                            connected = false
////                            binding.textTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
//                        }
//                        is SocketCallback.SocketMessage -> {
//                            println("------------------SocketMessage-----------------"+callbackData.toString())
//                        }
//                    }
//                }
//                is RequestCallback -> {
//                    when (callbackData) {
//                        is RequestCallback.EthSignRequested -> {
//                            println("----------------EthSignRequested-----------------")
//                            triggerDeepLink2()
//                        }
//                        is RequestCallback.EthSignTypedDataRequested -> {
//                            println("----------------EthSignTypedDataRequested-----------------")
//                        }
//                        is RequestCallback.EthSignResponse -> {
//                            println("----------------EthSignResponse-----------------")
//                            walletCallback.signCallback(approvedAddress,callbackData.signature)
//                        }
//
//                        is RequestCallback.CustomRequested -> {
//
//                        }
//                        is RequestCallback.CustomResponse -> {
////                            if (callbackData.data.toString().contains("transactionHash")) {
////                                if (nowStatus == Status.APPROVE) {
////                                    nowStatus = Status.PAY
////                                    sendRequestAsync(walletPayData, walletPayTo)
////                                } else if (nowStatus == Status.PAY) {
////                                    ToastUtils.Toast("Payment completed,please return to the game")
////                                    nowStatus = Status.NONE
////                                    walletCallback.payCallback(0, waitingTransactionHash)
////                                }
////                            } else {
////                                // 等待 再次查询
////                                Thread.sleep(2000)
////                                getTransactionReceipt(waitingTransactionHash)
////                            }
//                        }
//
//                        is RequestCallback.EthSignTxRequested -> {
//
//                        }
//                        is RequestCallback.EthSignTxResponse -> {
//
//                        }
//                        is RequestCallback.EthSendRawTxRequested -> {
//                            println("----------------EthSendRawTxRequested-----------------")
//                        }
//                        is RequestCallback.EthSendRawTxResponse -> {
//                            println("----------------EthSendRawTxResponse-----------------")
//                        }
//                        is RequestCallback.EthSendTxRequested -> {
//                            println("----------------EthSendTxRequested-----------------")
//                            triggerDeepLink2()
//                        }
//                        is RequestCallback.EthSendTxResponse -> {
//                            println("----------------EthSendTxResponse-----------------$callbackData")
//                            waitingTransactionHash = callbackData.transactionHash.toString()
//                            if(nowStatus == Status.APPROVE){
////                                getTransactionReceipt(waitingTransactionHash)
//                            } else if (nowStatus == Status.PAY) {
////                                getTransactionReceipt(waitingTransactionHash)
//                                ToastUtils.Toast("Payment completed,please return to the game")
//                                nowStatus = Status.NONE
//                                walletCallback.payCallback(0, waitingTransactionHash)
//                            }
//
//                        }
//
//                        is RequestCallback.RequestRejected -> {
//                            println("----------------RequestRejected-----------------")
//                            walletCallback.payCallback(-1,callbackData.error.message)
//                        }
//                    }
//                }
//                is FailureCallback -> {
//                    println("----------------Failure-----------------")
//                    println("Failure:" + callbackData.failure.toString())
//                }
//            }
////        }
//    }
//
//    fun close() {
//        nowStatus = Status.NONE
//        connected = false
//        islogined = false
//
//        dApp.closeAsync(deleteLocal = true, deleteRemote = true,100, onClosed = fun(f:Fresh){
//            println("-----------------Fresh-----------------------")
//            dApp.openSocketAsync(_initialSessionState,
//                callback = ::onSessionCallback,
//                onOpened = null)
//        })
//    }
//
//    fun closeConnect() {
//        nowStatus = Status.NONE
//        connected = false
//        islogined = false
//
//        dApp.closeAsync(deleteLocal = true, deleteRemote = true,100, onClosed = fun(f:Fresh){
//            println("-----------------closeAsync-----------------------")
//        })
//    }
//
//    public fun openSocketAsync(
//            callback: WalletCallback,
//            chainId: Int?,
//            onrequestLogin: Boolean
//    ){
//        walletCallback = callback
//        targetChainId = chainId
//        onlyRequestLogin = onrequestLogin
//        if(!connected){
//            isConnectManually = true
//            dApp.openSocketAsync(_initialSessionState,
//                callback = ::onSessionCallback,
//                onOpened = null)
//        }else if(!islogined){
//            sendSessionRequest(chainId)
//        }else{
//            if(onlyRequestLogin == false) {//支付时逻辑
//                val b: Boolean = walletCallback.connectCallback(approvedAddress,approvedChainId)
//                if (!b) {
//                    println("-----------------------close()--------------------")
//                    close()
//                    ToastUtils.Toast("Please switch to a supported chain")
//                    // 状态更新回调
////                    walletCallback.payCallback(
////                        -10,
////                        "Please switch to a supported chain in your wallet"
////                    )
//                    return
//                }
//            }else{
//                walletCallback.connectCallback(approvedAddress,approvedChainId)
//            }
//        }
//
//    }
//
//    public fun pay(
//        wallet_authData: String,
//        wallet_payData: String,
//        wallet_authTo: String,
//        wallet_payTo: String,
//        wallet_nonce: String
//    ) {
//        walletAuthData = wallet_authData
//        walletPayData = wallet_payData
//        walletAuthTo = wallet_authTo
//        walletPayTo = wallet_payTo
//        walletNonce = wallet_nonce.toLong()
//        if(walletAuthData.isNotEmpty()){
//            nowStatus = Status.APPROVE
//            sendRequestAsync(walletAuthData,walletAuthTo)
//        }else{
//            nowStatus = Status.PAY
//            sendRequestAsync(walletPayData,walletPayTo)
//        }
//    }
//
//    fun switchChain(chainId: Int) {
//        dApp.sendRequestAsync(
//            CustomRpcMethods.SwitchEthChain,
//            data = listOf(SwitchChain("0x" + chainId.toHex())),
//            itemType = SwitchChain::class.java
//        )
//    }
//
//    fun sign(nonce: String) {
//        dApp.sendRequestAsync(
//            EthRpcMethod.PersonalSign,
//            data = listOf(nonce,approvedAddress.toString()),
//            itemType = String::class.java
//        )
//    }
//
//    public fun sendSessionRequest(chainId: Int?){
//        dApp.sendSessionRequest(chainId)
//        Timer().schedule(object :TimerTask(){
//            override fun run() {
//                println("-------triggerDeepLink---------")
//                triggerDeepLink()
//            }
//
//        },1000)
//        //小狐狸测试
////        _triggerDeepLinkDirect("metamask")
////        Timer().schedule(object :TimerTask(){
////            override fun run() {
////                println("-------triggerDeepLink-----metamask----")
////                dApp.sendSessionRequest(chainId)
////                Timer().schedule(object :TimerTask(){
////                    override fun run() {
////                        _triggerDeepLinkDirect("","io.metamask")
////                    }
////                },1000)
////            }
////        },3000)
//    }
//
//    public fun sendRequestAsync(walletData:String,walletTo:String){
//        if (!approvedAddress.isNullOrBlank()) {
//            dApp.sendRequestAsync(
//                EthRpcMethod.SendTransaction,
//                data = listOf(createTransaction(walletData,walletTo)),
//                itemType = EthTransaction::class.java)
//        }
//    }
//
//    fun getTransactionReceipt(transactionHash: String) {
//        ToastUtils.Toast("Please wait...")
//        dApp.sendRequestAsync(
//            CustomRpcMethod("eth_getTransactionReceipt"),
//            data = listOf(transactionHash),
//            itemType = String::class.java
//        )
//    }
//
//    private fun triggerDeepLink() {
//        val currentSessionState = dApp.getInitialSessionState() ?: return
//        try {
//            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentSessionState.connectionParams.toUri()))
//            this._con.startActivity(myIntent)
//        } catch (_: ActivityNotFoundException) {
//            ToastUtils.Toast("No application can handle this request. Please install a wallet app")
//        }
//    }
//
//    private fun triggerDeepLink2() {
//        println("-----------------------remoteName--------------------"+remoteName)
//        println("-----------------------_topic--------------------"+_topic)
//        var delay:Long = 100;
//        if(remoteName.contains("BitKeep",true)){
//            delay = 500
//        }
//        Timer().schedule(object :TimerTask(){
//            override fun run() {
//                _triggerDeepLink2()
//            }
//        },delay)
//    }
//
//    private fun _triggerDeepLink2() {
//        try {
//            val myIntent =
//                Intent(Intent.ACTION_VIEW, Uri.parse("${remoteName.lowercase()}:${_topic}@1"))
//            _con.startActivity(myIntent)
//        } catch (_: ActivityNotFoundException) {
////            ToastUtils.Toast("No application can handle this request. Please install a wallet app")
//        }
//    }
//
//    private fun _triggerDeepLinkDirect(name: String,pkg:String? = "") {
//        var name = if(TextUtils.isEmpty(name))"wc" else name
//        val currentSessionState = dApp.getInitialSessionState() ?: return
//        var uri = currentSessionState.connectionParams.toUri().replace("wc:","${name}:")
//        println("Uri--->$uri")
//        try {
//            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            if(!TextUtils.isEmpty(pkg))myIntent.setPackage(pkg)
//            myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK)
//            this._con.startActivity(myIntent)
//        } catch (_: ActivityNotFoundException) {
//            ToastUtils.Toast("No application can handle this request. Please install a wallet app")
//        }
//    }
//
//    private fun createTransaction(walletData:String,walletTo:String)
//            : EthTransaction {
//        return EthTransaction(
//            from = approvedAddress!!,
//            to = walletTo,
//            data = walletData,
//            chainId = if (approvedChainId == null) null else "0x" + approvedChainId!!.toHex(),
//
//            gas = null,
//            gasPrice = null,
//            gasLimit = null,
//            maxFeePerGas = null,
//            maxPriorityFeePerGas = null,
//
////            value = "0x" + BigInteger("100000000").toString(16), // 1_000_000_000_000_000_000L.toHex(),
//            value = null,
//            nonce = if (remoteName.contains("metamask",true)) null else ("0x" + walletNonce.toHex())
//        )
//    }
}

