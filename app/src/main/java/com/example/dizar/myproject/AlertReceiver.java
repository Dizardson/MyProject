package com.example.dizar.myproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import static android.content.Context.NOTIFICATION_SERVICE;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent , 0);
        Notification.Builder notif = new Notification.Builder(context).setSmallIcon(R.drawable.ic_launcher_foreground).setContentText(UsersActivity.TEXT)
                .setContentTitle(UsersActivity.TITLE).setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notif.build());
        }
}
