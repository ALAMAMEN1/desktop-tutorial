package com.example.studentexamaveragecalculator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class OverallResultsActivity extends AppCompatActivity {
    private TextView resultsText;
    private DatabaseHelper dbHelper;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);

        studentId = getIntent().getIntExtra("studentId", -1);
        dbHelper = new DatabaseHelper(this);
        resultsText = findViewById(R.id.resultsText);

        loadOverallResults();
    }

    private void loadOverallResults() {
        String results = dbHelper.getOverallResults(studentId);
        resultsText.setText(results);
    }
}