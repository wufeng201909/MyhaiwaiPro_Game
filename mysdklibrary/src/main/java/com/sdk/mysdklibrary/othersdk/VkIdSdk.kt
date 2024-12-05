package com.sdk.mysdklibrary.othersdk

import android.app.Activity
import android.app.Application
import androidx.activity.ComponentActivity
import com.sdk.mysdklibrary.Net.HttpUtils
import com.sdk.mysdklibrary.Tools.PhoneTool
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail

class VkIdSdk {
    private lateinit var _act: ComponentActivity
    private lateinit var vkid: VKID
    fun init(con: Application){

    }
    fun initSDK(act: Activity){
        _act = act as ComponentActivity
        vkid = VKID(act)
    }
    fun login(act: Activity){
        vkid.authorize(_act,authCallback = object:VKID.AuthCallback{
            override fun onFail(fail: VKIDAuthFail) {
                println("error-->"+fail.description)
                PhoneTool.submitSDKEvent("11",fail.description)
            }

            override fun onSuccess(accessToken: AccessToken) {
                val token = accessToken.token
                val userId = accessToken.userID
                println("accessToken-userId->$userId")
                HttpUtils.vkLogin_check(userId.toString(),token)
                _act.finish()
            }

        })
    }
}