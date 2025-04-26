package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.adapters.SubjectAdapter;

import java.util.List;

public class ManageSubjectsActivity extends AppCompatActivity {

    private EditText editTextSubjectName;
    private Button buttonAddSubject;
    private RecyclerView recyclerViewSubjects;
    private DatabaseHelper databaseHelper;
    private SubjectAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subjects);

        editTextSubjectName = findViewById(R.id.editTextSubjectName);
        buttonAddSubject = findViewById(R.id.buttonAddSubject);
        recyclerViewSubjects = findViewById(R.id.recyclerViewSubjects);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));

        loadSubjects();

        buttonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubject();
            }
        });
    }

    private void loadSubjects() {
        List<String> subjects = databaseHelper.getSimpleList("subjects", "name");
        subjectAdapter = new SubjectAdapter(subjects);
        recyclerViewSubjects.setAdapter(subjectAdapter);
    }

    private void addSubject() {
        String name = editTextSubjectName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "ادخل اسم المادة", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.insertSubject(name);
        Toast.makeText(this, "تمت إضافة المادة", Toast.LENGTH_SHORT).show();
        loadSubjects();
        editTextSubjectName.setText("");
    }
}
