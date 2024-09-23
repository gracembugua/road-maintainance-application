package com.example.myapp;

import android.os.Bundle;
import android.util.Log;
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

public class engineersbidding extends AppCompatActivity {

    private RecyclerView recyclerView;
    private potholeAdapter2 adapter;
    private List<Pothole> approvedPotholesList;
    private DatabaseReference approvedPotholesRef;
    private static final String TAG = "engineersbidding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitybidding);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvedPotholesList = new ArrayList<>();
        adapter = new potholeAdapter2(this, approvedPotholesList);
        recyclerView.setAdapter(adapter);

        approvedPotholesRef = FirebaseDatabase.getInstance().getReference("approved_potholes");
        approvedPotholesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Data snapshot received");
                approvedPotholesList.clear();
                for (DataSnapshot potholeSnapshot : snapshot.getChildren()) {
                    Pothole pothole = potholeSnapshot.getValue(Pothole.class);
                    if (pothole != null) {
                        approvedPotholesList.add(pothole);
                    } else {
                        Log.e(TAG, "Pothole is null");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load data: " + error.getMessage());
                Toast.makeText(engineersbidding.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
