package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import java.util.List;


public class TeacherDashboardActivity extends AppCompatActivity {
    private ListView modulesListView;
    private DatabaseHelper dbHelper;
    private String teacherEmail;
    private List<Assignment> assignments;

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
        assignments = dbHelper.getAssignmentsForTeacher(teacherEmail);

        List<String> moduleNames = new ArrayList<>();
        for (Assignment a : assignments) {
            moduleNames.add(a.moduleName + " (" + a.formation + "-" + a.section + "-" + a.year + "-" + a.group + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, moduleNames);
        modulesListView.setAdapter(adapter);

        modulesListView.setOnItemClickListener((parent, view, position, id) -> {
            Assignment selectedAssignment = assignments.get(position);

            Intent intent = new Intent(this, StudentListActivity.class);
            intent.putExtra("formation", selectedAssignment.formation);
            intent.putExtra("section", selectedAssignment.section);
            intent.putExtra("year", selectedAssignment.year);
            intent.putExtra("group", selectedAssignment.group);
            intent.putExtra("moduleName", selectedAssignment.moduleName);
            startActivity(intent);
        });
    }
}