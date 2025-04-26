package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;

import java.util.List;

public class AssignSubjectActivity extends AppCompatActivity {

    private Spinner spinnerTeachers, spinnerSubjects, spinnerFormation, spinnerSection, spinnerYear, spinnerGroup;
    private EditText editTextCoefficient;
    private Button buttonAssign;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_subject);

        spinnerTeachers = findViewById(R.id.spinnerTeachers);
        spinnerSubjects = findViewById(R.id.spinnerSubjects);
        spinnerFormation = findViewById(R.id.spinnerFormation);
        spinnerSection = findViewById(R.id.spinnerSection);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        editTextCoefficient = findViewById(R.id.editTextCoefficient);
        buttonAssign = findViewById(R.id.buttonAssign);

        databaseHelper = new DatabaseHelper(this);

        loadSpinners(); // تحميل البيانات داخل السبنرات

        buttonAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignSubject();
            }
        });
    }

    private void loadSpinners() {
        // تحميل الأساتذة
        List<String> teachers = databaseHelper.getSimpleList("teachers", "email");
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teachers);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeachers.setAdapter(teacherAdapter);

        // تحميل المواد
        List<String> subjects = databaseHelper.getSimpleList("subjects", "name");
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(subjectAdapter);

        // تحميل التكوينات
        ArrayAdapter<String> formationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getFormations());
        formationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFormation.setAdapter(formationAdapter);

        // تحميل الشعب
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getSections());
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(sectionAdapter);

        // تحميل السنوات الدراسية
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getYears());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // تحميل المجموعات
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, databaseHelper.getGroups());
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(groupAdapter);
    }

    private void assignSubject() {
        // جمع القيم من الشاشة
        String teacher = spinnerTeachers.getSelectedItem() != null ? spinnerTeachers.getSelectedItem().toString() : "";
        String subject = spinnerSubjects.getSelectedItem() != null ? spinnerSubjects.getSelectedItem().toString() : "";
        String formation = spinnerFormation.getSelectedItem() != null ? spinnerFormation.getSelectedItem().toString() : "";
        String section = spinnerSection.getSelectedItem() != null ? spinnerSection.getSelectedItem().toString() : "";
        String year = spinnerYear.getSelectedItem() != null ? spinnerYear.getSelectedItem().toString() : "";
        String group = spinnerGroup.getSelectedItem() != null ? spinnerGroup.getSelectedItem().toString() : "";
        String coefficientStr = editTextCoefficient.getText().toString().trim();

        // التحقق من الحقول
        if (teacher.isEmpty() || subject.isEmpty() || formation.isEmpty() || section.isEmpty() || year.isEmpty() || group.isEmpty() || coefficientStr.isEmpty()) {
            Toast.makeText(this, "الرجاء ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        int coefficient;
        try {
            coefficient = Integer.parseInt(coefficientStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "المعامل يجب أن يكون رقماً صحيحاً", Toast.LENGTH_SHORT).show();
            return;
        }

        // عملية الربط
        boolean success = databaseHelper.assignSubject(teacher, subject, formation, section, year, group, coefficient);

        if (success) {
            Toast.makeText(this, "تم ربط المادة بالمعلم والمجموعة بنجاح", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "حدث خطأ أثناء عملية الربط", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextCoefficient.setText("");
    }
}
