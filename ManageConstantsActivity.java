package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class ManageConstantsActivity extends AppCompatActivity {

    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_constants);

        typeSpinner = findViewById(R.id.typeSpinner);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // عملية عند الاختيار
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // عملية عند عدم الاختيار
            }
        });
    }
}