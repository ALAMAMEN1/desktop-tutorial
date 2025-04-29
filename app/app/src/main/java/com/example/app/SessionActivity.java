package com.example.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionActivity extends AppCompatActivity {

    private static final String TAG = "SessionActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FirebaseFirestore db;
    private String sessionId, studentNumber, qrType, scanCode;
    private double qrLatitude, qrLongitude;
    private boolean attendanceConfirmed = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String scannedUrl = getIntent().getStringExtra("scannedUrl");
        if (scannedUrl == null || scannedUrl.isEmpty()) {
            showErrorAndFinish("لم يتم إرسال رابط!");
            return;
        }

        Uri uri = Uri.parse(scannedUrl);
        sessionId = uri.getQueryParameter("session");
        qrType = uri.getQueryParameter("type");
        scanCode = uri.getQueryParameter("code");

        try {
            qrLatitude = Double.parseDouble(uri.getQueryParameter("lat"));
            qrLongitude = Double.parseDouble(uri.getQueryParameter("lng"));
        } catch (Exception e) {
            showErrorAndFinish("إحداثيات غير صحيحة!");
            return;
        }

        if (!isOnline()) {
            showErrorAndFinish("لا يوجد اتصال بالإنترنت!");
            return;
        }

        loadSessionData();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        var doc = query.getDocuments().get(0);
                        studentNumber = doc.getId();
                        loadStudentData();
                        checkExistingAttendance();

                        Button confirmBtn = findViewById(R.id.confirmBtn);
                        if (confirmBtn != null) {
                            confirmBtn.setOnClickListener(v -> {
                                v.setEnabled(false);
                                checkLocationAndConfirm();
                            });
                        }
                    } else {
                        showErrorAndFinish("لم يتم العثور على حساب الطالب!");
                    }
                })
                .addOnFailureListener(e -> showErrorAndFinish("فشل في جلب بيانات الطالب!"));
    }

    private void checkLocationAndConfirm() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location == null) {
                        showError("لم يتم تحديد الموقع الحالي!");
                        return;
                    }

                    float[] results = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(), qrLatitude, qrLongitude, results);
                    float distanceInMeters = results[0];

                    if (distanceInMeters > 100) {
                        showError("يجب أن تكون في موقع الحصة (ضمن 100 متر)!");
                    } else {
                        confirmAttendance();
                    }
                })
                .addOnFailureListener(e -> showError("فشل في تحديد الموقع!"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationAndConfirm();
        } else {
            showError("يرجى السماح بالوصول إلى الموقع لتأكيد الحضور!");
        }
    }

    private void loadSessionData() {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "بيانات الجلسة غير موجودة!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        String subject = doc.getString("subject");
                        String teacher = doc.getString("teacher");
                        String sessionLocation = doc.getString("location");

                        Long timestamp = doc.getLong("date");
                        String date = (timestamp != null)
                                ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp))
                                : "غير متوفرة";

                        ((TextView) findViewById(R.id.subject)).setText("\uD83D\uDCDA " + subject);
                        ((TextView) findViewById(R.id.teacher)).setText("\uD83D\uDC68\uD83C\uDFFC\u200D\uD83C\uDFEB " + teacher);
                        ((TextView) findViewById(R.id.date)).setText("\uD83D\uDCC5 " + date);
                        ((TextView) findViewById(R.id.location)).setText("\uD83D\uDCCD الموقع: " + sessionLocation);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing session data: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "فشل في تحميل بيانات الجلسة!", Toast.LENGTH_SHORT).show());
    }

    private void loadStudentData() {
        db.collection("students").document(studentNumber).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    String name = doc.getString("name");
                    ((TextView) findViewById(R.id.name)).setText("\uD83D\uDC64 " + name);
                    ((TextView) findViewById(R.id.number)).setText("\uD83D\uDD22 " + studentNumber);
                });
    }

    private void checkExistingAttendance() {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    Map<String, Object> students = (Map<String, Object>) doc.get("students");
                    if (students != null && students.containsKey(studentNumber)) {
                        Map<String, Object> data = (Map<String, Object>) students.get(studentNumber);
                        String condition = (String) data.get("condition");
                        if ("present".equals(condition)) {
                            ((TextView) findViewById(R.id.status)).setText("\uD83D\uDCCC الحالة: مؤكد (حاضر)");
                            attendanceConfirmed = true;
                            findViewById(R.id.confirmBtn).setEnabled(false);
                        }
                    }
                });
    }

    private void confirmAttendance() {
        if (attendanceConfirmed || !isOnline()) {
            Toast.makeText(this, "تم التأكيد مسبقًا أو لا يوجد اتصال!", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("entry".equals(qrType)) {
            handleEntryScan();
        } else if ("exit".equals(qrType)) {
            handleExitScan();
        }
    }

    private void handleEntryScan() {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String correctCode = doc.getString("entryCode");
                    if (!scanCode.equals(correctCode)) {
                        showError("رمز الدخول غير صحيح!");
                        return;
                    }

                    Map<String, Object> update = new HashMap<>();
                    update.put("students." + studentNumber + ".entryTime", System.currentTimeMillis());
                    update.put("students." + studentNumber + ".condition", "entry_recorded");

                    db.collection("sessions").document(sessionId).update(update)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "✅ تم تسجيل الدخول!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> showError("فشل في تسجيل الدخول!"));
                });
    }

    private void handleExitScan() {
        db.collection("sessions").document(sessionId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String correctCode = doc.getString("exitCode");
                    if (!scanCode.equals(correctCode)) {
                        showError("رمز الخروج غير صحيح!");
                        return;
                    }

                    Map<String, Object> students = (Map<String, Object>) doc.get("students");
                    if (students == null || !students.containsKey(studentNumber)) {
                        showError("لم يتم تسجيل الدخول!");
                        return;
                    }

                    Map<String, Object> studentData = (Map<String, Object>) students.get(studentNumber);
                    if (!"entry_recorded".equals(studentData.get("condition"))) {
                        showError("لم يتم تسجيل الدخول أولاً!");
                        return;
                    }

                    long entryTime = (long) studentData.get("entryTime");
                    long exitTime = System.currentTimeMillis();
                    Long startTime = doc.getLong("startTime");
                    Long endTime = doc.getLong("endTime");

                    Map<String, Object> update = new HashMap<>();
                    update.put("students." + studentNumber + ".exitTime", exitTime);

                    if (startTime != null && endTime != null && entryTime >= startTime && exitTime <= endTime) {
                        update.put("students." + studentNumber + ".condition", "present");
                    } else {
                        update.put("students." + studentNumber + ".condition", "incomplete");
                    }

                    db.collection("sessions").document(sessionId).update(update)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "✅ تم تسجيل الخروج!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> showError("فشل في تسجيل الخروج!"));
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Button confirmBtn = findViewById(R.id.confirmBtn);
        if (confirmBtn != null) confirmBtn.setEnabled(true);
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkNetworkConnectionModern(cm);
        } else {
            return checkNetworkConnectionLegacy(cm);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkNetworkConnectionModern(ConnectivityManager cm) {
        try {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null &&
                    (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } catch (Exception e) {
            Log.e(TAG, "Modern network check failed: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean checkNetworkConnectionLegacy(ConnectivityManager cm) {
        try {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e(TAG, "Legacy network check failed: " + e.getMessage());
            return false;
        }
    }
}