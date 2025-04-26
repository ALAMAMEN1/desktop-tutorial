package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageModulesActivity extends AppCompatActivity {
    private ListView modulesListView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_modules);

        modulesListView = findViewById(R.id.modulesListView);
        dbHelper = new DatabaseHelper(this);

        List<String> modules = dbHelper.getAllModuleNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modules);
        modulesListView.setAdapter(adapter);
    }
}
