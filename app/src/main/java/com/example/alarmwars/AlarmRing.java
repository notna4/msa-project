package com.example.alarmwars;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

public class AlarmRing extends BroadcastReceiver {

    private MediaPlayer alarmMediaPlayer;
    private Vibrator vibrator;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(final Context context, Intent intent) {

        // Show a custom Toast message when the alarm rings (can be removed or customized)
        Toast.makeText(context, "Cantam, cantam si-ncurajam\n" + "Echipa noastra favorita.\n" + "Ea este Poli, ea este Poli,\n" + "Echipa mult iubita.", Toast.LENGTH_LONG).show();

        // Initialize the vibrator system service
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        // Create and play the ringtone sound
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        if (vibrator != null) {
            vibrator.vibrate(5000);  // Vibrate for 5 seconds
        }

        // Show the dialog to let the user stop or snooze the alarm
        showAlarmPopup(context);
    }

    // Show the Alarm Popup for Stop and Snooze actions
    private void showAlarmPopup(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alarm Started")
                .setMessage("Your alarm has started. Do you want to snooze or stop?")
                .setCancelable(false)
                .setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call snoozeAlarm() when snooze is pressed
                        snoozeAlarm(context);
                    }
                })
                .setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call stopAlarm() when stop is pressed
                        stopAlarm(context);
                    }
                })
                .show();
    }

    // Method to stop the alarm
    private void stopAlarm(Context context) {
        // Cancel the alarm using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);  // Replace with your receiver class
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        // Stop the alarm sound
        if (alarmMediaPlayer != null && alarmMediaPlayer.isPlaying()) {
            alarmMediaPlayer.stop();
            alarmMediaPlayer.release();
        }

        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }

        Toast.makeText(context, "Alarm Stopped", Toast.LENGTH_SHORT).show();
    }

    // Method to snooze the alarm (e.g., snooze for 5 minutes)
    @SuppressLint("ScheduleExactAlarm")
    private void snoozeAlarm(Context context) {
        // Set snooze time (5 minutes in milliseconds)
        long snoozeTime = 5 * 60 * 1000;  // 5 minutes
        long triggerAtMillis = System.currentTimeMillis() + snoozeTime;

        // Set the new alarm for snooze
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);  // Replace with your receiver class
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

        Toast.makeText(context, "Alarm Snoozed for 5 minutes", Toast.LENGTH_SHORT).show();
        stopAlarm(context);  // Stop the current alarm sound and vibration
    }

    // This is the receiver that will handle the alarm intent when it is triggered
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Trigger the alarm activity or perform any actions when the alarm is fired
            Intent alarmIntent = new Intent(context, AlarmRing.class);  // This starts your alarm ring activity
            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(alarmIntent);
        }
    }
}