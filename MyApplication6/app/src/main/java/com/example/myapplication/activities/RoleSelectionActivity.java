package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button buttonAdmin, buttonTeacher, buttonStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        buttonAdmin = findViewById(R.id.buttonAdmin);
        buttonTeacher = findViewById(R.id.buttonTeacher);
        buttonStudent = findViewById(R.id.buttonStudent);

        buttonAdmin.setOnClickListener(v -> openSignUp("admin"));
        buttonTeacher.setOnClickListener(v -> openSignUp("teacher"));
        buttonStudent.setOnClickListener(v -> openSignUp("student"));
    }

    private void openSignUp(String role) {
        Intent intent = new Intent(RoleSelectionActivity.this, SignUpActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
}
