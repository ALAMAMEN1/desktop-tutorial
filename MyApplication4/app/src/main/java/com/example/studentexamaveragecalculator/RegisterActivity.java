package com.example.studentexamaveragecalculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etFullName, etSpecialization;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etSpecialization = findViewById(R.id.etSpecialization);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || specialization.isEmpty()) {
            Toast.makeText(this, "يجب ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.isUsernameExists(username)) {
            Toast.makeText(this, "اسم المستخدم موجود مسبقاً", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addUser(username, password, fullName, specialization)) {
            Toast.makeText(this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "فشل في التسجيل", Toast.LENGTH_SHORT).show();
        }
    }
}