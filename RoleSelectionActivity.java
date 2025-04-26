package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    private Button studentButton, teacherButton, adminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        studentButton = findViewById(R.id.studentButton);
        teacherButton = findViewById(R.id.teacherButton);
        adminButton = findViewById(R.id.adminButton);

        studentButton.setOnClickListener(v -> openLogin("student"));
        teacherButton.setOnClickListener(v -> openLogin("teacher"));
        adminButton.setOnClickListener(v -> openLogin("admin"));
    }

    private void openLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
