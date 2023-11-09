package com.example.challange2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challange2.note.Note;
import com.example.challange2.note.NoteListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class NoteListFragment extends Fragment implements NoteListAdapter.OnNoteClickListener, NoteListAdapter.OnNoteLongClickListener {

    List<Note> dummyNotes = new ArrayList<>();
    NoteListAdapter noteListAdapter;

    EditText titleEditText;
    Note note;

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        //getActivity().onCreateOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notelist, container, false);
        /*Button clearFilterButton;
        clearFilterButton = view.findViewById(R.id.clearFilterButton);
        noteListAdapter = new NoteListAdapter(dummyNotes);
        if(noteListAdapter.getFilterPattern().equals(""))
            clearFilterButton.setVisibility(View.INVISIBLE);
        else
            clearFilterButton.setVisibility(View.VISIBLE);
        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearFilterButtonClick(v);
            }
        });*/
        //ArrayList<ImageButton>buttons=setupToolbarButtons(R.drawable.baseline_add_24, R.drawable.baseline_search_24);
        /*buttons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click (e.g., save action)
                addNewNote("New Note","");
                noteListAdapter.notifyDataSetChanged();
                NoteDetailFragment fragment = new NoteDetailFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
        buttons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click (e.g., save action)


            }
        });*/

        Toolbar toolbar = getActivity().findViewById(R.id.tb);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }


        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        // Dummy data (replace with actual data source)
        if (getActivity() instanceof MainActivity) {
            dummyNotes = ((MainActivity) getActivity()).dummyNotes;
            sortNotesByTitle(dummyNotes);
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
        NoteDetailFragment noteDetailFragment = new NoteDetailFragment();
        args.putInt("position", position);
        args.putString("title", dummyNotes.get(position).getTitle());
        args.putString("content", dummyNotes.get(position).getContent());

        Toast.makeText(getContext(), "CLICK NOTE.", Toast.LENGTH_SHORT).show();

        noteDetailFragment.setArguments(args);

        // Navigate to NoteDetailFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, noteDetailFragment);
        transaction.addToBackStack(null); // Add to back stack
        transaction.commit();


    }

    @Override
    public void onNoteLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Erase Note", "Change Title", "Cancel"}, (dialog, which) -> {
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
        Note noteToRemove = dummyNotes.get(position);
        dummyNotes.remove(position);

        // Notify the adapter that the data set has changed
        noteListAdapter.notifyDataSetChanged();
        ((MainActivity) requireActivity()).eraseNoteInFirestore(noteToRemove);


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
                Date currentDate = Calendar.getInstance().getTime();
                dummyNotes.get(position).setDate(currentDate);

                // Notify the adapter that the data set has changed
                noteListAdapter.notifyDataSetChanged();
                ((MainActivity) requireActivity()).updateNoteTitleInFirestore(position);


            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void onClearFilterButtonClick(View view) {
        // Implement the logic to clear the filter here
        if (noteListAdapter != null) {
            noteListAdapter.setNotes(dummyNotes);
            noteListAdapter.notifyDataSetChanged();
            noteListAdapter.setFilterPattern(""); // Passing an empty string will clear the filter


        }
    }

    private void sortNotesByTitle(List<Note> notes) {
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note note1, Note note2) {
                // Comparação decrescente usando a data
                return note2.getDate().compareTo(note1.getDate());
            }
        });
    }


}