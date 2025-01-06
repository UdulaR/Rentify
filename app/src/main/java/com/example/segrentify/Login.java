package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    // Declare UI elements and Firebase authentication
    TextInputEditText editTextEmail, editTextPassword; // Input fields for email and password
    Button buttonLogin;                                // Login button
    FirebaseAuth mAuth;                                // Firebase authentication instance
    ProgressBar progressBar;                           // Progress bar for loading indicator
    TextView textView;                                 // TextView for "register now" option

    @Override
    public void onStart() {
        super.onStart();
        // Check if the user is already logged in
        mAuth.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable EdgeToEdge display and set the content view
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
  
        // Apply system window insets to properly display content
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements with their respective IDs in the XML layout
        editTextEmail = findViewById(R.id.email);        // Email input field
        editTextPassword = findViewById(R.id.password);  // Password input field
        buttonLogin = findViewById(R.id.btn_login);      // Login button
        progressBar = findViewById(R.id.progressBar);    // Progress bar for loading
        textView = findViewById(R.id.registerNow);       // TextView for registering a new account

        // Set up "register now" button to navigate to Register activity
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Register activity when "register now" is clicked
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish(); // End current Login activity
            }
        });

        // Set up login button click event
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show progress bar while attempting login
                progressBar.setVisibility(View.VISIBLE);

                // Get the user's email and password inputs
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Validate email input
                if (TextUtils.isEmpty(email)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this,"Please enter your email!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this,"Please enter your password!",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Attempt to sign in using Firebase authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Hide progress bar after login attempt
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    // If sign-in is successful, show success message
                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    // Navigate to MainActivity
                                    Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                    startActivity(intent);
                                    finish(); // End Login activity
                                } else {
                                    // If sign-in fails, show error message
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}