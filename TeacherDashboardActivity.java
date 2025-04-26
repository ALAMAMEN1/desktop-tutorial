package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity {
    private ListView modulesListView;
    private DatabaseHelper dbHelper;
    private String teacherEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        teacherEmail = getIntent().getStringExtra("email");
        dbHelper = new DatabaseHelper(this);
        modulesListView = findViewById(R.id.modulesListView);

        loadAssignedModules();
    }

    private void loadAssignedModules() {
        List<Assignment> assignments = dbHelper.getAssignmentsForTeacher(teacherEmail);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        for (Assignment a : assignments) {
            adapter.add(a.getSubject() + " (" + a.getFormation() + "-" + a.getSection() + "-" + a.getYear() + "-" + a.getGroup() + ")");
        }

        modulesListView.setAdapter(adapter);

        modulesListView.setOnItemClickListener((parent, view, position, id) -> {
            Assignment selected = assignments.get(position);
            Intent intent = new Intent(this, StudentListActivity.class);
            intent.putExtra("formation", selected.getFormation());
            intent.putExtra("section", selected.getSection());
            intent.putExtra("year", selected.getYear());
            intent.putExtra("group", selected.getGroup());
            intent.putExtra("moduleName", selected.getSubject());
            startActivity(intent);
        });
    }
}
