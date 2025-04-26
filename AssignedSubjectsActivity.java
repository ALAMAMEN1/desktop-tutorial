package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AssignedSubjectsActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_subjects);

        listView = findViewById(R.id.listViewAssignedSubjects);
        dbHelper = new DatabaseHelper(this);

        loadAssignedSubjects();
    }

    private void loadAssignedSubjects() {
        List<String> assignments = dbHelper.getAllAssignedSubjects();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assignments);
        listView.setAdapter(adapter);
    }
}
