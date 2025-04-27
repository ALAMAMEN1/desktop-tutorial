package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class AssignSubjectActivity extends AppCompatActivity {

    private AutoCompleteTextView autoTeachers, autoSubjects, autoFormation,
            autoSection, autoYear, autoGroup;
    private TextInputEditText editTextCoefficient;
    private MaterialButton buttonAssign, buttonAddTeacher;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_subject);

        autoTeachers = findViewById(R.id.spinnerTeachers);
        autoSubjects = findViewById(R.id.spinnerSubjects);
        autoFormation = findViewById(R.id.spinnerFormation);
        autoSection = findViewById(R.id.spinnerSection);
        autoYear = findViewById(R.id.spinnerYear);
        autoGroup = findViewById(R.id.spinnerGroup);
        editTextCoefficient = findViewById(R.id.editTextCoefficient);
        buttonAssign = findViewById(R.id.buttonAssign);
        buttonAddTeacher = findViewById(R.id.buttonAddTeacher);

        databaseHelper = new DatabaseHelper(this);

        loadDropdowns();

        buttonAssign.setOnClickListener(v -> assignSubject());
        buttonAddTeacher.setOnClickListener(v -> showAddTeacherDialog());
    }

    private void loadDropdowns() {
        List<String> teachers = databaseHelper.getAllTeachers();
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, teachers);
        autoTeachers.setAdapter(teacherAdapter);
        autoTeachers.setOnItemClickListener((parent, view, position, id) -> {
        });

        List<String> subjects = databaseHelper.getAllSubjects();
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, subjects);
        autoSubjects.setAdapter(subjectAdapter);

        List<String> formations = databaseHelper.getFormations();
        ArrayAdapter<String> formationAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, formations);
        autoFormation.setAdapter(formationAdapter);

        List<String> sections = databaseHelper.getSections();
        ArrayAdapter<String> sectionAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, sections);
        autoSection.setAdapter(sectionAdapter);

        List<String> years = databaseHelper.getYears();
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, years);
        autoYear.setAdapter(yearAdapter);

        List<String> groups = databaseHelper.getGroups();
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, groups);
        autoGroup.setAdapter(groupAdapter);

        if (teachers.isEmpty()) {
            Toast.makeText(this, "لا يوجد معلمون مسجلون. يرجى إضافة معلم أولاً", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddTeacherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة معلم جديد");

        final TextInputEditText input = new TextInputEditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("البريد الإلكتروني للمعلم");

        TextInputLayout inputLayout = new TextInputLayout(this);
        inputLayout.addView(input);
        inputLayout.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.dialog_padding),
                0,
                getResources().getDimensionPixelOffset(R.dimen.dialog_padding),
                0
        );

        builder.setView(inputLayout);

        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String email = input.getText() != null ? input.getText().toString().trim() : "";
            if (!email.isEmpty()) {
                if (databaseHelper.registerUser(email, "123456", "teacher")) {
                    loadDropdowns();
                    Toast.makeText(this, "تمت إضافة المعلم بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "فشل إضافة المعلم. قد يكون البريد مسجلاً مسبقاً", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "الرجاء إدخال بريد إلكتروني صحيح", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void assignSubject() {
        String teacher = autoTeachers.getText().toString();
        String subject = autoSubjects.getText().toString();
        String formation = autoFormation.getText().toString();
        String section = autoSection.getText().toString();
        String year = autoYear.getText().toString();
        String group = autoGroup.getText().toString();
        String coefficientStr = editTextCoefficient.getText() != null ?
                editTextCoefficient.getText().toString().trim() : "";

        if (teacher.isEmpty() || subject.isEmpty() || formation.isEmpty() ||
                section.isEmpty() || year.isEmpty() || group.isEmpty() || coefficientStr.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int coefficient = Integer.parseInt(coefficientStr);

            if (coefficient <= 0) {
                Toast.makeText(this, "المعامل يجب أن يكون رقمًا موجبًا", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = databaseHelper.assignSubject(
                    teacher,
                    subject,
                    formation,
                    section,
                    year,
                    group,
                    coefficient
            );

            if (success) {
                Toast.makeText(this, "تم ربط المادة بالمعلم بنجاح", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "حدث خطأ أثناء محاولة الربط", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "المعامل يجب أن يكون رقمًا صحيحًا", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextCoefficient.setText("");
    }
}