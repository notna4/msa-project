package com.example.alarmwars;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class AlarmsActivity extends AppCompatActivity {

    private FrameLayout profileImage;
    private GoogleSignInClient googleSignInClient; // Declare GoogleSignInClient
    private String userInitials = "AB"; // Set this to the user's initials

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Your web client ID from google-services.json
                .requestEmail() // Request email address
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Get the user's email and extract the first initial
            String email = ((GoogleSignInAccount) account).getEmail();
            if (email != null && !email.isEmpty()) {
                String initials = String.valueOf(email.charAt(0)).toUpperCase(); // Get the first character
                TextView profileInitialsTextView = findViewById(R.id.profile_initials);
                profileInitialsTextView.setText(initials); // Set the initials in the TextView
            }
        }

        // Set up profile image click listener
        profileImage = findViewById(R.id.profile_frame);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // Set initials in the profile image (for simplicity)
        // This assumes you have a way to display initials in the profileImage
        // You may want to overlay a TextView on the ImageView for initials

        Button gotoalarmsetButton = findViewById(R.id.goToAlarmSet);

        gotoalarmsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetAlarm();  // Call the method to start the new activity
            }
        });


    }

    private void goToSetAlarm() {
        // Check if the current context is an Activity
        Intent intent = new Intent(this, FunctionalAlarmActivity.class);
        startActivity(intent);
    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logout logic
                        googleSignInClient.signOut().addOnCompleteListener(task -> {
                            Toast.makeText(AlarmsActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AlarmsActivity.this, MainActivity.class));
                            finish(); // Close AlarmsActivity
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
