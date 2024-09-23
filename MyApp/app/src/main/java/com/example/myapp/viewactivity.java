package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class viewactivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PotholeAdapter adapter;
    private List<Pothole> potholeList;
    private TextView approveButton, declineButton, viewWinnerButton;

    private DatabaseReference databaseReference;
    private int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityview);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        potholeList = new ArrayList<>();
        adapter = new PotholeAdapter(this, potholeList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Potholes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                potholeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pothole pothole = dataSnapshot.getValue(Pothole.class);
                    if (pothole != null) {
                        String mediaUrl = dataSnapshot.child("mediaUrl").getValue(String.class);
                        pothole.setMediaUrl(mediaUrl);

                        potholeList.add(pothole);
                    }
                }
                adapter.notifyDataSetChanged();
                if (potholeList.size() > 0) {
                    currentPosition = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(viewactivity.this, "Failed to load potholes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        approveButton = findViewById(R.id.approveButton);
        declineButton = findViewById(R.id.declineButton);
        viewWinnerButton = findViewById(R.id.viewWinnerButton);

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < potholeList.size()) {
                    Pothole pothole = potholeList.get(currentPosition);

                    DatabaseReference approvedPotholesRef = FirebaseDatabase.getInstance().getReference("approved_potholes");
                    approvedPotholesRef.push().setValue(pothole);

                    DatabaseReference engineersBiddingRef = FirebaseDatabase.getInstance().getReference("engineers_bidding");
                    engineersBiddingRef.push().setValue(pothole);

                    potholeList.remove(currentPosition);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(viewactivity.this, "Pothole approved and sent for bidding", Toast.LENGTH_SHORT).show();
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < potholeList.size()) {
                    potholeList.remove(currentPosition);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(viewactivity.this, "Pothole declined", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for view winner button
        viewWinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < potholeList.size()) {
                    Pothole pothole = potholeList.get(currentPosition);
                    String potholeId = pothole.getId(); // Get the pothole ID

                    Intent intent = new Intent(viewactivity.this, BestActivity.class);
                    intent.putExtra("potholeId", potholeId);
                    startActivity(intent);
                } else {
                    Toast.makeText(viewactivity.this, "No pothole selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    currentPosition = layoutManager.findFirstVisibleItemPosition();
                }
            }
        });
    }
}