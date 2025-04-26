package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        findViewById(R.id.manageStudentsBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, StudentManagementActivity.class));
        });

        findViewById(R.id.manageTeachersBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, TeacherManagementActivity.class));
        });

        findViewById(R.id.manageModulesBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, ModuleManagementActivity.class));
        });

        findViewById(R.id.assignModulesBtn).setOnClickListener(v -> {
            startActivity(new Intent(this, AssignModuleActivity.class));
        });
    }
}