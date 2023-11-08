package com.example.challange2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.challange2.note.Note;
import com.example.challange2.note.ToolbarHelper;

import java.util.ArrayList;
import java.util.List;


public class NoteDetailFragment extends Fragment {
    int position;

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        //getActivity().onCreateOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        // Retrieve note ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            position= args.getInt("int", 0);
            String title = args.getString("title", "");
            String content = args.getString("content", "");
            EditText titleEditText = view.findViewById(R.id.titleEditText);
            EditText contentEditText = view.findViewById(R.id.contentEditText);

            titleEditText.setText(title);
            contentEditText.setText(content);
            List<Note> dummyNotes = ((MainActivity) requireActivity()).dummyNotes;
            dummyNotes.get(position);
        }
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find UI elements
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);

        String newTitle = titleEditText.getText().toString().trim();
        String newContent = contentEditText.getText().toString().trim();

        // Check if both title and content are not empty
        if (!newTitle.isEmpty() && !newContent.isEmpty() && position >= 0) {
            // Update the note in the list
            Note updatedNote = new Note(newTitle, newContent);
            ((MainActivity) requireActivity()).dummyNotes.set(position, updatedNote);

            // Notify the adapter that the data set has changed
            ((MainActivity) requireActivity()).homeFragment.noteListAdapter.notifyDataSetChanged();

            // Save the updated note in Firebase (you can add your Firebase code here)
        }


    }

}