package com.example.alarmwars;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class AlarmsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Trigger the alarm immediately for testing
        startAlarm();
    }

    // Start the alarm, showing a toast, playing a sound, and starting vibration
    private void startAlarm() {
        // Show a custom Toast message when the alarm rings
        Toast.makeText(this, "Cantam, cantam si-ncurajam\n" + "Echipa noastra favorita.\n" + "Ea este Poli, ea este Poli,\n" + "Echipa mult iubita.", Toast.LENGTH_LONG).show();

        // Initialize the ringtone
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

        // Initialize the vibrator system service
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Optional: Start vibration for 5 seconds
        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(5000);  // Vibrate for 5 seconds
        }

        // Show the Alarm Popup with options to snooze or stop
        showAlarmPopup();
    }

    // Show the popup dialog with options for snooze and stop
    private void showAlarmPopup() {
        // Create and show the popup dialog for snooze/stop actions
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alarm Started")
                .setMessage("Your alarm has started. Do you want to snooze or stop?")
                .setCancelable(false)
                .setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        snoozeAlarm();
                    }
                })
                .setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopAlarm();
                    }
                })
                .show();
    }

    // Stop the alarm sound and vibration
    private void stopAlarm() {
        // Stop the alarm sound
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        // Stop vibration
        if (vibrator != null) {
            vibrator.cancel();
        }

        // Show the stop message and finish the activity
        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity
    }

    // Method to snooze the alarm (e.g., snooze for 5 minutes)
    @SuppressLint("ScheduleExactAlarm")
    private void snoozeAlarm() {
        // Set the snooze time (e.g., 5 minutes)
        long snoozeTime = 5 * 60 * 1000;  // 5 minutes
        long triggerAtMillis = System.currentTimeMillis() + snoozeTime;

        // Set the alarm to trigger again after the snooze period
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);  // Intent to trigger AlarmReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the snooze alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);

        // Show the snooze message
        Toast.makeText(this, "Alarm Snoozed for 5 minutes", Toast.LENGTH_SHORT).show();

        // Stop the current alarm sound and vibration
        stopAlarm();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Clean up when the activity stops (stop the alarm and vibration)
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
