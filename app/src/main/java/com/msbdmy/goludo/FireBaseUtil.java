package com.msbdmy.goludo;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sdk.mysdklibrary.Tools.MLog;

import androidx.annotation.NonNull;

public class FireBaseUtil {


    private static String result;


    //订阅主题
    public static void  pushTopic(Activity activity, String channelCode){
        MLog.a("FireBaseUtil");

        FirebaseMessaging.getInstance().subscribeToTopic(channelCode)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MLog.a("pushTopic");
//                        String msg = activity.getString(R.string.msg_subscribed);
//                        String msg = activity.getString("");


                        if (!task.isSuccessful()) {
//                            msg = activity.getString(R.string.msg_subscribe_failed);
                            MLog.a("pushTopic--failed");
                        }

                        MLog.a("pushTopic--end");
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //取消主题
    public static void  unPushTopic(Activity activity, String channelCode){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(channelCode)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MLog.a("pushTopic");
//                        String msg = activity.getString(R.string.msg_subscribed);
//                        String msg = activity.getString("");


                        if (!task.isSuccessful()) {
//                            msg = activity.getString(R.string.msg_subscribe_failed);
                            MLog.a("pushTopic--failed");
                        }

                        MLog.a("pushTopic--end");
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    public static String  getFBToken(Activity activity){


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {

                            MLog.a( "Fetching FCM registration token failed==="+task.getException());
                            return ;
                        }

                        // Get new FCM registration token
                        result = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token[0]);

                        MLog.a("FireBaseUtil token---"+ result);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                });

        return result;
    }

    public static void delFBToken(Activity activity){
        FirebaseMessaging.getInstance().deleteToken();
    }
}
