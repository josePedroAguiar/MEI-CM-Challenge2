package com.example.challange2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

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

    protected void retriveNotes(FirebaseUser currentUser){
        // Assuming db is your instance of FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        retriveNotes(currentUser);
        if (currentUser == null) {
            loadLoginFragment();
        }
        else {
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            loadHomeFragment();
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
                homeFragment.showSearchDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_new_note) {
            // Handle search action
                homeFragment.addNewNote("New Title","");
            return true;
        }
        if (item.getItemId() == R.id.action_back) {

            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            //noteDetailFragment.saveNote()
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



}
