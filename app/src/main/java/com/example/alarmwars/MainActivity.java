package com.example.alarmwars;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001; // Unique request code for sign-in
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if the user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // User is already signed in, go to AlarmsActivity
            Intent intent = new Intent(MainActivity.this, AlarmsActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so the user can't go back
        }
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Use the Web Client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the sign-in button
        Button signInButton = findViewById(R.id.sign_in);
        signInButton.setOnClickListener(v -> initiateSignIn());
    }

    private void initiateSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully
            String idToken = account.getIdToken();
            Log.d("MainActivity", "ID Token: " + idToken);
            // Go to AlarmsActivity on successful sign-in
            Intent intent = new Intent(MainActivity.this, AlarmsActivity.class);
            startActivity(intent);
            finish(); // Optional: Finish MainActivity if you don't want to go back
        } catch (ApiException e) {
            Log.w("MainActivity", "Sign-in failed: " + e.getStatusCode());
            Toast.makeText(this, "Sign-in failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }
}
