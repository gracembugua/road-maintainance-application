package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BestActivity extends AppCompatActivity {

    TextView bestEngineer;
    Button backButton, notifyButton;
    DatabaseReference bidsReference;
    FirebaseUser currentUser;
    Map<String, Bid> bestBids;

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bestactivity);

        bestEngineer = findViewById(R.id.bestEngineer);
        notifyButton = findViewById(R.id.notifyButton);
        backButton = findViewById(R.id.backButton);

        bidsReference = FirebaseDatabase.getInstance().getReference("engineersbidding");
        bestBids = new HashMap<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationsToWinners();
            }
        });

        fetchBids();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNotificationReceiver, new IntentFilter("local-notification"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNotificationReceiver);
    }

    private void fetchBids() {
        bidsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("BestActivity", "No bids found");
                    bestEngineer.setText("No bids found.");
                    return;
                }

                bestBids.clear();  // Clear previous best bids
                for (DataSnapshot potholeSnapshot : dataSnapshot.getChildren()) {
                    String potholeId = potholeSnapshot.getKey();
                    if (potholeId != null) {
                        Log.d("BestActivity", "Pothole ID: " + potholeId);
                        List<Bid> bidsList = new ArrayList<>();
                        for (DataSnapshot bidSnapshot : potholeSnapshot.getChildren()) {
                            Bid bid = bidSnapshot.getValue(Bid.class);
                            if (bid != null) {
                                Log.d("BestActivity", "Bid found: " + bid.getWinnerEmail());
                                bidsList.add(bid);
                            } else {
                                Log.d("BestActivity", "Bid is null for pothole: " + potholeId);
                            }
                        }
                        chooseBestEngineer(potholeId, bidsList);
                    } else {
                        Log.d("BestActivity", "Pothole ID is null");
                    }
                }
                updateTextView();  // Update the TextView after processing bids
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("BestActivity", "Failed to fetch bids: " + databaseError.getMessage());
                Toast.makeText(BestActivity.this, "Failed to fetch bids", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseBestEngineer(String potholeId, List<Bid> bidsList) {
        if (bidsList.isEmpty()) {
            Log.d("BestActivity", "No bids to evaluate for pothole: " + potholeId);
            return;
        }

        Bid bestBid = null;
        for (Bid bid : bidsList) {
            if (bestBid == null || compareBids(bid, bestBid) < 0) {
                bestBid = bid;
            }
        }

        if (bestBid != null) {
            bestBids.put(potholeId, bestBid);
            Log.d("BestActivity", "Best bid for pothole " + potholeId + " is " + bestBid.getWinnerEmail());
        }
    }

    private int compareBids(Bid bid1, Bid bid2) {
        int amount1 = Integer.parseInt(bid1.getAmount());
        int amount2 = Integer.parseInt(bid2.getAmount());
        if (amount1 != amount2) {
            return Integer.compare(amount1, amount2);
        }

        int experience1 = parseExperience(bid1.getExperienceYears());
        int experience2 = parseExperience(bid2.getExperienceYears());
        if (experience1 != experience2) {
            return Integer.compare(experience2, experience1);
        }

        int education1 = parseEducation(bid1.getEducationLevel());
        int education2 = parseEducation(bid2.getEducationLevel());
        return Integer.compare(education2, education1);
    }

    private int parseExperience(String experienceYears) {
        switch (experienceYears) {
            case "0":
                return 0;
            case "1-2 years":
                return 2;
            case "3-5 years":
                return 5;
            case "6-10 years":
                return 10;
            case "More than 10 years":
                return 11;
            default:
                return 0;
        }
    }

    private int parseEducation(String educationLevel) {
        switch (educationLevel) {
            case "High School":
                return 1;
            case "Bachelor's Degree":
                return 2;
            case "Master's Degree":
                return 3;
            case "PhD":
                return 4;
            default:
                return 0;
        }
    }

    private void updateTextView() {
        if (bestBids.isEmpty()) {
            Log.d("BestActivity", "No best bids to display");
            bestEngineer.setText("No winners found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Bid> entry : bestBids.entrySet()) {
            String potholeId = entry.getKey();
            Bid bestBid = entry.getValue();
            sb.append("Pothole ID: ").append(potholeId).append("\n");
            sb.append("Winner Details:\n");
            sb.append("Name: ").append(bestBid.getFirstName()).append(" ").append(bestBid.getLastName()).append("\n");
            sb.append("Location: ").append(bestBid.getLocation()).append("\n");
            sb.append("Education Level: ").append(bestBid.getEducationLevel()).append("\n");
            sb.append("Experience Years: ").append(bestBid.getExperienceYears()).append("\n");
            sb.append("Amount: ").append(bestBid.getAmount()).append("\n");
            sb.append("Phone Number: ").append(bestBid.getPhoneNumber()).append("\n");
            sb.append("Email: ").append(bestBid.getWinnerEmail()).append("\n\n");
        }
        bestEngineer.setText(sb.toString());
        Log.d("BestActivity", "TextView updated with detailed best bids");
    }

    private void sendNotificationsToWinners() {
        for (Map.Entry<String, Bid> entry : bestBids.entrySet()) {
            String potholeId = entry.getKey();
            Bid bestBid = entry.getValue();
            if (bestBid != null) {
                sendNotificationToWinner(bestBid.getWinnerEmail(), potholeId);
            }
        }
    }

    private void sendNotificationToWinner(String winnerEmail, String potholeId) {
        Intent intent = new Intent("local-notification");
        intent.putExtra("message", "Congratulations! You have won the bid for pothole " + potholeId + ".");
        intent.putExtra("winner_email", winnerEmail);
        sendBroadcast(intent);
        Log.d("BestActivity", "Notification sent to winner: " + winnerEmail + " for pothole " + potholeId);
    }
}
