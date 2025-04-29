package com.example.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    Button btnScan, btnStudentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        btnScan = findViewById(R.id.btnScan);
        btnStudentInfo = findViewById(R.id.btnStudentInfo);

        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("ضع الكود داخل الإطار");
            options.setBeepEnabled(false); // صوت البيب سامط
            options.setOrientationLocked(true); //باه متقعدش الشاشة دور
            options.setCaptureActivity(CaptureAct.class);
            barLauncher.launch(options);//خادمينو لتحت
        });

        btnStudentInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentInfoActivity.class));
        });
    }

    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String scannedUrl = result.getContents();
                    Log.d("QRScan", "Scanned URL: " + scannedUrl);

                    Intent intent = new Intent(MainActivity.this, SessionActivity.class);
                    intent.putExtra("scannedUrl", scannedUrl);
                    startActivity(intent);
                }
            });
}