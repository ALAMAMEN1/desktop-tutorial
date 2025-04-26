package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView usersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DatabaseHelper(this);
        usersListView = findViewById(R.id.usersListView);

        List<String> users = dbHelper.getAllUsers();
        // هنا تكمل عمل adapter للعرض
    }
}