package com.example.challange2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class NoteDetailFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find UI elements
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);

        // Add click listeners to buttons (save, delete, update)
        Button saveButton = view.findViewById(R.id.saveButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button updateButton = view.findViewById(R.id.updateButton);

        saveButton.setOnClickListener(v -> {
            // Save the note
        });

        deleteButton.setOnClickListener(v -> {
            // Delete the note
        });

        updateButton.setOnClickListener(v -> {
            // Update the note
        });
    }

}