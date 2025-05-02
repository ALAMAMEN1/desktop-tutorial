package com.example.studentexamaveragecalculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SemesterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private List<Module> moduleList = new ArrayList<>();
    private TextView resultTextView;
    private int currentSemester;
    private RequestQueue requestQueue;
    private DatabaseHelper dbHelper;
    private int studentId;

    private final String URL_SEMESTER1 = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";
    private final String URL_SEMESTER2 = "https://num.univ-biskra.dz/psp/formations/get_modules_json?sem=1&spec=184";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        currentSemester = getIntent().getIntExtra("SEMESTER", 1);
        studentId = getIntent().getIntExtra("studentId", -1);
        dbHelper = new DatabaseHelper(this);
        requestQueue = Volley.newRequestQueue(this);

        initializeViews();
        setupRecyclerView();
        setupCalculateButton();
        loadData();
    }
    private void loadData() {
        if (dbHelper.hasModulesForSemester(currentSemester)) {
            List<Module> localModules = dbHelper.getModulesBySemester(String.valueOf(currentSemester));
            updateModuleList(localModules);
        } else {
            fetchDataFromServer();
        }
    }

    private void fetchDataFromServer() {
        String url = (currentSemester == 1) ? URL_SEMESTER1 : URL_SEMESTER2;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    Log.d("SemesterActivity", "JSON Response: " + response.toString());
                    parseJsonData(response);
                    saveModulesToDatabase(response);
                },
                error -> {
                    resultTextView.setText("خطأ في جلب البيانات من الخادم");
                    Log.e("SemesterActivity", "Error fetching data: " + error.getMessage());
                }
        );

        requestQueue.add(request);
    }

    private void parseJsonData(JSONArray response) {
        try {
            List<Module> modules = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Module module = new Module();
                module.name = obj.getString("Nom_module");
                module.coefficient = obj.getInt("Coefficient");
                module.semester = String.valueOf(currentSemester);
                modules.add(module);
            }
            updateModuleList(modules);
        } catch (JSONException e) {
            resultTextView.setText("خطأ في تحليل البيانات");
            e.printStackTrace();
        }
    }

    private void saveModulesToDatabase(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() == 0) {
            resultTextView.setText("لا توجد بيانات لحفظها");
            return;
        }

        dbHelper.importModulesFromJson(this, currentSemester, jsonArray, new DatabaseHelper.ImportCallback() {
            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    List<Module> updatedModules = dbHelper.getModulesBySemester(String.valueOf(currentSemester));
                    if (updatedModules.isEmpty()) {
                        resultTextView.setText("لم يتم حفظ الوحدات بشكل صحيح");
                    } else {
                        updateModuleList(updatedModules);
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    resultTextView.setText("خطأ في حفظ البيانات: " + message);
                    Log.e("SemesterActivity", "Save error: " + message);
                });
            }
        });
    }

    private void updateModuleList(List<Module> modules) {
        runOnUiThread(() -> {
            moduleList.clear();
            moduleList.addAll(modules);
            adapter.notifyDataSetChanged();

            if (modules.isEmpty()) {
                resultTextView.setText("لا توجد مواد متاحة لهذا الفصل");
            }
        });
    }



    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        resultTextView = findViewById(R.id.resultTextView);

        TextView title = findViewById(R.id.semesterTitle);
        title.setText("الفصل " + currentSemester);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ModuleAdapter(moduleList);
        recyclerView.setAdapter(adapter);
    }

    private void setupCalculateButton() {
        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculateSemesterAverage());
    }

    private void calculateSemesterAverage() {
        if (moduleList.isEmpty()) {
            resultTextView.setText("لا توجد بيانات لحساب المعدل");
            return;
        }

        double total = 0;
        double sumCoeff = 0;
        StringBuilder details = new StringBuilder("تفاصيل المواد:\n\n");
        boolean hasInvalidScores = false;

        for (Module module : moduleList) {
            if ((module.td < 0 || module.td > 20) ||
                    (module.tp < 0 || module.tp > 20) ||
                    (module.exam < 0 || module.exam > 20)) {
                hasInvalidScores = true;
                break;
            }
        }

        if (hasInvalidScores) {
            resultTextView.setText("بعض القيم غير صالحة. يجب أن تكون جميع الدرجات بين 0 و 20");
            return;
        }

        for (Module module : moduleList) {
            double avg = module.getModuleAverage();
            total += avg * module.coefficient;
            sumCoeff += module.coefficient;

            details.append(String.format(
                    "%s: %.2f (معامل: %d)\nTD: %.1f | TP: %.1f | Exam: %.1f\n\n",
                    module.name, avg, module.coefficient,
                    module.td, module.tp, module.exam
            ));
        }

        double semesterAvg = sumCoeff > 0 ? total / sumCoeff : 0;

        String result = String.format(
                "معدل الفصل %d: %.2f\nالحالة: %s\n\n%s",
                currentSemester, semesterAvg,
                semesterAvg >= 10 ? "ناجح" : "راسب",
                details.toString()
        );

        if (dbHelper.saveStudentResults(studentId, moduleList)) {
            resultTextView.setText(result);
        } else {
            resultTextView.setText("حدث خطأ في حفظ النتائج");
        }
    }
}