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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class engineers_login extends AppCompatActivity {

    private EditText etEngineerEmail, etEngineerPassword;
    private Button btnEngineerSignup, btnEngineerLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.engineer_loginactivity);

        etEngineerEmail = findViewById(R.id.et_engineer_email);
        etEngineerPassword = findViewById(R.id.et_engineer_password);
        btnEngineerSignup = findViewById(R.id.btn_engineer_signup);
        btnEngineerLogin = findViewById(R.id.btn_engineer_login);

        mAuth = FirebaseAuth.getInstance();

        btnEngineerSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEngineerEmail.getText().toString().trim();
                String password = etEngineerPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etEngineerEmail.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etEngineerPassword.setError("Password is required.");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(engineers_login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(engineers_login.this, "Sign Up successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(engineers_login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnEngineerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(engineers_login.this, engineerlogin2.class));
            }
        });
    }
}
