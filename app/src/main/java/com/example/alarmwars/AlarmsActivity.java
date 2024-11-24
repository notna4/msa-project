package com.example.alarmwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    private FrameLayout profileImage;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private LinearLayout alarmsLayout;
    private List<String> alarmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            firebaseAuthWithGoogle(account);
        } else {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show();
        }

        // Set up profile image click listener
        profileImage = findViewById(R.id.profile_frame);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        Button gotoalarmsetButton = findViewById(R.id.goToAlarmSet);
        gotoalarmsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetAlarm();
            }
        });

        alarmsLayout = findViewById(R.id.alarmsLayout); // LinearLayout or any other container for alarms
        fetchAlarms();
    }

    // Authenticate with Firebase using the Google account
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AlarmsActivity.this, "Firebase Auth successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlarmsActivity.this, "Firebase Auth failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAlarms() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userEmail = currentUser.getEmail();
        if (userEmail == null) return;

        // Get a reference to the alarms in the database
        DatabaseReference alarmsRef = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms");

        // Attach a listener to the alarms reference to fetch the data
        alarmsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alarmList.clear();  // Clear the previous list of alarms
                alarmsLayout.removeAllViews(); // Remove any old views

                for (DataSnapshot alarmSnapshot : snapshot.getChildren()) {
                    Map<String, Object> alarmData = (Map<String, Object>) alarmSnapshot.getValue();
                    Map<String, Object> members = (Map<String, Object>) alarmData.get("members");

                    // Check if the "members" field contains the user's email under the "true" key
                    if (members != null && members.containsKey("true")) {
                        List<String> memberList = (List<String>) members.get("true");
                        if (memberList != null && memberList.contains(userEmail)) {
                            // Add this alarm to the list (You can create a string or format data as you need)
                            String alarmId = alarmSnapshot.getKey();  // Get the alarm ID
                            String alarmDetails = "Alarm ID: " + alarmId +
                                    "\nTime: " + alarmData.get("hour") + ":" + alarmData.get("minutes") +
                                    "\nDays: " + alarmData.get("daysToRing");
                            alarmList.add(alarmDetails); // Add the formatted alarm data to the list

                            // Create a TextView for each alarm and add it to the layout
                            TextView alarmTextView = new TextView(AlarmsActivity.this);
                            alarmTextView.setText(alarmDetails);
                            alarmTextView.setTextSize(18); // Set the text size as desired
                            alarmTextView.setTextColor(Color.WHITE); // Set the text color
                            alarmTextView.setPadding(0, 16, 0, 16); // Add padding between alarms

                            // Set an OnClickListener to handle the click event
                            alarmTextView.setOnClickListener(v -> {
                                // When the item is clicked, open FunctionalAlarmActivity
                                Intent intent = new Intent(AlarmsActivity.this, FunctionalAlarmActivity.class);
                                intent.putExtra("alarmId", alarmId); // Pass the alarm ID to the next activity
                                startActivity(intent);
                            });

                            // Add TextView to the LinearLayout
                            alarmsLayout.addView(alarmTextView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AlarmsActivity.this, "Failed to load alarms", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void goToSetAlarm() {
        // Ensure user is authenticated with Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a random code for the alarm
        String alarmId = UUID.randomUUID().toString();

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Prepare the data to be saved
        Map<String, Object> alarmData = new HashMap<>();
        alarmData.put("password", "1234");
        alarmData.put("hour", currentHour);
        alarmData.put("minutes", currentMinute);
        alarmData.put("daysToRing", Arrays.asList("Mo", "Tu", "We"));

        // Add members (list of emails under the "true" key)
        Map<String, List<String>> members = new HashMap<>();
        members.put("true", Arrays.asList(userEmail));
        alarmData.put("members", members);

        // Add next game details
        Map<String, List<List<Integer>>> nextGame = new HashMap<>();
        nextGame.put("DESCENDING", Arrays.asList(
                Arrays.asList(1, 8, 6),
                Arrays.asList(4, 9, 2),
                Arrays.asList(3, 5, 7)
        ));
        alarmData.put("nextGame", nextGame);

        // Use the custom database URL
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(
                "https://ronbase-4f0c6-default-rtdb.europe-west1.firebasedatabase.app/"
        ).getReference("alarms").child(alarmId);

        databaseRef.setValue(alarmData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, FunctionalAlarmActivity.class);
                        intent.putExtra("alarmId", alarmId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Failed to set alarm", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Database write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }




    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    googleSignInClient.signOut().addOnCompleteListener(task -> {
                        Toast.makeText(AlarmsActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AlarmsActivity.this, MainActivity.class));
                        finish();
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}