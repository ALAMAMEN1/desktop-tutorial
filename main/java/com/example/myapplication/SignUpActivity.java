package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText signUpEmail, signUpPassword;
    Button createAccountButton;
    DatabaseHelper dbHelper;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        createAccountButton = findViewById(R.id.createAccountButton);

        dbHelper = new DatabaseHelper(this);
        role = getIntent().getStringExtra("role");

        createAccountButton.setOnClickListener(v -> {
            String email = signUpEmail.getText().toString().trim();
            String password = signUpPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.registerUser(email, password, role);
                if (success) {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Account already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
