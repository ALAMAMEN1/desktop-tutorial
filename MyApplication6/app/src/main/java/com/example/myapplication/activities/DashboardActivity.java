package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private Button buttonAction1, buttonAction2, buttonAssignSubjects;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textViewWelcome = findViewById(R.id.textViewWelcome);
        buttonAction1 = findViewById(R.id.buttonAction1);
        buttonAction2 = findViewById(R.id.buttonAction2);
        buttonAssignSubjects = findViewById(R.id.buttonAssignSubjects);

        role = SessionManager.getUserRole(this);

        if (role == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupDashboard();
    }

    private void setupDashboard() {
        textViewWelcome.setText("مرحباً بك، " + role);

        switch (role) {
            case "admin":
                buttonAction1.setText("إدارة الطلاب");
                buttonAction2.setText("إدارة المواد");
                buttonAssignSubjects.setVisibility(View.VISIBLE);

                buttonAction1.setOnClickListener(v -> {
                    startActivity(new Intent(DashboardActivity.this, ManageStudentsActivity.class));
                });

                buttonAction2.setOnClickListener(v -> {
                    startActivity(new Intent(DashboardActivity.this, ManageSubjectsActivity.class));
                });

                buttonAssignSubjects.setOnClickListener(v -> {
                    startActivity(new Intent(DashboardActivity.this, AssignSubjectActivity.class));
                });
                break;

            case "teacher":
                buttonAction1.setText("إدخال النقاط");
                buttonAction2.setVisibility(View.GONE);
                buttonAssignSubjects.setVisibility(View.GONE);

                buttonAction1.setOnClickListener(v -> {
                    startActivity(new Intent(DashboardActivity.this, EnterGradesActivity.class));
                });
                break;

            case "student":
                buttonAction1.setText("عرض نقاطي");
                buttonAction2.setVisibility(View.GONE);
                buttonAssignSubjects.setVisibility(View.GONE);

                buttonAction1.setOnClickListener(v -> {
                    startActivity(new Intent(DashboardActivity.this, ViewGradesActivity.class));
                });
                break;

            default:
                textViewWelcome.setText("دور غير معروف");
                buttonAction1.setVisibility(View.GONE);
                buttonAction2.setVisibility(View.GONE);
                buttonAssignSubjects.setVisibility(View.GONE);
                break;
        }
    }
}
