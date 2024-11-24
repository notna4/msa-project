package com.example.alarmwars;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
                                                yourTaskTextView.setText("Your task: DESCENDING");
                                                List<List<Long>> descendingData = (List<List<Long>>) nextGameMap.get("DESCENDING");
                                                createButtons(descendingData);
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
                button.setBackgroundColor(Color.LTGRAY);

                // Handle button click
                button.setOnClickListener(v -> {
                    int num = number.intValue();
                    if (!selectedNumbers.contains(num)) {
                        // Add the number to the list if not already present
                        selectedNumbers.add(num);
                        button.setBackgroundColor(Color.GREEN); // Indicate the button is selected
                    } else {
                        // Remove the number from the list if it is already selected
                        selectedNumbers.remove((Integer) num); // Remove by value, not index
                        button.setBackgroundColor(Color.LTGRAY); // Reset button color
                    }

                    if (selectedNumbers.size() == 9) {
                        // Check if the list is in descending order
                        if (isDescending(selectedNumbers)) {
                            AlarmRing.stopAlarm(); // Stop the alarm
                            // Optionally set another alarm for snoozing (e.g., set an alarm for a few minutes later)(to be added)
                            finish(); // Close the popup
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
        selectedNumbersTextView.setText("Selected Numbers: " + selectedNumbers.toString());
    }
}
