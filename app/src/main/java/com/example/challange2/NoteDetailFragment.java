package com.example.challange2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.challange2.note.Note;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NoteDetailFragment extends Fragment {
    int position;
    EditText titleEditText;
    EditText contentEditText;
    Note note;

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
            position = args.getInt("position", 0);
            String title = args.getString("title", "");
            String content = args.getString("content", "");
            titleEditText = view.findViewById(R.id.titleEditText);
            contentEditText = view.findViewById(R.id.contentEditText);

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
        titleEditText = view.findViewById(R.id.titleEditText);
        contentEditText = view.findViewById(R.id.contentEditText);


    }

    public void saveChanges() {
        String newTitle = titleEditText.getText().toString().trim();
        String newContent = contentEditText.getText().toString().trim();
        note = ((MainActivity) requireActivity()).dummyNotes.get(position);
        if (!newTitle.isEmpty()) {
            // Update the note in the list
            Note updatedNote = new Note(newTitle, newContent, note.getId());
            Date currentDate = Calendar.getInstance().getTime();
            updatedNote.setDate(currentDate);
            note = updatedNote;

            List<Note> dummyNotes = ((MainActivity) requireActivity()).dummyNotes;
            List<Note> originalDummyNotes = ((MainActivity) requireActivity()).originalDummyNotes;

            if (dummyNotes.size() == originalDummyNotes.size()) {
                dummyNotes.set(position, updatedNote);
                originalDummyNotes.set(position, updatedNote);
            } else {
                dummyNotes.set(position, updatedNote);
                for (int i = 0; i < originalDummyNotes.size(); i++) {
                    if (originalDummyNotes.get(i).getId().equals(updatedNote.getId())) {
                        originalDummyNotes.set(i, updatedNote);
                        break;
                    }
                }
            }

            ((MainActivity) requireActivity()).sortNotesByDate(((MainActivity) requireActivity()).dummyNotes);
            ((MainActivity) requireActivity()).sortNotesByDate(((MainActivity) requireActivity()).originalDummyNotes);

            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment noteListFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
            if (noteListFragment instanceof NoteListFragment) {
                // Notify the adapter that the data set has changed
                ((NoteListFragment) noteListFragment).noteListAdapter.notifyDataSetChanged();
            }
        }
    }

}