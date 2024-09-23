package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class biddingactivity extends AppCompatActivity {

    EditText firstName, lastName, email, amount, phoneNumber;
    Spinner locationSpinner, educationSpinner, experienceSpinner;
    Button bidButton;

    DatabaseReference databaseReference;
    String potholeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engineersbidding);

        potholeId = getIntent().getStringExtra("potholeId");

        if (potholeId == null) {
            Toast.makeText(this, "Invalid pothole ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("engineersbidding").child(potholeId); // Use new path

        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.LastName);
        email = findViewById(R.id.email);
        amount = findViewById(R.id.Amount);
        phoneNumber = findViewById(R.id.PhoneNumber);
        locationSpinner = findViewById(R.id.Location);
        educationSpinner = findViewById(R.id.EducationLevel);
        experienceSpinner = findViewById(R.id.spYearsOfExperience);
        bidButton = findViewById(R.id.Bid);

        String[] locations = {"Eastern Bypass (A8)", "Limuru Road", "Kiambu Road", "Waiyaki Way (A104)",
                "Nairobi-Nakuru Highway (A104)", "Gatundu Road", "Ruiru-Kiambu Road",
                "Githunguri Road", "Juja Road", "Kamiti Road", "Banana Hill Road",
                "Kiambu-Kirigiti Road", "Kenyatta Road", "Karura-Kimbo Road",
                "Ndumberi-Kiambu Road", "Kikuyu-Ondiri Road", "Gachie-Kihara Road"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        String[] educationLevels = {"High School", "Bachelor's Degree", "Master's Degree", "PhD"};
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, educationLevels);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationSpinner.setAdapter(educationAdapter);

        String[] experienceYears = {"0", "1-2 years", "3-5 years", "6-10 years", "More than 10 years"};
        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, experienceYears);
        experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        experienceSpinner.setAdapter(experienceAdapter);

        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBidToDatabase();
            }
        });
    }

    private void addBidToDatabase() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String loc = locationSpinner.getSelectedItem().toString();
        String eduLevel = educationSpinner.getSelectedItem().toString();
        String expYears = experienceSpinner.getSelectedItem().toString();
        String amt = amount.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || emailStr.isEmpty() || amt.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String bidId = databaseReference.push().getKey();
        Bid bid = new Bid(bidId, fName, lName, emailStr, loc, eduLevel, expYears, amt, phone);

        if (bidId != null) {
            databaseReference.child(bidId).setValue(bid)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(biddingactivity.this, "Bid added successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(biddingactivity.this, "Failed to add bid", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Failed to add bid", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        amount.setText("");
        phoneNumber.setText("");
        finish();

    }


}
