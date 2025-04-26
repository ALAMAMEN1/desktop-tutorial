package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SubjectManagementActivity extends AppCompatActivity {

    private Spinner teacherSpinner, subjectSpinner, formationSpinner, sectionSpinner, yearSpinner, groupSpinner;
    private Button assignButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_management);

        dbHelper = new DatabaseHelper(this);

        teacherSpinner = findViewById(R.id.spinnerTeacher);
        subjectSpinner = findViewById(R.id.spinnerSubject);
        formationSpinner = findViewById(R.id.spinnerFormation);
        sectionSpinner = findViewById(R.id.spinnerSection);
        yearSpinner = findViewById(R.id.spinnerYear);
        groupSpinner = findViewById(R.id.spinnerGroup);
        assignButton = findViewById(R.id.buttonAssignSubject);

        setupSpinners();

        assignButton.setOnClickListener(v -> assignSubject());
    }

    private void setupSpinners() {
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllTeachers());
        teacherSpinner.setAdapter(teacherAdapter);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllSubjects());
        subjectSpinner.setAdapter(subjectAdapter);

        ArrayAdapter<String> formationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllFormations());
        formationSpinner.setAdapter(formationAdapter);

        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllSections());
        sectionSpinner.setAdapter(sectionAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllYears());
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.getAllGroups());
        groupSpinner.setAdapter(groupAdapter);
    }

    private void assignSubject() {
        String teacherName = teacherSpinner.getSelectedItem().toString();
        String subjectName = subjectSpinner.getSelectedItem().toString();
        String formation = formationSpinner.getSelectedItem().toString();
        String section = sectionSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();
        String group = groupSpinner.getSelectedItem().toString();

        boolean inserted = dbHelper.assignSubjectToTeacher(teacherName, subjectName, formation, section, year, group);
        if (inserted) {
            Toast.makeText(this, "تم تعيين المادة للأستاذ بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "حدث خطأ أثناء التعيين", Toast.LENGTH_SHORT).show();
        }
    }
}
