package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AssignModuleActivity extends AppCompatActivity {
    Spinner moduleSpinner, teacherSpinner, formationSpinner, sectionSpinner, yearSpinner, groupSpinner;
    Button assignButton;
    ListView assignmentsListView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_module);

        db = new DatabaseHelper(this);

        moduleSpinner = findViewById(R.id.moduleSpinner);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        formationSpinner = findViewById(R.id.formationSpinner);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        groupSpinner = findViewById(R.id.groupSpinner);
        assignButton = findViewById(R.id.assignButton);
        assignmentsListView = findViewById(R.id.assignmentsListView);

        setupSpinner(moduleSpinner, db.getAllModuleNames());
        setupSpinner(teacherSpinner, db.getAllTeachersEmails());
        setupSpinner(formationSpinner, db.getAllFormations());
        setupSpinner(sectionSpinner, db.getAllSections());
        setupSpinner(yearSpinner, db.getAllYears());
        setupSpinner(groupSpinner, db.getAllGroups());

        assignButton.setOnClickListener(v -> {
            String module = moduleSpinner.getSelectedItem().toString();
            String teacher = teacherSpinner.getSelectedItem().toString();
            String formation = formationSpinner.getSelectedItem().toString();
            String section = sectionSpinner.getSelectedItem().toString();
            String year = yearSpinner.getSelectedItem().toString();
            String group = groupSpinner.getSelectedItem().toString();

            if (db.assignModuleToTeacher(teacher, module, formation, section, year, group)) {
                Toast.makeText(this, "تم التعيين بنجاح", Toast.LENGTH_SHORT).show();
                loadAssignments();
            } else {
                Toast.makeText(this, "فشل التعيين", Toast.LENGTH_SHORT).show();
            }
        });

        loadAssignments();
    }

    private void setupSpinner(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void loadAssignments() {
        List<String> list = db.getAllAssignments();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        assignmentsListView.setAdapter(adapter);
    }
}

