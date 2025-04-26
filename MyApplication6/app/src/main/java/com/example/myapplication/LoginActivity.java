package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput, passwordInput;
    Button loginButton, signUpButton;
    DatabaseHelper dbHelper;
    String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        selectedRole = getIntent().getStringExtra("role");
        dbHelper = new DatabaseHelper(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String actualRole = dbHelper.getUserRole(email, password);

            if (actualRole == null) {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!actualRole.equals(selectedRole)) {
                Toast.makeText(this, "This account is not registered as " + selectedRole, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent;
            switch (selectedRole) {
                case "student":
                    int studentId = dbHelper.getStudentIdByEmail(email);
                    if (studentId == -1) {
                        Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent = new Intent(this, StudentDashboardActivity.class);
                    intent.putExtra("studentId", studentId);
                    break;
                case "teacher":
                    intent = new Intent(this, TeacherDashboardActivity.class);
                    break;
                case "admin":
                    intent = new Intent(this, AdminDashboardActivity.class);
                    break;
                default:
                    return;
            }

            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
        });
    }
}