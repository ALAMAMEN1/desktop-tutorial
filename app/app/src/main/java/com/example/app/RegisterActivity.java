package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, name, department;
    TextView studentNumber;
    Button scanQrBtn, registerBtn;
    String extractedStudentNumber = "";
    FirebaseFirestore db;

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() != null && result.getContents().contains("/check/")) {
                    extractedStudentNumber = result.getContents().substring(result.getContents().lastIndexOf("/") + 1);
                    studentNumber.setText("رقم الطالب: " + extractedStudentNumber);
                } else {
                    Toast.makeText(this, "الرابط غير صالح!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        department = findViewById(R.id.department);
        studentNumber = findViewById(R.id.studentNumber);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        registerBtn = findViewById(R.id.registerBtn);

        db = FirebaseFirestore.getInstance();

        scanQrBtn.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("ضع الكود داخل الإطار");
            options.setBeepEnabled(true);
            options.setOrientationLocked(false);
            options.setCaptureActivity(CaptureAct.class);
            qrLauncher.launch(options);
        });

        registerBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userName = name.getText().toString().trim();
            String userDepartment = department.getText().toString().trim();

            if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty() || userDepartment.isEmpty() || extractedStudentNumber.isEmpty()) {
                Toast.makeText(this, "املأ جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Object> student = new HashMap<>();
                            student.put("name", userName);
                            student.put("email", userEmail);
                            student.put("department", userDepartment);

                            db.collection("students").document(extractedStudentNumber)
                                    .set(student)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "تم التسجيل بنجاح", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "خطأ في حفظ البيانات", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "فشل التسجيل: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
