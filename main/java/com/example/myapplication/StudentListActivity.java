package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    private ListView studentsListView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        studentsListView = findViewById(R.id.studentsListView);
        dbHelper = new DatabaseHelper(this);

        String formation = getIntent().getStringExtra("formation");
        String section = getIntent().getStringExtra("section");
        String year = getIntent().getStringExtra("year");
        String group = getIntent().getStringExtra("group");

        List<Student> students = dbHelper.getStudentList(formation, section, year, group);

        List<String> studentNames = new ArrayList<>();
        for (Student s : students) {
            studentNames.add(s.getName() + " (" + s.getEmail() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, studentNames);
        studentsListView.setAdapter(adapter);
    }
}
