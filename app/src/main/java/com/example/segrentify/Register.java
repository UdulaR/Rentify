package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    // UI elements
    TextInputEditText editTextEmail, editTextPassword, editTextFName, editTextLName;
    Button buttonReg;
    Spinner spinnerRoles;
    String selectedRole = null; // Stores the selected role from the spinner
    FirebaseAuth mAuth; // Firebase authentication instance
    ProgressBar progressBar; // Progress bar for visual feedback
    TextView textView; // TextView to navigate to the login screen

    // Reference to Firebase Realtime Database
    DatabaseReference databaseReference;

    @Override
    public void onStart() {
        super.onStart();
        // Check if a user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If logged in, redirect to MainActivity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish(); // Close the current activity
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Change "users" if needed

        // Bind UI elements to their respective views in the layout
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFName = findViewById(R.id.FirstName);
        editTextLName = findViewById(R.id.LastName);
        buttonReg = findViewById(R.id.btn_Register);
        spinnerRoles = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Set click listener to navigate to the login screen
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // Set up the spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(adapter);

        // Listener for spinner item selection
        spinnerRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected role
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No role selected
                selectedRole = null;
            }
        });

        // Set click listener for the registration button
        buttonReg.setOnClickListener(v -> {
            // Check if a role has been selected
            if (selectedRole == null) {
                Toast.makeText(Register.this, "Please select a role!", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                Account person = new Account();
                person.setRole(selectedRole);

                try{
                    person.setFirstName(String.valueOf(editTextFName.getText()));
                    person.setLastName(String.valueOf(editTextLName.getText()));
                    person.setPassword(String.valueOf(editTextPassword.getText()));
                    person.setEmail(String.valueOf(editTextEmail.getText()));
                }
                catch (IllegalArgumentException a){
                    Toast.makeText(Register.this,a.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user with email and password
                mAuth.createUserWithEmailAndPassword(person.getEmail(), person.getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE); // Hide progress bar
                                if (task.isSuccessful()) {
                                    // If registration is successful, save user details to the Realtime Database
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    saveUserToDatabase(user.getUid(), person);

                                    Toast.makeText(Register.this, "Account Created.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                    startActivity(intent);
                                    finish(); // Close the current activity
                                } else {
                                    // If registration fails, notify the user
                                    Toast.makeText(Register.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Adjust layout padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to save user details to the Realtime Database
    private void saveUserToDatabase(String userId, Account person) {
        databaseReference.child(userId).setValue(person) // Use userId as the key
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Register.this, "User details saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Register.this, "Failed to save user details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
