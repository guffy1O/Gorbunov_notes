package com.example.gorbunov_notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private int contextMenuPosition;

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    public int getContextMenuPosition() {
        return contextMenuPosition;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textTitle.setText(note.getTitle());
        holder.textDescription.setText(note.getDescription());
        holder.textDate.setText(note.getDate());

        holder.itemView.setOnLongClickListener(v -> {
            contextMenuPosition = holder.getAdapterPosition();

            PopupMenu popup = new PopupMenu(v.getContext(), v, 0, androidx.appcompat.R.attr.popupMenuStyle, 0);

            popup.getMenu().add(0, 1, 0, "Изменить");
            popup.getMenu().add(0, 2, 0, "Удалить");
            popup.setOnMenuItemClickListener(item -> {
                if (v.getContext() instanceof android.app.Activity) {
                    ((android.app.Activity) v.getContext()).onContextItemSelected(item);
                }
                return true;
            });
            popup.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription, textDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}