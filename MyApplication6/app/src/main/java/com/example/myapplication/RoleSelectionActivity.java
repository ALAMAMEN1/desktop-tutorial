package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    Button studentButton, teacherButton, adminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        studentButton = findViewById(R.id.studentButton);
        teacherButton = findViewById(R.id.teacherButton);
        adminButton = findViewById(R.id.adminButton);

        studentButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoleSelectionActivity.this, LoginActivity.class);
            intent.putExtra("role", "student");
            startActivity(intent);
        });

        teacherButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoleSelectionActivity.this, LoginActivity.class);
            intent.putExtra("role", "teacher");
            startActivity(intent);
        });

        adminButton.setOnClickListener(v -> {
            Intent intent = new Intent(RoleSelectionActivity.this, LoginActivity.class);
            intent.putExtra("role", "admin");
            startActivity(intent);
        });
    }
}