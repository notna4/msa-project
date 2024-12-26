package com.example.alarmwars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;

public class AlarmRing extends BroadcastReceiver {


    static Ringtone ringtone; // Keep a reference to stop the alarm later
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1234;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the popup activity to show the alarm control UI (Stop/Snooze)
        Intent popupIntent = new Intent(context, AlarmPopupActivity.class);
        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(popupIntent); // Start the popup activity

        // Play the alarm sound
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, alarmUri);
        }

        if (!ringtone.isPlaying()) {
            ringtone.play();
        }
    }

    // Stop the alarm
    public static void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

}
