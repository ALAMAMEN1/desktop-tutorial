package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, tdText, tpText, examText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectName);
            tdText = itemView.findViewById(R.id.tdValue);
            tpText = itemView.findViewById(R.id.tpValue);
            examText = itemView.findViewById(R.id.examValue);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.subjectText.setText(note.getSubject());
        holder.tdText.setText("TD: " + note.getTd());
        holder.tpText.setText("TP: " + note.getTp());
        holder.examText.setText("Exam: " + note.getExam());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
