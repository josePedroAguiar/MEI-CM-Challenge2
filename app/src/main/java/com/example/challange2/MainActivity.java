package com.example.challange2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.challange2.note.Note;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public List<Note> dummyNotes = new ArrayList<>();
    NoteListFragment homeFragment = new NoteListFragment();
    LoginFragment loginFragment = new LoginFragment();
    NoteDetailFragment noteDetailFragment = new NoteDetailFragment();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    Handler mainHandler = new Handler(Looper.getMainLooper());  // Crie o Handler na thread principal

    public static String generateRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    protected void retriveNotes(FirebaseUser currentUser) {
        // Assuming "notes" is your collection name
        mainHandler.post(() -> {
            db.collection(currentUser.getEmail())
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // document.getData() will contain the note's data
                                String title = (String) document.get("title");
                                String content = (String) document.get("content");
                                Timestamp date = (Timestamp) document.get("date");
                                dummyNotes.add(new Note(title, content, document.getId(), date.toDate()));

                                Log.d("Debug", "title: " + title);
                                Log.d("Debug", "Retrieved notes: " + dummyNotes.size());
                                homeFragment.noteListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(this, dummyNotes.size(), Toast.LENGTH_SHORT).show();
                            // Handle errors here
                        }
                    });
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
        } else {
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

            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < backStackEntryCount; i++) {
                fragmentManager.popBackStack();
            }


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
            addNewNote("New Title", "");
            NoteListFragment nodeListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (nodeListFragment != null) {
                nodeListFragment.onNoteClick(dummyNotes.size() - 1); // Replace 'position' with the actual position you want to pass
            }
            return true;
        }
        if (item.getItemId() == R.id.action_back) {
            back();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {

            updateNoteInFirestore();
            //noteDetailFragment.saveNote();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void addNewNote(String title, String content) {
        String randomId = generateRandomUUID();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Create a new Note object
        // Add the new note to the list
        Note newNote = new Note(title, content);
        newNote.setId(randomId);
        Date currentDate = Calendar.getInstance().getTime();
        newNote.setDate(currentDate);
        dummyNotes.add(newNote);
        mainHandler.post(() -> {
            if (currentUser != null) {
                db.collection(currentUser.getEmail())
                        .document(randomId)
                        .set(newNote)
                        .addOnSuccessListener(documentReference -> {

                            // Operação bem-sucedida, notifique o adaptador na thread principal
                            homeFragment.noteListAdapter.notifyDataSetChanged();

                        })
                        .addOnFailureListener(e -> {

                        });

                // Notify the adapter that the data set has changed
                NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                noteListFragment.noteListAdapter.notifyDataSetChanged();

            }
        });
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


    private void updateNoteInFirestore() {
        mainHandler.post(() -> {
            NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            noteDetailFragment.saveChanges();
            Note note = dummyNotes.get(noteDetailFragment.position);


            db.collection(currentUser.getEmail())
                    .document(note.getId())
                    .update("title", note.getTitle(), "content", note.getContent())
                    .addOnSuccessListener(aVoid -> {
                        // Handle successful update
                    })
                    .addOnFailureListener(e -> {
                        // Handle failed update
                    });
        });
    }

    void updateNoteTitleInFirestore(int position) {
        mainHandler.post(() -> {
            if (currentUser.getEmail() == null) return;

            Note note = dummyNotes.get(position);
            db.collection(currentUser.getEmail())
                    .document(note.getId())
                    .update("title", note.getTitle())
                    .addOnSuccessListener(aVoid -> {
                        // Handle successful update
                    })
                    .addOnFailureListener(e -> {
                        // Handle failed update
                    });
        });
    }

    void eraseNoteInFirestore(Note note) {

        if (currentUser.getEmail() == null) return;
        mainHandler.post(() -> {
            db.collection(currentUser.getEmail())
                    .document(note.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Handle successful update

                    })
                    .addOnFailureListener(e -> {
                        // Handle failed update

                    });
        });
    }


}
