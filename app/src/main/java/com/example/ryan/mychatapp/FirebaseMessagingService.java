package com.example.ryan.mychatapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.media.RemotePlaybackClient;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Ryan on 3/11/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
//for API 26 and above
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

      // String notification_title = remoteMessage.getNotification().getTitle();
      //  String notification_body = remoteMessage.getNotification().getBody();


        //String click_action = remoteMessage.getNotification().getClickAction();

        String from_sender_id = remoteMessage.getData().get("from_sender_id").toString();

        String notification_title = remoteMessage.getData().get("title");
        String notification_body = remoteMessage.getData().get("body");
        //String click_action = remoteMessage.getNotification().getClickAction();
        String click_action =   remoteMessage.getData().get("click_action");


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.app_icon)
                    .setTicker("Hearty365")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(notification_title)
                    .setContentText(notification_body)
                    .setContentInfo("Info");

            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("visit_user_id",from_sender_id);


        // Create the PendingIntent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        notificationBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId = (int) System.currentTimeMillis();
            notificationManager.notify(mNotificationId, notificationBuilder.build());



//for API < 26
/*

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(notification_title)
                .setContentText(notification_body);

        int notificationId = (int)System.currentTimeMillis();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build()); */
    }

}
