//package com.sdk.mysdklibrary.othersdk
//
//import android.content.Context
//import android.util.Log
//import com.vk.api.sdk.VK
//import com.vk.auth.main.SilentAuthSource
//import com.vk.auth.main.VkFastLoginModifiedUser
//import com.vk.auth.main.VkSilentTokenExchanger
//import com.vk.silentauth.SilentAuthInfo
//import com.vk.superapp.api.core.SuperappApiCore
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.io.IOException
//
//class VkSilentTokenExchangerImpl(private val context: Context) : VkSilentTokenExchanger {
//    override fun exchangeSilentToken(
//            user: SilentAuthInfo, modifiedUser: VkFastLoginModifiedUser?, source: SilentAuthSource
//    ): VkSilentTokenExchanger.Result {
//        return try {
//            val response = callOnYourBackend(user.token, user.uuid)
//            VkSilentTokenExchanger.Result.Success(response.getString("access_token"), response.getLong("user_id"))
//        } catch (error: Exception) {
//            VkSilentTokenExchanger.Result.Error(error, "Network error!", error !is IOException)
//        }
//    }
//
//    /**
//     * Warning!
//     * The following code should be placed on your Backend.
//     *
//     * Method auth.exchangeSilentAuthToken should be called ONLY from your Backend
//     * with Service token from platform.vk.com.
//     *
//     * For more info, check documentation - https://platform.vk.com/?p=DocsDashboard&docs=tokens_access-token
//     */
//    private fun callOnYourBackend(
//            silentToken: String,
//            uuid: String
//    ): JSONObject {
//        var yourEndpoint = "https://api.vk.com/method/auth.exchangeSilentAuthToken"
//        val yourServiceToken = SuperappApiCore.anonymousTokenProvider?.getToken()
//        var postQuery = "access_token=${yourServiceToken}&token=${silentToken}&uuid=${uuid}&v=5.141"
//        val identifier = context.resources.getIdentifier("test_endpoint", "string", context.packageName)
//        if (identifier != 0) {
//            yourEndpoint = context.getString(identifier)
//            postQuery += "&silent_token=${silentToken}&app_id=${VK.getAppId(context)}"
//        }
//        Log.d("endpoint", yourEndpoint)
//        val request = Request.Builder()
//                .url(yourEndpoint)
//                .post(postQuery.toRequestBody())
//                .build()
//        val answer = OkHttpClient().newCall(request).execute().body!!.string()
//        return JSONObject(answer)
//                .getJSONObject("response")
//    }
//}