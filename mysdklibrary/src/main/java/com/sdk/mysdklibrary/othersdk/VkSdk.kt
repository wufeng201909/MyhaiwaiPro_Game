package com.sdk.mysdklibrary.othersdk

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.sdk.mysdklibrary.Net.HttpUtils
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

class VkSdk {
    private lateinit var _con: Context
    private lateinit var authLauncher: ActivityResultLauncher<Collection<VKScope>>
    fun init(con: Application){

//        val appName = con.applicationInfo.loadLabel(con.packageManager).toString()
//        val appId = ResourceUtil.getString(con, "com_vk_sdk_AppId")
//        val clientSecret = ResourceUtil.getString(con, "vk_client_secret")
//        println("clientSecret-->$clientSecret")
//        // Specify an icon which would be shown on UI components
//        val icon = AppCompatResources.getDrawable(con, ResourceUtil.getDrawableId(con,"icon"))!!
//
//        val appInfo = SuperappConfig.AppInfo(
//                appName,
//                VK.getAppId(con).toString(),
//                HttpUtils.getAppVersion()
//        )
//
//        val config = SuperappKitConfig.Builder(con)
//                // VK ID settings
//                .setAuthModelData(clientSecret)
//                .setAuthUiManagerData(VkClientUiInfo(icon, appName))
//                .setLegalInfoLinks(
//                        serviceUserAgreement = "https://id.vk.com/terms",
//                        servicePrivacyPolicy = "https://id.vk.com/privacy"
//                )
//                .setApplicationInfo(appInfo)
////                .setUseCodeFlow(true)
//                .setSilentTokenExchanger(VkSilentTokenExchangerImpl(con))
//                .sslPinningEnabled(false)
//                .build()
//        SuperappKit.init(config)
//
//        VkClientAuthLib.addAuthCallback(authCallback)

        _con = con
        VK.addTokenExpiredHandler(tokenTracker)
    }
    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
            println("vk token expired")
            VK.clearAccessToken(_con)
        }
    }
    fun initSDK(act: Activity){
        authLauncher = VK.login(act as ComponentActivity) { result : VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    // User passed authorization
                    val userId = result.token.userId.toString()
                    println("result.token-userId->$userId")
                    HttpUtils.vkLogin_check(userId,result.token.accessToken)
                    act.finish()
                }
                is VKAuthenticationResult.Failed -> {
                    // User didn't pass authorization
                    val msg = result.exception.message
                    println("error-->$msg")
                }
            }
        }
    }

    fun loginOut(){
//        VkClientAuthLib.logout()
    }

    fun login(act: Activity){
        authLauncher.launch(arrayListOf())
//        authLauncher.launch(arrayListOf(VKScope.PHONE, VKScope.EMAIL))
//        VkFastLoginButton(act).showFastLoginDialog(null)
    }

    fun onDestroy(){
        VK.removeTokenExpiredHandler(tokenTracker)
//        VkClientAuthLib.removeAuthCallback(authCallback)
    }

//    private val authCallback = object : com.vk.auth.main.VkClientAuthCallback {
//        override fun onAuth(authResult: com.vk.auth.api.models.AuthResult) {
//            superappApi.users.sendGetUserMyInfo(
//                    VK.getAppId(
//                            MyApplication.context).toLong(),
//                    0
//            ).subscribe {
//                val info = it.getJSONArray("response")
//                if (info.length() > 0) {
//                    val firstUser = info.getJSONObject(0)
//                    println("firstUser-->$firstUser")
//                } else {
//                    println("No users found!")
//                }
//            }
//        }
//        override fun onLogout(logoutReason: LogoutReason) {
//            TODO("Your implementation")
//        }
//    }
}