package com.example.myapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.adapters.AssignmentAdapter;
import com.example.myapplication.models.Assignment;

import java.util.List;

public class ViewAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAssignments;
    private DatabaseHelper databaseHelper;
    private AssignmentAdapter assignmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignments);

        recyclerViewAssignments = findViewById(R.id.recyclerViewAssignments);
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        loadAssignments();
    }

    private void loadAssignments() {
        List<Assignment> assignmentList = databaseHelper.getAllAssignments();
        assignmentAdapter = new AssignmentAdapter(assignmentList);
        recyclerViewAssignments.setAdapter(assignmentAdapter);
    }
}
