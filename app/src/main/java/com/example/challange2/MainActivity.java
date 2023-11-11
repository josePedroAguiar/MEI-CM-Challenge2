package com.example.challange2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Objects;
import java.util.UUID;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;




public class MainActivity extends AppCompatActivity implements LoginFragment.OnAuthenticationListener {
    public List<Note> dummyNotes = new ArrayList<>();
    LoginFragment loginFragment = new LoginFragment();
    NoteDetailFragment noteDetailFragment = new NoteDetailFragment();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Note> originalDummyNotes = new ArrayList<>();

    Handler mainHandler = new Handler(Looper.getMainLooper());  // Crie o Handler na thread principal

    private NetworkConnectivityListener connectivityListener;
    private ConnectivityManager.NetworkCallback networkCallback;

    private final Object retrieveNotesLock = new Object();

    public static String generateRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void saveNotesToInternalStorage(List<Note> notes) {
        SharedPreferences preferences = getSharedPreferences("MyNotes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String notesJson = gson.toJson(notes);

        editor.putString("notes", notesJson);
        editor.apply();
    }

    private List<Note> readNotesFromInternalStorage() {
        SharedPreferences preferences = getSharedPreferences("MyNotes", Context.MODE_PRIVATE);
        String notesJson = preferences.getString("notes", null);

        if (notesJson != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Note>>() {}.getType();
            return gson.fromJson(notesJson, listType);
        }

        return new ArrayList<>();
    }

    private void syncDataWithFirebase(FirebaseUser currentUser) {
        List<Note> notesToSync = readNotesFromInternalStorage();
        for (Note note : notesToSync) {
            // Verifique se o documento já existe na coleção
            mainHandler.post(() -> db.collection(Objects.requireNonNull(currentUser.getEmail()))
                    .document(note.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                setNoteInFirebase(currentUser, note);
                            }else{
                                updateNoteInFirestore2(note);
                            }
                            removeNoteFromInternalStorage(note);
                        }
                    }));
        }
    }


    private void removeNoteFromInternalStorage(Note note) {
        List<Note> notesFromStorage = readNotesFromInternalStorage();
        for (Note n : notesFromStorage) {
            if (n.getId().equals(note.getId())) {
                notesFromStorage.remove(n);
                break;
            }
        }
        saveNotesToInternalStorage(notesFromStorage);
    }


    protected void retriveNotes(FirebaseUser currentUser) {
        // Assuming "notes" is your collection name
        if (isNetworkConnected()) {
            mainHandler.post(() -> db.collection(Objects.requireNonNull(currentUser.getEmail()))
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // document.getData() will contain the note's data
                                String title = (String) document.get("title");
                                String content = (String) document.get("content");
                                Timestamp date = (Timestamp) document.get("date");
                                Note note = new Note(title, content, document.getId(), date.toDate());

                                boolean isNoteExists = dummyNotes.stream().anyMatch(n -> n.getId().equals(note.getId()));
                                if (!isNoteExists){
                                    dummyNotes.add(note);
                                    originalDummyNotes.add(note);
                                }

                                Log.d("Debug", "title: " + title);
                                Log.d("Debug", "Retrieved notes: " + dummyNotes.size());
                                NoteListFragment noteListFragment = getNoteListFragment();
                                if (noteListFragment != null) {
                                    noteListFragment.updateNoteListAdapter();
                                }

                            }
                        } else {
                            Toast.makeText(this, dummyNotes.size(), Toast.LENGTH_SHORT).show();
                            // Handle errors here
                        }
                    }));
            //saveNotesToInternalStorage( originalDummyNotes);
        }else {
            List<Note> localNotes = readNotesFromInternalStorage();
            dummyNotes.clear();
            originalDummyNotes.clear();
            dummyNotes.addAll(localNotes);
            originalDummyNotes.addAll(localNotes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityListener = new NetworkConnectivityListener(this);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d("NetworkStatus", "Device is online.");
                // Perform actions when the device becomes online
                FirebaseUser currentUser = getCurrentUser();
                syncDataWithFirebase(currentUser);
                retriveNotes(currentUser);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d("NetworkStatus", "Device is offline.");
                // Perform actions when the device becomes offline
                saveNotesToInternalStorage(dummyNotes);

            }
        };

        connectivityListener.registerNetworkCallback(networkCallback);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser = getCurrentUser();

        if (currentUser == null) {
            loadLoginFragment();
        } else {
            if (isNetworkConnected()) {
                syncDataWithFirebase(currentUser);
            }
            retriveNotes(currentUser);
            Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            loadHomeFragment();
            //homeFragment.noteListAdapter.notifyDataSetChanged();
        }

    }

    private void loadLoginFragment() {
        // Set the authentication listener
        loginFragment.setAuthenticationListener(this);

        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the existing fragment (if any) with the new one
        transaction.replace(R.id.fragmentContainer, loginFragment);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onAuthenticationSuccess() {
        FirebaseUser currentUser = getCurrentUser();
        Log.d("onAuthenticationSuccess", "currentUser: " + currentUser.getEmail());
        dummyNotes = new ArrayList<>();
        originalDummyNotes = new ArrayList<>();
        if (isNetworkConnected()) {
            syncDataWithFirebase(currentUser);
        }
        retriveNotes(currentUser);
        Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();


    }

    private void loadHomeFragment() {
        // Create a new instance of HomeFragment


        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        NoteListFragment homeFragment = new NoteListFragment();
        // Replace the existing fragment (if any) with the new one
        transaction.replace(R.id.fragmentContainer, homeFragment);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof NoteListFragment) {
            FirebaseAuth.getInstance().signOut();
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            for (int i = 0; i < backStackEntryCount; i++) {
                fragmentManager.popBackStack();
            }

            if (isNetworkConnected()) {
                FirebaseUser currentUser = getCurrentUser();
                if (currentUser != null) {
                    syncDataWithFirebase(currentUser);
                }
            }

            loadLoginFragment();
        } else {
            super.onBackPressed();
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
            back();
            //noteDetailFragment.saveNote();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void addNewNote(String title, String content) {
        String randomId = generateRandomUUID();
        FirebaseUser currentUser = getCurrentUser();

        // Create a new Note object
        // Add the new note to the list
        Note newNote = new Note(title, content);
        newNote.setId(randomId);
        Date currentDate = Calendar.getInstance().getTime();
        newNote.setDate(currentDate);
        dummyNotes.add(newNote);
        originalDummyNotes.add(newNote);

        if (isNetworkConnected()) {
            mainHandler.post(() -> {
                if (currentUser != null) {
                    db.collection(Objects.requireNonNull(currentUser.getEmail()))
                            .document(randomId)
                            .set(newNote)
                            .addOnSuccessListener(documentReference -> {
                                // Operação bem-sucedida, notifique o adaptador na thread principal
                                NoteListFragment noteListFragment = getNoteListFragment();
                                if (noteListFragment != null) {
                                    noteListFragment.updateNoteListAdapter();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failed database operation
                            });
                }
            });
        } else {
            // A rede não está disponível, armazene a nota localmente
            saveNoteToLocal(newNote);
        }
    }

    private void setNoteInFirebase(FirebaseUser currentUser, Note note) {
        db.collection(currentUser.getEmail())
                .document(note.getId())
                .set(note)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "Note----------" + note);
                })
                .addOnFailureListener(e -> {
                    // Handle a falha na definição do documento
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
                NoteListFragment noteListFragment = getNoteListFragment();
                if (noteListFragment != null) {
                    noteListFragment.noteListAdapter.getFilter().filter(searchQuery);
                }
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
            NoteListFragment homeFragment = new NoteListFragment();
            // If it is, navigate back to the NoteListFragment
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, homeFragment);
            transaction.commit();

        } else {
            super.onBackPressed(); // If not on NoteDetailFragment, proceed with default behavior
        }

    }
    public void updateNoteInFirestore2(Note note) {

        FirebaseUser currentUser = getCurrentUser();

        if (isNetworkConnected()) {
            mainHandler.post(() -> {
                db.collection(Objects.requireNonNull(currentUser.getEmail()))
                        .document(note.getId())
                        .update("title", note.getTitle(), "content", note.getContent(), "date", note.getDate())
                        .addOnSuccessListener(aVoid -> {
                            // Handle successful update
                        })
                        .addOnFailureListener(e -> {
                            // Handle failed database operation
                        });
            });
        } else {
            // A rede não está disponível, atualize a nota localmente
            updateNoteLocally(note);
            //saveNoteToLocal(note);
        }
    }

    public void updateNoteInFirestore() {
        NoteDetailFragment noteDetailFragment = (NoteDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        noteDetailFragment.saveChanges();
        Note note = dummyNotes.get(0);
        FirebaseUser currentUser = getCurrentUser();

        if (isNetworkConnected()) {
            mainHandler.post(() -> {
                db.collection(Objects.requireNonNull(currentUser.getEmail()))
                        .document(note.getId())
                        .update("title", note.getTitle(), "content", note.getContent(), "date", note.getDate())
                        .addOnSuccessListener(aVoid -> {
                            // Handle successful update
                        })
                        .addOnFailureListener(e -> {
                            // Handle failed database operation
                        });
            });
        } else {
            // A rede não está disponível, atualize a nota localmente
            updateNoteLocally(note);
            //saveNoteToLocal(note);
        }
    }

    void updateNoteTitleInFirestore(int position) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser.getEmail() == null) return;
        Note note = dummyNotes.get(position);

        if (isNetworkConnected()) {
            mainHandler.post(() -> {
                db.collection(currentUser.getEmail())
                        .document(note.getId())
                        .update("title", note.getTitle(), "content", note.getContent(), "date", note.getDate())
                        .addOnSuccessListener(aVoid -> {
                            // Handle successful update
                        })
                        .addOnFailureListener(e -> {
                            // Handle failed database operation
                        });
            });
        } else {
            // A rede não está disponível, atualize a nota localmente
            updateNoteLocally(note);
            //saveNoteToLocal(note);

        }
    }

    void saveNoteToLocal(Note note) {
        List<Note> localNotes = readNotesFromInternalStorage();
        localNotes.add(note);
        saveNotesToInternalStorage(localNotes);
    }

    void updateNoteLocally(Note updatedNote) {
        List<Note> localNotes = readNotesFromInternalStorage();
        for (int i = 0; i < localNotes.size(); i++) {
            if (localNotes.get(i).getId().equals(updatedNote.getId())) {
                localNotes.set(i, updatedNote);
                break;
            }
        }
        saveNotesToInternalStorage(localNotes);
    }

    void eraseNoteInFirestore(Note note) {
        dummyNotes.remove(note);
        originalDummyNotes.remove(note);
        removeNoteFromInternalStorage(note);

        FirebaseUser currentUser = getCurrentUser();
        if (currentUser.getEmail() == null) return;

        mainHandler.post(() -> db.collection(currentUser.getEmail())
                .document(note.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle successful update
                })
                .addOnFailureListener(e -> {
                    // Handle failed update
                }));
    }

    public void sortNotesByDate(List<Note> notes) {
        notes.sort((note1, note2) -> {
            // Comparação decrescente usando a data
            return note2.getDate().compareTo(note1.getDate());
        });
    }

    private NoteListFragment getNoteListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof NoteListFragment) return (NoteListFragment) fragment;
        return null;
    }

    private FirebaseUser getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityListener.unregisterNetworkCallback(networkCallback);
    }
}
