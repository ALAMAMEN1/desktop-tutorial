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
import com.example.myapplication.utils.SessionManager;

import java.util.List;

public class EnterGradesActivity extends AppCompatActivity {

    private Spinner spinnerSubjects, spinnerStudents;
    private EditText editTextTD, editTextTP, editTextExam;
    private Button buttonSaveGrade;
    private DatabaseHelper databaseHelper;
    private String currentTeacherEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_grades);

        spinnerSubjects = findViewById(R.id.spinnerSubjects);
        spinnerStudents = findViewById(R.id.spinnerStudents);
        editTextTD = findViewById(R.id.editTextTD);
        editTextTP = findViewById(R.id.editTextTP);
        editTextExam = findViewById(R.id.editTextExam);
        buttonSaveGrade = findViewById(R.id.buttonSaveGrade);

        databaseHelper = new DatabaseHelper(this);
        currentTeacherEmail = SessionManager.getUserEmail(this); // جلب الإيميل من الجلسة

        if (currentTeacherEmail == null) {
            Toast.makeText(this, "خطأ: المستخدم غير مسجل الدخول!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubjects();

        spinnerSubjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadStudents();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // لا شيء
            }
        });

        buttonSaveGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGrade();
            }
        });
    }

    private void loadSubjects() {
        List<String> subjects = databaseHelper.getSubjectsForTeacher(currentTeacherEmail);

        if (subjects == null || subjects.isEmpty()) {
            Toast.makeText(this, "لم يتم العثور على مواد مسندة لهذا المعلم", Toast.LENGTH_LONG).show();
            finish(); // خروج بأمان
            return;
        }

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(subjectAdapter);
    }

    private void loadStudents() {
        String selectedSubject = spinnerSubjects.getSelectedItem().toString();
        List<com.example.myapplication.models.Student> students = databaseHelper.getStudentsForTeacherAndSubject(currentTeacherEmail, selectedSubject);

        if (students == null || students.isEmpty()) {
            Toast.makeText(this, "لا يوجد طلاب لهذه المادة!", Toast.LENGTH_SHORT).show();
            spinnerStudents.setAdapter(null);
            return;
        }

        List<String> studentEmails = new java.util.ArrayList<>();
        for (com.example.myapplication.models.Student student : students) {
            studentEmails.add(student.getEmail());
        }

        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentEmails);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudents.setAdapter(studentAdapter);
    }

    private void saveGrade() {
        String selectedStudentEmail = spinnerStudents.getSelectedItem() != null ? spinnerStudents.getSelectedItem().toString() : null;
        String selectedSubject = spinnerSubjects.getSelectedItem() != null ? spinnerSubjects.getSelectedItem().toString() : null;

        if (selectedStudentEmail == null || selectedSubject == null) {
            Toast.makeText(this, "يرجى اختيار طالب ومادة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextTD.getText().toString().isEmpty() || editTextTP.getText().toString().isEmpty() || editTextExam.getText().toString().isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع النقاط", Toast.LENGTH_SHORT).show();
            return;
        }

        float td = Float.parseFloat(editTextTD.getText().toString());
        float tp = Float.parseFloat(editTextTP.getText().toString());
        float exam = Float.parseFloat(editTextExam.getText().toString());

        int studentId = databaseHelper.getStudentIdByEmail(selectedStudentEmail);
        if (studentId == -1) {
            Toast.makeText(this, "خطأ في جلب الطالب!", Toast.LENGTH_SHORT).show();
            return;
        }

        int coefficient = 1;

        boolean success = databaseHelper.insertNote(studentId, selectedSubject, td, tp, exam, coefficient);

        if (success) {
            Toast.makeText(this, "تم حفظ النقاط بنجاح", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "حدث خطأ أثناء الحفظ", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextTD.setText("");
        editTextTP.setText("");
        editTextExam.setText("");
    }
}
