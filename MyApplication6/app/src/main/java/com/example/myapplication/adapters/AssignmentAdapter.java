package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentList;

    public AssignmentAdapter(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.textViewTeacher.setText("الأستاذ: " + assignment.getTeacher());
        holder.textViewSubject.setText("المادة: " + assignment.getSubject());
        holder.textViewGroup.setText("التكوين: " + assignment.getFormation() +
                " | الشعبة: " + assignment.getSection() +
                " | السنة: " + assignment.getYear() +
                " | المجموعة: " + assignment.getGroupName());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTeacher, textViewSubject, textViewGroup;

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTeacher = itemView.findViewById(R.id.textViewTeacher);
            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewGroup = itemView.findViewById(R.id.textViewGroup);
        }
    }
}
