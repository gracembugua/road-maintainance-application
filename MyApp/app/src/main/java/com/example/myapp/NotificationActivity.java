package com.example.myapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        // Initialize views
        TextView notificationText = findViewById(R.id.notificationText);

        // Get intent extras
        String message = getIntent().getStringExtra("message");
        String winnerEmail = getIntent().getStringExtra("winner_email");

        // Display notification message
        String notificationMessage = message + "\nWinner's Email: " + winnerEmail;
        notificationText.setText(notificationMessage);
    }
}
