package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {
    private TextView welcomeText, resultText;
    private RecyclerView recyclerViewModules;
    private Button calculateButton, logoutButton;
    private List<Module> moduleList = new ArrayList<>();
    private ModuleAdapter adapter;
    private DatabaseHelper dbHelper;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        studentId = getIntent().getIntExtra("studentId", -1);
        dbHelper = new DatabaseHelper(this);

        welcomeText = findViewById(R.id.welcomeText);
        resultText = findViewById(R.id.tvResult);
        recyclerViewModules = findViewById(R.id.moduleList);
        calculateButton = findViewById(R.id.calculateButton4);
        logoutButton = findViewById(R.id.logoutButton);

        welcomeText.setText("Welcome, Student #" + studentId);

        recyclerViewModules.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModuleAdapter(moduleList);
        recyclerViewModules.setAdapter(adapter);

        loadModules();

        calculateButton.setOnClickListener(v -> calculateAverage());

        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSelectionActivity.class));
            finish();
        });
    }

    private void loadModules() {
        moduleList.clear();
        List<Note> notes = dbHelper.getNotesForStudent(studentId);
        for (Note note : notes) {
            moduleList.add(new Module(
                    note.getSubject(),
                    note.getTd(),
                    note.getTp(),
                    note.getExam(),
                    note.getCoefficient()
            ));
        }
        adapter.notifyDataSetChanged();
    }

    private void calculateAverage() {
        float totalWeighted = 0;
        float totalCoefficients = 0;

        for (Module m : moduleList) {
            float moduleAvg = (m.td * 0.2f + m.tp * 0.2f + m.exam * 0.6f);
            totalWeighted += moduleAvg * m.coefficient;
            totalCoefficients += m.coefficient;
        }

        float finalAverage = totalCoefficients > 0 ? totalWeighted / totalCoefficients : 0;
        resultText.setText(String.format("Average: %.2f - %s",
                finalAverage,
                finalAverage >= 10 ? "Pass" : "Fail"));
    }
}