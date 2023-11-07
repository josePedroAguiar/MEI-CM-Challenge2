package com.example.challange2.note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challange2.R;
import com.example.challange2.note.Note;
import com.example.challange2.note.NoteViewHolder;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;
import android.widget.Filterable;


public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> implements Filterable {
    private List<Note> notes;
    private OnNoteClickListener onNoteClickListener;
    private List<Note> filteredNotes=new ArrayList<>();
    private  String filterPattern="";
    private OnNoteLongClickListener onNoteLongClickListener;


    // Constructor and other methods

    public NoteListAdapter(List<Note> notes) {
        this.notes = notes;
    }
    public interface OnNoteClickListener {
        void onNoteClick(int position);
    }

    public interface OnNoteLongClickListener {
        void onNoteLongClick(int position);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public void setOnNoteLongClickListener(OnNoteLongClickListener listener) {
        this.onNoteLongClickListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getTitle());

        holder.itemView.setOnClickListener(view -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (onNoteLongClickListener != null) {
                onNoteLongClickListener.onNoteLongClick(position);
            }
            return true; // Consume the long-click event
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    // Filter implementation
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filterPattern=constraint.toString().toLowerCase().trim();;
                String filterPattern = constraint.toString().toLowerCase().trim();

                List<Note> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty()) {
                    filteredList.addAll(notes);
                } else {
                    for (Note note : notes) {
                        if (note.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(note);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notes.clear();  // Clear the previous filtered results
                notes.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public List<Note> getFilteredNotes() {
        return filteredNotes;
    }

    public String getFilterPattern() {
        return filterPattern;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }
}
