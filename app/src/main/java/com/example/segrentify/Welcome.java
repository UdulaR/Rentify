package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Welcome extends AppCompatActivity {

    FirebaseAuth auth;
    Button buttonLogout;
    TextView fNameView, lNameView, emailView, roleView, option1View,option2View,option3View;
    FirebaseUser user;
    DatabaseReference mDatabase;
    Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Handle window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize UI elements
        fNameView = findViewById(R.id.fname_input);
        lNameView = findViewById(R.id.lname_input);
        emailView = findViewById(R.id.email_input);
        roleView = findViewById(R.id.role_input);
        buttonLogout = findViewById(R.id.logout);
        option1View = findViewById(R.id.option1);
        option2View = findViewById(R.id.option2);
        option3View = findViewById(R.id.option3);


        // Check if the user is logged in, if not redirect to Login
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return; // Exit the onCreate method if user is not logged in
        }
        else {

            // Retrieve user details from Firebase Realtime Database
            mDatabase.child("users")
                    .child(user.getUid())  // Get the child that matches the user's UID
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the Student object
                            account = dataSnapshot.getValue(Account.class);

                            // If student data exists, update the UI with the data
                            if (account != null) {
                                String firstName = account.getFirstName();
                                String lastName = account.getLastName();
                                String role = account.getRole();
                                String email = account.getEmail();

                                // Set the data in the TextViews
                                fNameView.setText(firstName);
                                lNameView.setText(lastName);
                                roleView.setText(role);
                                emailView.setText(email);
                                initializeWelcome(account);


                            }
                            else{
                                auth.getCurrentUser().delete();
                                Toast.makeText(getApplicationContext(), "Account is Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here (optional)
                            Toast.makeText(getApplicationContext(), "Failed to load student data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }






        // Set logout button click listener
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut(); // Sign out the user
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }



    public void initializeWelcome(Account account){
            String role = account.getRole();
            if (role.equals("Admin")) {
                option1View.setText(R.string.click_to_add_edit_delete_a_category);
                option2View.setText(R.string.click_to_list_all_users);
                option3View.setHeight(0);

                option1View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddDeleteEditCategory.class);
                        startActivity(intent);
                        finish();
                    }
                });

                option2View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ViewUsers.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else if (role.equals("Lessor") ) {
                if (!account.isDisabled()) {
                    option1View.setText(R.string.click_to_add_edit_delete_an_item);
                } else {
                    option1View.setText(R.string.your_account_is_disabled);
                }
                option2View.setHeight(0);
                option3View.setHeight(0);

                option1View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddDeleteEditItem.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else if (role.equals("Renter")) {
                if (!account.isDisabled()) {
                    option1View.setText(R.string.click_to_search_for_an_item);
                } else {
                    option1View.setText(R.string.your_account_is_disabled);
                }
                option2View.setHeight(0);
                option3View.setHeight(0);

                option1View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), RenterSearch.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }

}

