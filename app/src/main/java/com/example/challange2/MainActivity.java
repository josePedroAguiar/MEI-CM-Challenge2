package com.example.challange2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Toast;

import com.example.challange2.note.Note;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public List<Note> dummyNotes = new ArrayList<>();
    NoteListFragment homeFragment = new NoteListFragment();
    LoginFragment loginFragment = new LoginFragment();
    NoteDetailFragment noteDetailFragment = new NoteDetailFragment();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    protected void retriveNotes(FirebaseUser currentUser){
        // Assuming "notes" is your collection name
            db.collection(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // document.getData() will contain the note's data
                                String title = (String) document.get("title");
                                String content = (String) document.get("content");
                                dummyNotes.add(new Note(title, content));
                                Log.d("Debug", "Retrieved notes: " + dummyNotes.size());
                                homeFragment.noteListAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(this, dummyNotes.size(), Toast.LENGTH_SHORT).show();

                            // Handle errors here
                        }
                    });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            loadLoginFragment();
        }
        else {
            retriveNotes(currentUser);
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            loadHomeFragment();
            //homeFragment.noteListAdapter.notifyDataSetChanged();
        }

    }
    private void loadLoginFragment() {
        // Create a new instance of LoginFragment


        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the existing fragment (if any) with the new one
        transaction.replace(R.id.fragmentContainer, loginFragment);

        // Commit the transaction
        transaction.commit();
    }
    private void loadHomeFragment() {
        // Create a new instance of HomeFragment


        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the existing fragment (if any) with the new one
        transaction.replace(R.id.fragmentContainer, homeFragment);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        // Get the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        // Check if the current fragment is the HomeFragment
        if (currentFragment instanceof NoteListFragment) {
            // Log out the user
            FirebaseAuth.getInstance().signOut();
            loadLoginFragment();
        } else {
            //super.getSupportFragmentManager().popBackStack();
            super.onBackPressed(); // If not on HomeFragment, proceed with default behavior
        }
    }
   /* public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if(homeFragment.isVisible()) {
            menu.clear();
        inflater.inflate(R.menu.main_menu, menu);}
        if(noteDetailFragment.isVisible()) {
            menu.clear();
            inflater.inflate(R.menu.detail_menu, menu);
        }

        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // Handle search action
                showSearchDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_new_note) {
            // Handle search action
            addNewNote("New Title","");
            return true;
        }
        if (item.getItemId() == R.id.action_back) {
            back();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            //noteDetailFragment.saveNote()
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    public void addNewNote(String title, String content) {
        // Create a new Note object
        Note newNote = new Note(title, content);

        // Add the new note to the list
        dummyNotes.add(newNote);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser!=null) {
            db.collection(currentUser.getEmail())
                    .add(newNote)
                    .addOnSuccessListener(documentReference -> {
                        // Note added successfully
                        String noteId = documentReference.getId();
                        // You can do further operations here if needed
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors here
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            // Notify the adapter that the data set has changed

        }

    }

    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search by Title");

        // Create an EditText to allow the user to input a search query
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String searchQuery = input.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                homeFragment.noteListAdapter.getFilter().filter(searchQuery);


            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    public void back() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof NoteDetailFragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < backStackEntryCount; i++) {
                fragmentManager.popBackStack();
            }
            // If it is, navigate back to the NoteListFragment
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, homeFragment);
            transaction.commit();

        } else {
            super.onBackPressed(); // If not on NoteDetailFragment, proceed with default behavior
        }

    }




}
