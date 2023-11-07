package com.example.challange2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(!username.equals("") && !password.equals(""))
                    loginUser(username, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the registration fragment or perform registration here
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(!username.equals("") && !password.equals(""))
                    registerUser(username, password);
            }
        });
        

        return view;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to the next fragment
                        // Replace "YourNextFragment" with the actual fragment you want to navigate to
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        NoteListFragment newFragment = new NoteListFragment();
                        transaction.replace(R.id.fragmentContainer, newFragment);
                        transaction.addToBackStack(null);

                        transaction.commit();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Registration success, you can perform additional actions here if needed
                        // For example, you might want to save user information in a database
                        Toast.makeText(getContext(), "Registration successful.", Toast.LENGTH_SHORT).show();

                        // After successful registration, you can navigate to the next fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        // Replace "YourNextFragment" with the actual fragment you want to navigate to
                        NoteListFragment newFragment = new NoteListFragment();
                        transaction.replace(R.id.fragmentContainer, newFragment);
                        transaction.addToBackStack(null);

                        transaction.commit();
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(getContext(), "Registration failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
