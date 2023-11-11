package com.example.challange2.note;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotesManager {
    private static final String PREF_NAME = "NotesPref";
    private static final String NOTES_KEY = "NotesList";

    public static void saveNotes(Context context, List<Note> notesList) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonNotes = gson.toJson(notesList);
        editor.putString(NOTES_KEY, jsonNotes);
        editor.apply();
    }

    public static List<Note> getNotes(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonNotes = preferences.getString(NOTES_KEY, "");
        Note[] notesArray = gson.fromJson(jsonNotes, Note[].class);
        if (notesArray != null) {
            return new ArrayList<>(Arrays.asList(notesArray));
        } else {
            return new ArrayList<>();
        }
    }
}
