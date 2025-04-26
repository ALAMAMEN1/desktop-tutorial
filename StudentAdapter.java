package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private List<Student> studentList;
    private StudentManagementActivity activity;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
        if (context instanceof StudentManagementActivity) {
            this.activity = (StudentManagementActivity) context;
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.email.setText(student.getEmail());
        holder.formation.setText(student.getFormation());
        holder.section.setText(student.getSection());
        holder.year.setText(student.getYear());
        holder.group.setText(student.getGroup());

        holder.updateButton.setOnClickListener(v -> {
            String newFormation = activity.formationSpinner.getSelectedItem().toString();
            String newSection = activity.sectionSpinner.getSelectedItem().toString();
            String newYear = activity.yearSpinner.getSelectedItem().toString();
            String newGroup = activity.groupSpinner.getSelectedItem().toString();
            activity.updateStudentFormation(student.getId(), newFormation, newSection, newYear, newGroup);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, formation, section, year, group;
        Button updateButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textStudentName);
            email = itemView.findViewById(R.id.textStudentEmail);
            formation = itemView.findViewById(R.id.textFormation);
            section = itemView.findViewById(R.id.textSection);
            year = itemView.findViewById(R.id.textYear);
            group = itemView.findViewById(R.id.textGroup);
            updateButton = itemView.findViewById(R.id.buttonUpdateStudent);
        }
    }
}
