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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //ToolbarHelper.setupToolbarButtons(this, R.drawable.baseline_arrow_back_24, R.drawable.baseline_save_24);

        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Find UI elements
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText contentEditText = view.findViewById(R.id.contentEditText);


    }

}