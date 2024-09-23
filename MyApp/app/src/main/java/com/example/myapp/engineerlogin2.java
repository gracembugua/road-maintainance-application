package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class engineerlogin2 extends AppCompatActivity {

    private static final String TAG = "EngineersLogin2";
    private EditText etEngineerEmailLogin, etEngineerPasswordLogin;
    private Button btnEngineerLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engineerslogin2);

        etEngineerEmailLogin = findViewById(R.id.et_engineer_email_login);
        etEngineerPasswordLogin = findViewById(R.id.et_engineer_password_login);
        btnEngineerLogin = findViewById(R.id.btn_engineer_login);

        mAuth = FirebaseAuth.getInstance();

        btnEngineerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEngineerEmailLogin.getText().toString().trim();
                String password = etEngineerPasswordLogin.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etEngineerEmailLogin.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etEngineerPasswordLogin.setError("Password is required.");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(engineerlogin2.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Toast.makeText(engineerlogin2.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                        try {
                                            Intent intent = new Intent(engineerlogin2.this, engineersbidding.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Failed to start EngineersBiddingActivity", e);
                                            Toast.makeText(engineerlogin2.this, "Failed to navigate to Engineers Bidding Activity", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e(TAG, "FirebaseUser is null after successful sign-in");
                                        Toast.makeText(engineerlogin2.this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e(TAG, "Authentication failed", task.getException());
                                    Toast.makeText(engineerlogin2.this, "Authentication failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
