package com.example.alarmwars;

import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FunctionalAlarmActivity extends AppCompatActivity {
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private ToggleButton mondayToggleButton;
    private ToggleButton tuesdayToggleButton;
    private Map<String, CheckBox> emailCheckBoxes = new HashMap<>(); // To store email and corresponding checkbox

    private ToggleButton wednesdayToggleButton;
    private ToggleButton thursdayToggleButton;
    private ToggleButton fridayToggleButton;
    private ToggleButton saturdayToggleButton;
    private ToggleButton sundayToggleButton;

    private String alarmId;
    private TextView saveBtn;
    private DatabaseReference databaseRef;

    private DatabaseReference playersRef;

    private LinearLayout playersList;

    private Switch isMyAlarmActive;
    private TextView myEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionalalarm);

        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        TextView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish()); // Go back to the previous activity

        saveBtn = findViewById(R.id.saveBtn);

        // Get the alarmId passed from the previous activity
        alarmId = getIntent().getStringExtra("alarmId");

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms");

        // Fetch the existing alarm details from Firebase
        fetchAlarmData();

        // Set onClickListener for the Save button
        saveBtn.setOnClickListener(v -> updateAlarmTime());

        mondayToggleButton = findViewById(R.id.monToggle);
        tuesdayToggleButton = findViewById(R.id.tueToggle);
        wednesdayToggleButton = findViewById(R.id.wedToggle);
        thursdayToggleButton = findViewById(R.id.thuToggle);
        fridayToggleButton = findViewById(R.id.friToggle);
        saturdayToggleButton = findViewById(R.id.satToggle);
        sundayToggleButton = findViewById(R.id.sunToggle);

        setOpacityOnToggle(mondayToggleButton);
        setOpacityOnToggle(tuesdayToggleButton);
        setOpacityOnToggle(wednesdayToggleButton);
        setOpacityOnToggle(thursdayToggleButton);
        setOpacityOnToggle(fridayToggleButton);
        setOpacityOnToggle(saturdayToggleButton);
        setOpacityOnToggle(sundayToggleButton);

        playersList = findViewById(R.id.playersList); // This is your LinearLayout
        playersRef = FirebaseDatabase.getInstance().getReference("members");
        myEmail = findViewById(R.id.myEmail);
        myEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        isMyAlarmActive = findViewById(R.id.isAlarmActive);

        fetchAndPopulateMembers();
    }

    private void fetchAndPopulateMembers() {
        // Get reference to the "members" node
        DatabaseReference playersRef = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms").child(alarmId).child("members");

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Get current user's email

        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("Database", "Data exists: " + dataSnapshot.toString()); // Debug log

                    DataSnapshot trueMembersSnapshot = dataSnapshot.child("true");
                    DataSnapshot falseMembersSnapshot = dataSnapshot.child("false");

                    // Get reference to the container LinearLayout (playersList)
                    LinearLayout playersList = findViewById(R.id.playersList); // Ensure this LinearLayout exists in your XML

                    // Clear any previous views in the container
                    playersList.removeAllViews();

                    // Iterate through the "true" members and add them to the layout
                    for (DataSnapshot emailSnapshot : trueMembersSnapshot.getChildren()) {
                        String email = emailSnapshot.getValue(String.class);
                        addMemberToLayout(playersList, email, true, email.equals(currentUserEmail));
                    }

                    // Iterate through the "false" members and add them to the layout
                    for (DataSnapshot emailSnapshot : falseMembersSnapshot.getChildren()) {
                        String email = emailSnapshot.getValue(String.class);
                        addMemberToLayout(playersList, email, false, email.equals(currentUserEmail));
                    }

                } else {
                    Toast.makeText(FunctionalAlarmActivity.this, "No members found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FunctionalAlarmActivity.this, "Failed to load members", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMemberToLayout(LinearLayout playersList, String email, boolean isChecked, boolean isEditable) {
        // Create a new LinearLayout for each member
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Get current user's email

        if (!email.equals(currentUserEmail)) {
            LinearLayout memberLayout = new LinearLayout(this);
            memberLayout.setOrientation(LinearLayout.HORIZONTAL);
            memberLayout.setPadding(10, 10, 10, 10); // Optional padding for spacing between members

            // Create and configure the TextView for the email
            TextView emailTextView = new TextView(this);
            emailTextView.setText(email);

            // Set the custom font (Nunito_Bold)
            Typeface nunitoBold = ResourcesCompat.getFont(this, R.font.nunito_bold);
            emailTextView.setTypeface(nunitoBold);
            emailTextView.setTextSize(18);
            emailTextView.setPadding(50, 10, 10, 10); // Adjust padding as needed
            emailTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Create and configure the CheckBox for the member's status
            CheckBox checkBox = new CheckBox(this);
            checkBox.setChecked(isChecked);
            checkBox.setEnabled(isEditable); // Enable only if it matches the current user's email

            // Store the checkbox in the map with the email as the key
            emailCheckBoxes.put(email, checkBox);

            // Apply padding to the CheckBox as well
            checkBox.setPadding(10, 10, 10, 10); // Adjust padding as needed


            // Add the TextView and CheckBox to the member layout
            memberLayout.addView(emailTextView);
            memberLayout.addView(checkBox);

            // Add the member layout to the container
            playersList.addView(memberLayout);
        } else {
            isMyAlarmActive.setChecked(isChecked);
        }

    }

    private void setOpacityOnToggle(ToggleButton toggleButton) {
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If checked, set full opacity (1.0f)
                buttonView.setAlpha(1.0f);
            } else {
                // If unchecked, set lower opacity (e.g., 0.5f)
                buttonView.setAlpha(0.5f);
            }
        });

        // Set initial opacity based on current state (checked or unchecked)
        if (toggleButton.isChecked()) {
            toggleButton.setAlpha(1.0f);
        } else {
            toggleButton.setAlpha(0.5f);
        }
    }


    private void fetchAlarmData() {
        if (alarmId == null || alarmId.isEmpty()) {
            Toast.makeText(this, "Invalid alarm ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to the specific alarm using the alarmId
        databaseRef.child(alarmId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if "daysToRing" is an ArrayList or String
                    Object daysToRingObj = dataSnapshot.child("daysToRing").getValue();
                    if (daysToRingObj instanceof ArrayList) {
                        // If it's an ArrayList, cast it properly
                        ArrayList<String> daysToRing = (ArrayList<String>) daysToRingObj;

                        // Iterate through the list to visually mark the days as selected on your UI
                        for (String day : daysToRing) {
                            selectDayInUI(day);  // Mark the day as checked
                        }
                    } else if (daysToRingObj instanceof String) {
                        // If it's a String, split the days and handle them
                        String daysToRingStr = (String) daysToRingObj;
                        String[] daysArray = daysToRingStr.split(",");
                        for (String day : daysArray) {
                            selectDayInUI(day);  // Mark each day as checked
                        }
                    }

                    // Fetch the existing alarm time data (hour and minute)
                    Long hourLong = dataSnapshot.child("hour").getValue(Long.class);
                    Long minuteLong = dataSnapshot.child("minutes").getValue(Long.class);

                    if (hourLong != null && minuteLong != null) {
                        // Convert Long to int and set the time in the TimePicker
                        int hour = hourLong.intValue();  // Convert Long to int
                        int minute = minuteLong.intValue();  // Convert Long to int

                        alarmTimePicker.setHour(hour);
                        alarmTimePicker.setMinute(minute);
                    }
                } else {
                    Toast.makeText(FunctionalAlarmActivity.this, "Alarm not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FunctionalAlarmActivity.this, "Failed to load alarm data", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void selectDayInUI(String day) {
        switch (day) {
            case "Mon":
                // Select Monday's toggle button
                mondayToggleButton.setChecked(true);
                break;
            case "Tue":
                // Select Tuesday's toggle button
                tuesdayToggleButton.setChecked(true);
                break;
            case "Wed":
                // Select Wednesday's toggle button
                wednesdayToggleButton.setChecked(true);
                break;
            case "Thu":
                // Select Thursday's toggle button
                thursdayToggleButton.setChecked(true);
                break;
            case "Fri":
                // Select Friday's toggle button
                fridayToggleButton.setChecked(true);
                break;
            case "Sat":
                // Select Saturday's toggle button
                saturdayToggleButton.setChecked(true);
                break;
            case "Sun":
                // Select Sunday's toggle button
                sundayToggleButton.setChecked(true);
                break;
            default:
                break;
        }
    }


    private void updateAlarmTime() {
        // Ensure we have a valid alarmId
        if (alarmId == null || alarmId.isEmpty()) {
            Toast.makeText(this, "Invalid alarm ID", Toast.LENGTH_SHORT).show();
            Log.e("AlarmUpdate", "Invalid alarm ID");
            return;
        }

        // Get the selected time from the TimePicker
        int hour = alarmTimePicker.getHour();
        int minutes = alarmTimePicker.getMinute();

        StringBuilder selectedDays = new StringBuilder();
        if (mondayToggleButton.isChecked()) {
            selectedDays.append("Mon,");
        }
        if (tuesdayToggleButton.isChecked()) {
            selectedDays.append("Tue,");
        }
        if (wednesdayToggleButton.isChecked()) {
            selectedDays.append("Wed,");
        }
        if (thursdayToggleButton.isChecked()) {
            selectedDays.append("Thu,");
        }
        if (fridayToggleButton.isChecked()) {
            selectedDays.append("Fri,");
        }
        if (saturdayToggleButton.isChecked()) {
            selectedDays.append("Sat,");
        }
        if (sundayToggleButton.isChecked()) {
            selectedDays.append("Sun,");
        }

        // Remove the last comma if there are any selected days
        if (selectedDays.length() > 0) {
            selectedDays.deleteCharAt(selectedDays.length() - 1);
        }

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Get current user's email


        // Log the selected values for debugging
        Log.d("AlarmUpdate", "Selected Time: " + hour + ":" + minutes);
        Log.d("AlarmUpdate", "Selected Days: " + selectedDays.toString());
        Log.d("AlarmUpdate", "Current User Email: " + currentUserEmail);

        // Check if the email is valid
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Log.e("AlarmUpdate", "Current user email is null or empty");
            Toast.makeText(this, "Current user email is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare to update the members list based on the checkbox status
//
//
//        boolean isChecked = emailCheckBoxes.containsKey(currentUserEmail) && emailCheckBoxes.get(currentUserEmail).isChecked();
        boolean isChecked = isMyAlarmActive.isChecked();
        Log.d("AlarmUpdate", "Is user checkbox checked: " + isChecked);

        Map<String, Object> updates = new HashMap<>();
        updates.put("hour", hour);
        updates.put("minutes", minutes);
        updates.put("daysToRing", selectedDays.toString()); // Update the days

        // Create a map to handle member updates (add to true/false based on checkbox)
        DatabaseReference membersRef = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms").child(alarmId).child("members");
        // Log the reference being used to update Firebase
        Log.d("AlarmUpdate", "Updating Firebase at path: alarms/" + alarmId + "/members");


        // NOT WORKING YET
//        if (isChecked) {
//            membersRef.child("true").child(currentUserEmail).setValue(true)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Log.d("AlarmUpdate", "User email added to 'true' list");
//                        } else {
//                            Log.e("AlarmUpdate", "Error adding user email to 'true' list: " + task.getException());
//                        }
//                    });
//            membersRef.child("false").child(currentUserEmail).removeValue();
//        } else {
//            membersRef.child("false").child(currentUserEmail).setValue(true)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Log.d("AlarmUpdate", "User email added to 'false' list");
//                        } else {
//                            Log.e("AlarmUpdate", "Error adding user email to 'false' list: " + task.getException());
//                        }
//                    });
//            membersRef.child("true").child(currentUserEmail).removeValue();
//        }

        // Update the existing alarm in Firebase
        databaseRef.child(alarmId).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AlarmUpdate", "Alarm updated successfully");
                        Toast.makeText(this, "Alarm updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AlarmUpdate", "Failed to update alarm: " + task.getException());
                        Toast.makeText(this, "Failed to update alarm", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AlarmUpdate", "Error: " + e.getMessage());
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }





    private void setExactAlarm(Calendar calendar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                showExactAlarmPermissionDialog();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void showExactAlarmPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exact Alarm Permission Needed")
                .setMessage("This app needs permission to schedule exact alarms. Please enable it in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    // Launch app settings to allow user to enable exact alarms
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void OnToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(FunctionalAlarmActivity.this, "Alarm turned ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent intent = new Intent(this, AlarmRing.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            setExactAlarm(calendar); // Use setExactAlarm method
        } else {
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                Toast.makeText(FunctionalAlarmActivity.this, "Alarm turned off", Toast.LENGTH_SHORT).show();
            }
        }
    }
}