
package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private int studentId;
    private RecyclerView notesRecyclerView;
    private DatabaseHelper dbHelper;
    private TextView averageText;
    private NoteAdapter adapter;
    private List<Note> notes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        studentId = getIntent().getIntExtra("studentId", -1);
        dbHelper = new DatabaseHelper(this);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        averageText = findViewById(R.id.averageText);

        loadNotes();
    }

    private void loadNotes() {
        notes = dbHelper.getNotesForStudent(studentId);

        adapter = new NoteAdapter(notes, this);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);

        calculateAverage();
    }

    private void calculateAverage() {
        if (notes == null || notes.isEmpty()) {
            averageText.setText("لا توجد نقاط مسجلة");
            return;
        }

        float total = 0;
        int totalCoefficient = 0;

        for (Note note : notes) {
            total += note.getAverage() * note.getCoefficient();
            totalCoefficient += note.getCoefficient();
        }

        if (totalCoefficient != 0) {
            float average = total / totalCoefficient;
            averageText.setText(String.format("المعدل العام: %.2f", average));
        } else {
            averageText.setText("لا توجد نقاط مسجلة");
        }
    }
}
