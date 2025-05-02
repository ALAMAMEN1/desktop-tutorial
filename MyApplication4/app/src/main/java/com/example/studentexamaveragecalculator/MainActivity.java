package com.example.studentexamaveragecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        studentId = getIntent().getIntExtra("studentId", -1);

        Button btnSemester1 = findViewById(R.id.btnSemester1);
        Button btnSemester2 = findViewById(R.id.btnSemester2);
        Button btnOverall = findViewById(R.id.btnOverall);

        btnSemester1.setOnClickListener(v -> openSemester(1));
        btnSemester2.setOnClickListener(v -> openSemester(2));
        btnOverall.setOnClickListener(v -> openOverallResults());
    }

    private void openSemester(int semester) {
        Intent intent = new Intent(this, SemesterActivity.class);
        intent.putExtra("SEMESTER", semester);
        intent.putExtra("studentId", studentId);
        startActivity(intent);
    }

    private void openOverallResults() {
        Intent intent = new Intent(this, OverallResultsActivity.class);
        intent.putExtra("studentId", studentId);
        startActivity(intent);
    }
}