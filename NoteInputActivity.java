package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NoteInputActivity extends AppCompatActivity {
    private EditText tdInput, tpInput, examInput, coefficientInput;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private int studentId;
    private String moduleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_input);

        tdInput = findViewById(R.id.tdInput);
        tpInput = findViewById(R.id.tpInput);
        examInput = findViewById(R.id.examInput);
        coefficientInput = findViewById(R.id.coefficientInput);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        studentId = getIntent().getIntExtra("studentId", -1);
        moduleName = getIntent().getStringExtra("moduleName");

        saveButton.setOnClickListener(v -> {
            float td = Float.parseFloat(tdInput.getText().toString());
            float tp = Float.parseFloat(tpInput.getText().toString());
            float exam = Float.parseFloat(examInput.getText().toString());
            int coefficient = Integer.parseInt(coefficientInput.getText().toString());

            boolean success = dbHelper.insertNote(studentId, moduleName, td, tp, exam, coefficient);
            if (success) {
                Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "خطأ أثناء الحفظ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
