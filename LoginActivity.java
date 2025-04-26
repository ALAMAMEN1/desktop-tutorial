package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton, signUpButton;
    private DatabaseHelper dbHelper;
    private String selectedRole;

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
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            String actualRole = dbHelper.getUserRole(email, password);
            if (actualRole == null || !actualRole.equals(selectedRole)) {
                Toast.makeText(this, "بيانات الدخول غير صحيحة", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;
            if (selectedRole.equals("student")) {
                Cursor cursor = dbHelper.getStudentDetails(email);
                if (cursor.moveToFirst()) {
                    String formation = cursor.getString(0);
                    String section = cursor.getString(1);
                    String year = cursor.getString(2);
                    String group = cursor.getString(3);

                    intent = new Intent(LoginActivity.this, StudentSubjectsActivity.class);
                    intent.putExtra("formation", formation);
                    intent.putExtra("section", section);
                    intent.putExtra("year", year);
                    intent.putExtra("group", group);
                }
            } else if (selectedRole.equals("teacher")) {
                intent = new Intent(this, TeacherDashboardActivity.class);
            } else {
                intent = new Intent(this, AdminDashboardActivity.class);
            }

            if (intent != null) {
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }

        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
        });
    }
}
