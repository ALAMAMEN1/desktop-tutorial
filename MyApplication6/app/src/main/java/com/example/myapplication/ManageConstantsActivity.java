package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageConstantsActivity extends AppCompatActivity {

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_constants);

        db = new DatabaseHelper(this);

        setupBlock(R.id.formationBlock, "formation");
        setupBlock(R.id.sectionBlock, "section");
        setupBlock(R.id.yearBlock, "year");
        setupBlock(R.id.groupBlock, "group");
        setupBlock(R.id.moduleBlock, "module");
    }

    private void setupBlock(int blockId, String type) {
        View block = findViewById(blockId);

        TextView title = block.findViewById(R.id.blockTitle);
        Spinner spinner = block.findViewById(R.id.blockSpinner);
        EditText input = block.findViewById(R.id.blockInput);
        Button addButton = block.findViewById(R.id.blockAddButton);

        // وضع اسم العنوان حسب النوع
        switch (type) {
            case "formation": title.setText("📚 التكوينات"); break;
            case "section": title.setText("🏛️ الشعب"); break;
            case "year": title.setText("🎓 السنوات"); break;
            case "group": title.setText("👥 المجموعات"); break;
            case "module": title.setText("📖 المواد"); break;
        }

        loadSpinner(spinner, type);

        addButton.setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                boolean success = db.insertConstant(type, value);
                if (success) {
                    Toast.makeText(this, "تمت الإضافة ✅", Toast.LENGTH_SHORT).show();
                    input.setText("");
                    loadSpinner(spinner, type);
                } else {
                    Toast.makeText(this, "موجودة مسبقاً!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinner.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedValue = (String) parent.getItemAtPosition(position);

            new AlertDialog.Builder(this)
                    .setTitle("تأكيد الحذف")
                    .setMessage("هل تريد حذف \"" + selectedValue + "\" ؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        db.deleteConstant(type, selectedValue);
                        Toast.makeText(this, "تم الحذف ❌", Toast.LENGTH_SHORT).show();
                        loadSpinner(spinner, type);
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();

            return true;
        });
    }

    private void loadSpinner(Spinner spinner, String type) {
        List<String> items = db.getConstants(type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
