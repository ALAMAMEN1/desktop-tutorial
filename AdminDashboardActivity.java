package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button manageUsersButton, manageModulesButton, manageConstantsButton, assignModulesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        manageUsersButton = findViewById(R.id.manageUsersButton);
        manageModulesButton = findViewById(R.id.manageModulesButton);
        manageConstantsButton = findViewById(R.id.manageConstantsButton);
        assignModulesButton = findViewById(R.id.assignModulesButton);

        manageUsersButton.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        manageModulesButton.setOnClickListener(v -> startActivity(new Intent(this, ManageModulesActivity.class)));
        manageConstantsButton.setOnClickListener(v -> startActivity(new Intent(this, ManageConstantsActivity.class)));
        assignModulesButton.setOnClickListener(v -> startActivity(new Intent(this, AssignModuleActivity.class)));
    }
}
