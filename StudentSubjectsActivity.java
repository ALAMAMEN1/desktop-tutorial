package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class StudentSubjectsActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;

    private String studentFormation, studentSection, studentYear, studentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_subjects);

        listView = findViewById(R.id.listViewStudentSubjects);
        dbHelper = new DatabaseHelper(this);

        studentFormation = getIntent().getStringExtra("formation");
        studentSection = getIntent().getStringExtra("section");
        studentYear = getIntent().getStringExtra("year");
        studentGroup = getIntent().getStringExtra("group");

        loadSubjectsForStudent();
    }


    private void loadSubjectsForStudent() {
        List<String> subjects = dbHelper.getSubjectsForStudent(studentFormation, studentSection, studentYear, studentGroup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);
        listView.setAdapter(adapter);
    }
}
