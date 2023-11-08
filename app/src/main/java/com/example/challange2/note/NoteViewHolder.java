package com.example.challange2.note;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challange2.R;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    public TextView titleTextView;
    public TextView contentTextView;
    public TextView dateTextView;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        contentTextView = itemView.findViewById(R.id.contentTextView);
        dateTextView = itemView.findViewById(R.id.msgtime);

    }
}

