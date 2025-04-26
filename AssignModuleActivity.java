package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AssignModuleActivity extends AppCompatActivity {
    private Spinner teacherSpinner, moduleSpinner, formationSpinner, sectionSpinner, yearSpinner, groupSpinner;
    private Button assignButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_module);

        db = new DatabaseHelper(this);

        teacherSpinner = findViewById(R.id.teacherSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        formationSpinner = findViewById(R.id.formationSpinner);
        sectionSpinner = findViewById(R.id.sectionSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        groupSpinner = findViewById(R.id.groupSpinner);
        assignButton = findViewById(R.id.assignButton);

        setupSpinner(teacherSpinner, db.getAllTeachersEmails());
        setupSpinner(moduleSpinner, db.getAllModuleNames());
        setupSpinner(formationSpinner, db.getAllFormations());
        setupSpinner(sectionSpinner, db.getAllSections());
        setupSpinner(yearSpinner, db.getAllYears());
        setupSpinner(groupSpinner, db.getAllGroups());

        assignButton.setOnClickListener(v -> {
            String teacher = teacherSpinner.getSelectedItem().toString();
            String module = moduleSpinner.getSelectedItem().toString();
            String formation = formationSpinner.getSelectedItem().toString();
            String section = sectionSpinner.getSelectedItem().toString();
            String year = yearSpinner.getSelectedItem().toString();
            String group = groupSpinner.getSelectedItem().toString();

            if (db.assignModuleToTeacher(teacher, module, formation, section, year, group)) {
                Toast.makeText(this, "تم التعيين بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "حدث خطأ أثناء التعيين", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
