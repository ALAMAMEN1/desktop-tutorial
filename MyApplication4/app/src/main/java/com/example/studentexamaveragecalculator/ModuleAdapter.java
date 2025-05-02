package com.example.studentexamaveragecalculator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private final List<Module> modules;

    public ModuleAdapter(List<Module> modules) {
        this.modules = modules;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = modules.get(position);

        holder.moduleName.setText(module.name);
        holder.moduleCoeff.setText("Coeff: " + module.coefficient);

        setupEditText(holder.tdInput, value -> module.td = value);
        setupEditText(holder.tpInput, value -> module.tp = value);
        setupEditText(holder.examInput, value -> module.exam = value);

        holder.tdInput.setText(module.td > 0 ? String.valueOf(module.td) : "");
        holder.tpInput.setText(module.tp > 0 ? String.valueOf(module.tp) : "");
        holder.examInput.setText(module.exam > 0 ? String.valueOf(module.exam) : "");
    }

    private void setupEditText(EditText editText, OnValueChanged listener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        double value = Double.parseDouble(s.toString());
                        // Validate score range (0-20)
                        if (value < 0 || value > 20) {
                            editText.setError("يجب أن تكون القيمة بين 0 و 20");
                            listener.onValueChanged(0);
                        } else {
                            listener.onValueChanged(value);
                        }
                    } else {
                        listener.onValueChanged(0);
                    }
                } catch (NumberFormatException e) {
                    editText.setError("قيمة غير صالحة");
                    listener.onValueChanged(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName, moduleCoeff;
        EditText tdInput, tpInput, examInput;

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.moduleName);
            moduleCoeff = itemView.findViewById(R.id.moduleCoeff);
            tdInput = itemView.findViewById(R.id.tdInput);
            tpInput = itemView.findViewById(R.id.tpInput);
            examInput = itemView.findViewById(R.id.examInput);
        }
    }
    public void updateModules(List<Module> newModules) {
        this.modules.clear();
        this.modules.addAll(newModules);
        notifyDataSetChanged();
    }

    interface OnValueChanged {
        void onValueChanged(double value);
    }
}