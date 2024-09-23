package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class activitymuniciple extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    // Hardcoded municipal credentials
    private final String MUNICIPAL_EMAIL = "tabinahwambui@gmail.com";
    private final String MUNICIPAL_PASSWORD = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.municiplelogin);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("Username is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required.");
                    return;
                }

                // Check if the entered credentials match the hardcoded credentials
                if (username.equals(MUNICIPAL_EMAIL) && password.equals(MUNICIPAL_PASSWORD)) {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(activitymuniciple.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(activitymuniciple.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(activitymuniciple.this, viewactivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(activitymuniciple.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(activitymuniciple.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
