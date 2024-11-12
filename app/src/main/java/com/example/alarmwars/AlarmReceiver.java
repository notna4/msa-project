package com.example.alarmwars;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the AlarmsActivity when the alarm goes off
        Intent alarmIntent = new Intent(context, AlarmsActivity.class);  // Start AlarmsActivity to show the alarm UI
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);

        // Optional: You can also show a notification if you prefer that over starting an activity
        showNotification(context);
    }

    private void showNotification(Context context) {
        // Notification logic to notify the user outside the app
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "alarm_channel";

        // Create notification channel for devices running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for when the user taps the notification
        Intent notificationIntent = new Intent(context, AlarmsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create the notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // Using the system alert icon
                .setContentTitle("Alarm Alert!")
                .setContentText("Tap to stop or snooze the alarm.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)  // The notification will be removed when tapped
                .setContentIntent(pendingIntent)  // Set the action to open AlarmsActivity
                .build();

        // Show the notification
        notificationManager.notify(1, notification);  // Notification ID is 1 (you can change this)
    }
}
