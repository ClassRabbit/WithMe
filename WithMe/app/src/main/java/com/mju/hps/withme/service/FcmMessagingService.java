package com.mju.hps.withme.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mju.hps.withme.ChatActivity;
import com.mju.hps.withme.ChatMessage;
import com.mju.hps.withme.LoginActivity;
import com.mju.hps.withme.MainActivity;
import com.mju.hps.withme.R;
import com.mju.hps.withme.model.User;

import java.util.Map;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.i("MessagingService", "onMessageReceived");
//        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String id = data.get("data1");
        String name = data.get("data2");
        String time = data.get("data3");
        String text = data.get("data4");
//        chatMessage.setId(122);//dummy
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(name + " : " +text);
        chatMessage.setDate(time);
        if(id.equals(User.getInstance().getId())){
            chatMessage.setMe(true);
        }
        if(ChatActivity.handler == null){
            Log.e("handler", "null");
        }
        else {
            Log.e("handler", "created");
            ChatActivity.handler.sendMessage(Message.obtain(ChatActivity.handler, ChatActivity.MSG_CHAT_SUCCESS, chatMessage));
//            if(!id.equals(User.getInstance().getId())){
//                sendPushNotification(name + " : " +text);
//            }

        }
    }


    //푸쉬 알람 생성 함수
    private void sendPushNotification(String message) {
        Log.e("noti", message);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher) )
                .setContentTitle("Push Title ")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255,500,2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
