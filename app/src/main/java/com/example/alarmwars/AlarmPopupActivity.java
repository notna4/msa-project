package com.example.alarmwars;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmPopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the dialog theme (defined below in the styles)
        setContentView(R.layout.activity_alarm_popup);

        // Prevent dismissing the popup via the back button
        setFinishOnTouchOutside(false);

        // Make it appear over other apps and lock screen
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN); // Ensure full-screen popup

        // Stop button functionality
        Button stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(v -> {
            AlarmRing.stopAlarm(); // Stop the alarm
            finish(); // Close the popup
        });

        // Snooze button functionality (optional)
        Button snoozeButton = findViewById(R.id.snooze_button);
        snoozeButton.setOnClickListener(v -> {
            // Handle snoozing logic
            AlarmRing.stopAlarm(); // Stop the alarm
            // Optionally set another alarm for snoozing (e.g., set an alarm for a few minutes later)(to be added)
            finish(); // Close the popup
        });
    }
}
