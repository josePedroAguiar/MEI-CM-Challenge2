package com.example.challange2;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.challange2.note.Note;
import com.example.challange2.note.NoteListAdapter;

import java.util.ArrayList;
import java.util.List;


public class NoteListFragment extends Fragment  implements NoteListAdapter.OnNoteClickListener, NoteListAdapter.OnNoteLongClickListener   {

    List<Note> dummyNotes = new ArrayList<>();
    private NoteListAdapter noteListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notelist, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy data (replace with actual data source)
        if (getActivity() instanceof MainActivity) {
            dummyNotes=((MainActivity) getActivity()).dummyNotes;
        }

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.noteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteListAdapter = new NoteListAdapter(dummyNotes);
        recyclerView.setAdapter(noteListAdapter);
        noteListAdapter.setOnNoteClickListener(this);
        noteListAdapter.setOnNoteLongClickListener(this);

    }

    public void onNoteClick(int position) {
        // Navigate to NoteDetailFragment and pass the selected note's data as arguments
        Bundle args = new Bundle();
        args.putString("title", dummyNotes.get(position).getTitle());
        args.putString("content", dummyNotes.get(position).getContent());
        Toast.makeText(getContext(), "CLICK NOTE.", Toast.LENGTH_SHORT).show();
        NoteDetailFragment fragment = new NoteDetailFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNoteLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Erase Note", "Change Title","Cancel"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Erase Note
                            eraseNote(position);
                            break;
                        case 1:
                            // Change Title
                            changeTitle(position);
                            break;
                        default:
                            // Nothing
                            break;
                    }
                })
                .show();
        Toast.makeText(getContext(), "LONG CLICK NOTE.", Toast.LENGTH_SHORT).show();

    }
    private void eraseNote(int position) {
        //TODO: Implement the logic to erase the note
            // Remove the note from the list
            dummyNotes.remove(position);

            // Notify the adapter that the data set has changed
            noteListAdapter.notifyDataSetChanged();

    }

    private void changeTitle(int position) {
        //TODO: Implement the logic to change the title
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Title");

        // Create an EditText to allow the user to input a new title
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                // Update the note with the new title
                dummyNotes.get(position).setTitle(newTitle);

                // Notify the adapter that the data set has changed
                noteListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}