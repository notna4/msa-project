package com.example.alarmwars;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmPopupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, 12);
            }
        }

        // Set the dialog theme 
        setContentView(R.layout.activity_alarm_popup);

        // Prevent dismissing the popup via the back button
        setFinishOnTouchOutside(false);

        // Make it appear over other apps and lock screen
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Button to start the game!
        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPopupActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        // Snooze button functionality 
        Button snoozeButton = findViewById(R.id.snooze_button);
        snoozeButton.setOnClickListener(v -> {
            // Handle snoozing logic
            AlarmRing.stopAlarm(); // Stop the alarm
            finish(); // Close the popup
        });
    }
}
