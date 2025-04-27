package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteEmails, autoCompleteFormation,
            autoCompleteSection, autoCompleteYear, autoCompleteGroup;
    private MaterialButton buttonAddStudent;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> emailAdapter, formationAdapter,
            sectionAdapter, yearAdapter, groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        autoCompleteEmails = findViewById(R.id.spinnerEmails);
        autoCompleteFormation = findViewById(R.id.spinnerFormation);
        autoCompleteSection = findViewById(R.id.spinnerSection);
        autoCompleteYear = findViewById(R.id.spinnerYear);
        autoCompleteGroup = findViewById(R.id.spinnerGroup);

        buttonAddStudent = findViewById(R.id.buttonAddStudent);

        databaseHelper = new DatabaseHelper(this);

        loadDropdowns();

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });
    }

    private void loadDropdowns() {
        List<String> emails = databaseHelper.getAllStudentEmails();
        emailAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, emails);
        autoCompleteEmails.setAdapter(emailAdapter);

        formationAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, databaseHelper.getFormations());
        autoCompleteFormation.setAdapter(formationAdapter);

        sectionAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, databaseHelper.getSections());
        autoCompleteSection.setAdapter(sectionAdapter);

        yearAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, databaseHelper.getYears());
        autoCompleteYear.setAdapter(yearAdapter);

        groupAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, databaseHelper.getGroups());
        autoCompleteGroup.setAdapter(groupAdapter);
    }

    private void addStudent() {
        String email = autoCompleteEmails.getText().toString();
        String formation = autoCompleteFormation.getText().toString();
        String section = autoCompleteSection.getText().toString();
        String year = autoCompleteYear.getText().toString();
        String group = autoCompleteGroup.getText().toString();

        if (email.isEmpty() || formation.isEmpty() || section.isEmpty() || year.isEmpty() || group.isEmpty()) {
            Toast.makeText(this, "الرجاء ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.insertStudent(email, formation, section, year, group)) {
            Toast.makeText(this, "تم إضافة الطالب بنجاح", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "فشل إضافة الطالب أو الطالب موجود مسبقاً", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        autoCompleteEmails.setText("");
        autoCompleteFormation.setText("");
        autoCompleteSection.setText("");
        autoCompleteYear.setText("");
        autoCompleteGroup.setText("");
    }
}