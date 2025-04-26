package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Student> students;
    Spinner formationSpinner;
    Spinner sectionSpinner;
    Spinner yearSpinner;
    Spinner groupSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        formationSpinner = findViewById(R.id.spinnerFormation);
        sectionSpinner = findViewById(R.id.spinnerSection);
        yearSpinner = findViewById(R.id.spinnerYear);
        groupSpinner = findViewById(R.id.spinnerGroup);

        setupSpinners();

        loadStudents();
    }

    private void setupSpinners() {
        ArrayAdapter<String> formationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllFormations());
        formationSpinner.setAdapter(formationAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllSections());
        sectionSpinner.setAdapter(sectionAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllYears());
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllGroups());
        groupSpinner.setAdapter(groupAdapter);
    }

    private void loadStudents() {
        students = dbHelper.getAllStudents();
        adapter = new StudentAdapter(this, students);
        recyclerView.setAdapter(adapter);
    }

    public void updateStudentFormation(int studentId, String formation, String section, String year, String group) {
        boolean updated = dbHelper.updateStudentFormation(studentId, formation, section, year, group);
        if (updated) {
            Toast.makeText(this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
            loadStudents();
        } else {
            Toast.makeText(this, "فشل التحديث", Toast.LENGTH_SHORT).show();
        }
    }
}
