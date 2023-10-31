package com.example.challange2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.challange2.note.Note;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public List<Note> dummyNotes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dummyNotes.add(new Note("Note 1", "Content for Note 1"));
        dummyNotes.add(new Note("Note 2", "Content for Note 2"));
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

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
        LoginFragment loginFragment = new LoginFragment();

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
        NoteListFragment homeFragment = new NoteListFragment();

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


}
