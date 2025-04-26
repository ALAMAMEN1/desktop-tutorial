package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> students;
    private String moduleName;
    private int coefficient;
    private Context context;

    public StudentAdapter(List<Student> students, String moduleName, int coefficient) {
        this.students = students;
        this.moduleName = moduleName;
        this.coefficient = coefficient;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        Button addNoteButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            addNoteButton = itemView.findViewById(R.id.buttonAddNote);
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.studentName.setText(student.getName());

        if (moduleName != null) {
            holder.addNoteButton.setVisibility(View.VISIBLE);
            holder.addNoteButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, NoteInputActivity.class);
                intent.putExtra("studentId", student.getId());
                intent.putExtra("studentName", student.getName());
                intent.putExtra("moduleName", moduleName);
                intent.putExtra("moduleCoefficient", coefficient);
                context.startActivity(intent);
            });
        } else {
            holder.addNoteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}