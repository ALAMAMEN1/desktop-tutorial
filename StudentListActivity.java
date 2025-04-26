package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private String formation, section, year, group, moduleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        listView = findViewById(R.id.studentListView);
        dbHelper = new DatabaseHelper(this);

        formation = getIntent().getStringExtra("formation");
        section = getIntent().getStringExtra("section");
        year = getIntent().getStringExtra("year");
        group = getIntent().getStringExtra("group");
        moduleName = getIntent().getStringExtra("moduleName");

        loadStudents();
    }

    private void loadStudents() {
        List<Student> students = dbHelper.getStudentList(formation, section, year, group);
        List<String> names = new ArrayList<>();
        for (Student s : students) {
            names.add(s.getName() + " (" + s.getEmail() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Student selectedStudent = students.get(position);
            Intent intent = new Intent(StudentListActivity.this, NoteInputActivity.class);
            intent.putExtra("studentId", selectedStudent.getId());
            intent.putExtra("studentName", selectedStudent.getName());
            intent.putExtra("moduleName", moduleName);
            startActivity(intent);
        });
    }
}
