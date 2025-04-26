package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private Spinner spinnerEmails, spinnerFormation, spinnerSection, spinnerYear, spinnerGroup;
    private Button buttonAddStudent;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> emailAdapter, formationAdapter, sectionAdapter, yearAdapter, groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        spinnerEmails = findViewById(R.id.spinnerEmails);
        spinnerFormation = findViewById(R.id.spinnerFormation);
        spinnerSection = findViewById(R.id.spinnerSection);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);

        databaseHelper = new DatabaseHelper(this);

        loadSpinners();

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });
    }

    private void loadSpinners() {
        List<String> emails = databaseHelper.getAllStudentEmails();
        emailAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emails);
        spinnerEmails.setAdapter(emailAdapter);

        formationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getFormations());
        spinnerFormation.setAdapter(formationAdapter);

        sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getSections());
        spinnerSection.setAdapter(sectionAdapter);

        yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getYears());
        spinnerYear.setAdapter(yearAdapter);

        groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getGroups());
        spinnerGroup.setAdapter(groupAdapter);
    }

    private void addStudent() {
        String email = spinnerEmails.getSelectedItem().toString();
        String formation = spinnerFormation.getSelectedItem().toString();
        String section = spinnerSection.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();
        String group = spinnerGroup.getSelectedItem().toString();

        if (databaseHelper.insertStudent(email, formation, section, year, group)) {
            Toast.makeText(this, "تم إضافة الطالب بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "فشل إضافة الطالب أو الطالب موجود مسبقاً", Toast.LENGTH_SHORT).show();
        }
    }
}
