package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    ListView usersListView;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        usersListView = findViewById(R.id.usersListView);
        dbHelper = new DatabaseHelper(this);

        List<String> allUsers = dbHelper.getAllUsers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allUsers);
        usersListView.setAdapter(adapter);
    }
}
