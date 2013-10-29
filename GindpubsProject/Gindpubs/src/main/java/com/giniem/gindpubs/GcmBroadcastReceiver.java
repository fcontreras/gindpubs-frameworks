package com.giniem.gindpubs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Holland on 10-24-13.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(context, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(context, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                // Post notification of received message.
                sendNotification(context, "Received: " + extras.toString());
                Log.i(this.getClass().toString(), "Received: " + extras.toString());
            }
        }
    }

    private void sendNotification(Context context, String msg) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, GindActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
