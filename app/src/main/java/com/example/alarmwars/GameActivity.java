package com.example.alarmwars;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Map<String, List<List<Integer>>> nextGame;
    TextView yourTaskTextView;

    private List<Integer> selectedNumbers = new ArrayList<>();
    private LinearLayout buttonContainer; // Container to hold the buttons
    private TextView selectedNumbersTextView; // To display selected numbers

    private TextView timerTextView;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private boolean isTimerRunning = false;

    private TextView bottomText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        bottomText = findViewById(R.id.bottomText);
        timerTextView = findViewById(R.id.timer);
        yourTaskTextView = findViewById(R.id.yourTask);
        buttonContainer = findViewById(R.id.buttonContainer);
        selectedNumbersTextView = findViewById(R.id.selectedNumbersTextView);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null) {
                checkAlarms(userEmail);
//                Log.d("aici", nextGame.get("DESCENDING").toString());
            } else {
                Log.d("aici", "User email is null.");
            }
        } else {
            Log.d("aici", "No user is currently signed in.");
        }
    }

    private void displayTime(long elapsedTimeMillis) {
        long minutes = (elapsedTimeMillis / (1000 * 60)) % 60;
        long seconds = (elapsedTimeMillis / 1000) % 60;
        long milliseconds = elapsedTimeMillis % 1000;

        timerTextView.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTimeMillis = System.currentTimeMillis() - startTime;
            displayTime(elapsedTimeMillis);

            handler.postDelayed(this, 10); // Update every 10 milliseconds
        }
    };

    private void startTimer() {
        if (!isTimerRunning) {
            startTime = System.currentTimeMillis();
            handler.post(timerRunnable);
            isTimerRunning = true;
            displayTime(startTime);
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable);
            isTimerRunning = false;
            bottomText.setText("Back to menu");
            yourTaskTextView.setText("\uD83C\uDF89 Congratulations!!! \uD83C\uDF89");
            yourTaskTextView.setOnClickListener(view -> {
                Intent intent = new Intent(GameActivity.this, AlarmsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    private void checkAlarms(String userEmail) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("alarms");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms");
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // Current hour (24-hour format)

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot alarmSnapshot : snapshot.getChildren()) {
                    String alarmId = alarmSnapshot.getKey();

                    if (alarmId != null) {
                        DatabaseReference membersRef = alarmSnapshot.child("members/true").getRef();
                        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot membersSnapshot) {
                                boolean emailFound = false;

                                for (DataSnapshot emailSnapshot : membersSnapshot.getChildren()) {
                                    String email = emailSnapshot.getValue(String.class);
                                    if (userEmail.equals(email)) {
                                        emailFound = true;
                                        break;
                                    }
                                }

                                if (emailFound) {
                                    // Check the hour condition
                                    Integer alarmHour = alarmSnapshot.child("hour").getValue(Integer.class);
                                    if (alarmHour != null && alarmHour == currentHour) {
                                        Log.d("aici", "Alarm matching conditions: " + alarmSnapshot.getValue());
                                        Log.d("aici", String.valueOf(alarmSnapshot.child("nextGame")));
                                        DataSnapshot nextGameSnapshot = alarmSnapshot.child("nextGame");

                                        Map<String, Object> nextGameMap = (Map<String, Object>) nextGameSnapshot.getValue();

                                        if (nextGameMap != null) {
                                            Log.d("aici", "nextGame data: " + nextGameMap.toString());
                                            if (nextGameMap.get("DESCENDING") != null) {
                                                yourTaskTextView.setText("Select the numbers in descending order:");
                                                List<List<Long>> descendingData = (List<List<Long>>) nextGameMap.get("DESCENDING");
                                                createButtons(descendingData);
                                                startTimer();
                                            }
                                        } else {
                                            Log.d("aici", "nextGame data is null or empty.");
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("aici", "Error reading members: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("aici", "Error reading alarms: " + error.getMessage());
            }
        });
    }

    private void createButtons(List<List<Long>> descendingData) {
        for (List<Long> row : descendingData) {
            // Create a new horizontal layout for each row
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (Long number : row) {
                Button button = new Button(this);
                button.setText(String.valueOf(number));
//                button.setBackgroundColor(Color.LTGRAY);
                button.setBackgroundResource(R.drawable.rounder);
                button.setTextColor(Color.WHITE);
                int widthInDp = 100; // Change this to the desired width
                int heightInDp = 100; // Change this to the desired height
                float density = getResources().getDisplayMetrics().density;
                int widthInPx = (int) (widthInDp * density);
                int heightInPx = (int) (heightInDp * density);

                button.setLayoutParams(new ViewGroup.LayoutParams(widthInPx, heightInPx));

                Typeface nunitoBold = ResourcesCompat.getFont(this, R.font.nunito_bold);
                button.setTypeface(nunitoBold);
                button.setTextSize(20);

                // Handle button click
                button.setOnClickListener(v -> {
                    int num = number.intValue();
                    if (!selectedNumbers.contains(num)) {
                        // Add the number to the list if not already present
                        selectedNumbers.add(num);
                        button.setBackgroundResource(R.drawable.rounder2);
                    } else {
                        // Remove the number from the list if it is already selected
                        selectedNumbers.remove((Integer) num); // Remove by value, not index
//                        button.setBackgroundColor(Color.LTGRAY); // Reset button color
                        button.setBackgroundResource(R.drawable.rounder);

                    }

                    if (selectedNumbers.size() == 9) {
                        // Check if the list is in descending order
                        if (isDescending(selectedNumbers)) {
                            AlarmRing.stopAlarm(); // Stop the alarm
                            // Optionally set another alarm for snoozing (e.g., set an alarm for a few minutes later)(to be added)
//                            finish(); // Close the popup
                            stopTimer();
                            Log.d("aici", "Selected numbers are in descending order!");
                        } else {
                            Log.d("aici", "Selected numbers are NOT in descending order.");
                        }
                    }

                    updateSelectedNumbersTextView();


                });

                // Add button to the row
                rowLayout.addView(button);
            }

            // Add the row to the container
            buttonContainer.addView(rowLayout);
        }
    }

    private boolean isDescending(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) < list.get(i + 1)) {
                return false; // Not in descending order
            }
        }
        return true; // All elements are in descending order
    }

    private void updateSelectedNumbersTextView() {
        selectedNumbersTextView.setText(selectedNumbers.toString());
    }
}
