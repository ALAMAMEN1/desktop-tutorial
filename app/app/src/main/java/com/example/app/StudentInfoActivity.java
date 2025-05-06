package com.example.app;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentInfoActivity extends AppCompatActivity {

    TextView nameText, emailText, departmentText, studentNumberText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        departmentText = findViewById(R.id.departmentText);
        studentNumberText = findViewById(R.id.studentNumberText);
        db = FirebaseFirestore.getInstance();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        var doc = query.getDocuments().get(0);
                        nameText.setText("الاسم: " + doc.getString("name"));
                        emailText.setText("البريد الإلكتروني: " + doc.getString("email"));
                        departmentText.setText("التخصص: " + doc.getString("department"));
                        studentNumberText.setText("رقم الطالب: " + doc.getId());
                    } else {
                        Toast.makeText(this, "لا توجد بيانات لهذا الطالب", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "فشل في تحميل البيانات", Toast.LENGTH_SHORT).show());
    }
}
