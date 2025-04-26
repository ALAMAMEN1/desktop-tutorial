package com.example.myapplication;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageModulesActivity extends AppCompatActivity {

    EditText moduleNameEditText, teacherEmailEditText, formationEditText, sectionEditText, yearEditText, groupEditText;
    Button assignButton;
    ListView assignmentsListView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_modules);

        db = new DatabaseHelper(this);

        moduleNameEditText = findViewById(R.id.moduleNameEditText);
        teacherEmailEditText = findViewById(R.id.teacherEmailEditText);
        formationEditText = findViewById(R.id.formationEditText);
        sectionEditText = findViewById(R.id.sectionEditText);
        yearEditText = findViewById(R.id.yearEditText);
        groupEditText = findViewById(R.id.groupEditText);
        assignButton = findViewById(R.id.assignButton);
        assignmentsListView = findViewById(R.id.assignmentsListView);

        assignButton.setOnClickListener(v -> {
            String email = teacherEmailEditText.getText().toString();
            String module = moduleNameEditText.getText().toString();
            String formation = formationEditText.getText().toString();
            String section = sectionEditText.getText().toString();
            String year = yearEditText.getText().toString();
            String group = groupEditText.getText().toString();

            if (db.assignModuleToTeacher(email, module, formation, section, year, group)) {
                Toast.makeText(this, "تم التعيين بنجاح", Toast.LENGTH_SHORT).show();
                loadAssignments();
            } else {
                Toast.makeText(this, "فشل التعيين", Toast.LENGTH_SHORT).show();
            }
        });

        loadAssignments();
    }

    private void loadAssignments() {
        List<String> list = db.getAllAssignments();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        assignmentsListView.setAdapter(adapter);
    }
}
