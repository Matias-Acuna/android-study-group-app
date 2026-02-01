package com.matias.disciteomnes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Main entry point of the app (if using Firebase Auth)
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the current authenticated Firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is already logged in → go directly to Dashboard
            startActivity(new Intent(this, DashboardActivity.class));
            finish(); // Prevent returning to this screen
        } else {
            // No user logged in → show login/register fragments
            setContentView(R.layout.activity_main);
        }
    }
}
