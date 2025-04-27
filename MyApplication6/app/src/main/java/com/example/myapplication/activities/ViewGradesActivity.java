package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.adapters.NoteAdapter;
import com.example.myapplication.models.Note;
import com.example.myapplication.utils.SessionManager;

import java.util.List;

public class ViewGradesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
    private TextView textViewAverage;
    private DatabaseHelper databaseHelper;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grades);

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        textViewAverage = findViewById(R.id.textViewAverage);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        loadNotes();
    }

    private void loadNotes() {
        String email = SessionManager.getUserEmail(this);
        if (email == null) {
            Toast.makeText(this, "مشكلة في الجلسة", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentId = databaseHelper.getStudentIdByEmail(email);
        if (studentId == -1) {
            Toast.makeText(this, "لم يتم العثور على الطالب", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Note> noteList = databaseHelper.getNotesForStudent(studentId);
        noteAdapter = new NoteAdapter(noteList);
        recyclerViewNotes.setAdapter(noteAdapter);

        calculateAverage(noteList);
    }

    private void calculateAverage(List<Note> notes) {
        double total = 0;
        int totalCoefficient = 0;

        for (Note note : notes) {
            double moyenne = (note.getTd() + note.getTp() + (2 * note.getExam())) / 4.0;
            total += moyenne * note.getCoefficient();
            totalCoefficient += note.getCoefficient();
        }

        if (totalCoefficient > 0) {
            double average = total / totalCoefficient;
            textViewAverage.setText("معدلك: " + String.format("%.2f", average));
        } else {
            textViewAverage.setText("لا توجد نقاط لحساب المعدل");
        }
    }
}
