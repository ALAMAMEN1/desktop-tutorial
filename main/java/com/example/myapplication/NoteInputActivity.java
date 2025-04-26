package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoteInputActivity extends AppCompatActivity {

    TextView textViewStudentName, textViewModuleName;
    EditText editTextTD, editTextTP, editTextExam, editTextCoefficient;
    Button buttonSaveNote;

    DatabaseHelper dbHelper;
    int studentId;
    String studentName, moduleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_input);

        // ربط الواجهات
        textViewStudentName  = findViewById(R.id.textViewStudentName);
        textViewModuleName   = findViewById(R.id.textViewModuleName);
        editTextTD           = findViewById(R.id.editTextTD);
        editTextTP           = findViewById(R.id.editTextTP);
        editTextExam         = findViewById(R.id.editTextExam);
        editTextCoefficient  = findViewById(R.id.editTextCoefficient);
        buttonSaveNote       = findViewById(R.id.buttonSaveNote);

        dbHelper = new DatabaseHelper(this);

        // استقبال البيانات
        studentId   = getIntent().getIntExtra("studentId", -1);
        studentName = getIntent().getStringExtra("studentName");
        moduleName  = getIntent().getStringExtra("moduleName");
        // استقبال معامل المادة
        int moduleCoef = getIntent().getIntExtra("moduleCoefficient", 0);


        textViewStudentName.setText("Student: " + studentName);
        textViewModuleName.setText("Module: " + moduleName);

        buttonSaveNote.setOnClickListener(v -> {
            String tdStr    = editTextTD.getText().toString().trim();
            String tpStr    = editTextTP.getText().toString().trim();
            String examStr  = editTextExam.getText().toString().trim();
            String coeffStr = editTextCoefficient.getText().toString().trim();

            if (tdStr.isEmpty() || tpStr.isEmpty() ||
                    examStr.isEmpty() || coeffStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float td  = Float.parseFloat(tdStr);
                float tp  = Float.parseFloat(tpStr);
                float exam= Float.parseFloat(examStr);
                int coefficient = Integer.parseInt(coeffStr);

                boolean success = dbHelper.insertNote(studentId, moduleName, td, tp, exam, coefficient);


                if (success) {
                    Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save note.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
