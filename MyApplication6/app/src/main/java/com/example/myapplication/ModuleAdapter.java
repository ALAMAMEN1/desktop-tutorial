package com.example.myapplication;

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

    List<Module> moduleList;

    public ModuleAdapter(List<Module> modules) {
        this.moduleList = modules;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_item, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.nameText.setText(module.name + " (Coef: " + module.coefficient + ")");
        holder.tdText.setText("TD: " + module.td);
        holder.tpText.setText("TP: " + module.tp);
        holder.examText.setText("Exam: " + module.exam);
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, tdText, tpText, examText;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.moduleName);
            tdText = itemView.findViewById(R.id.tdInput);
            tpText = itemView.findViewById(R.id.tpInput);
            examText = itemView.findViewById(R.id.examInput);
        }
    }
}
