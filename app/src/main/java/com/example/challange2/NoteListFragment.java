package com.example.challange2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NoteListFragment extends Fragment  implements NoteListAdapter.OnNoteClickListener, NoteListAdapter.OnNoteLongClickListener   {

    List<Note> dummyNotes = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notelist, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy data (replace with actual data source)

        dummyNotes.add(new Note("Note 1", "Content for Note 1"));
        dummyNotes.add(new Note("Note 2", "Content for Note 2"));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.noteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        NoteListAdapter adapter = new NoteListAdapter(dummyNotes);
        recyclerView.setAdapter(adapter);
        adapter.setOnNoteClickListener(this);
        adapter.setOnNoteLongClickListener(this);

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
        Toast.makeText(getContext(), "LONG CLICK NOTE.", Toast.LENGTH_SHORT).show();

    }
}