package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

public class RenterSearch extends AppCompatActivity {

    EditText editTextName;

    TextView helper;

    Button buttonSearch;
    Button buttonExit;

    ListView listViewItems;

    List<Item> items;
    Account account;
    ArrayList<String> categories;

    DatabaseReference databaseReference;
    DatabaseReference accountdatabase;
    FirebaseAuth auth;
    FirebaseUser user;

    Spinner spinnerCategory;
    String selectedCategory;
    String currentSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_renter_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        editTextName = (EditText) findViewById(R.id.updateItemName);
        listViewItems = (ListView) findViewById(R.id.searchItem);
        buttonSearch = (Button) findViewById(R.id.itemSearch);
        buttonExit = (Button) findViewById(R.id.itemExit);
        accountdatabase = FirebaseDatabase.getInstance().getReference();
        spinnerCategory = findViewById(R.id.searchCategorySpinner);
        helper = findViewById(R.id.helperText);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        items = new ArrayList<>();
        categories = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    String a = category.getName();
                    if (!(a == null)) {
                        categories.add(a);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RenterSearch.this,
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here (optional)
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No category selected
                selectedCategory = null;
            }
        });


        // Check if the user is logged in, if not redirect to Login
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return; // Exit the onCreate method if user is not logged in
        } else {
            // Retrieve user details from Firebase Realtime Database
            accountdatabase.child("users")
                    .child(user.getUid())  // Get the child that matches the user's UID
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get the Account object
                            account = dataSnapshot.getValue(Account.class);
                            //createList(account);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here (optional)
                            Toast.makeText(getApplicationContext(), "Failed to load account data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createList(account);
            }
        });

        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createList(Account account) {
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear(); // Clear the list before adding new items
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    //Getting the currently selected category
                    TextView textView = (TextView)spinnerCategory.getSelectedView();
                    String result = textView.getText().toString();

                    //Finding the search request currently typed into the search bar
                    currentSearch = editTextName.getText().toString().trim();
                    //The if statement uses .contains() so that any item containing the characters inputted
                    //(within the proper order) will show up. The category of the item is also compared to
                    //The category selected on the spinner.
                    if(item.getItem_name().toLowerCase().contains(currentSearch.toLowerCase())
                            || item.getDescription().toLowerCase().contains(currentSearch.toLowerCase())
                            && item.getCategory().equals(result)){
                                items.add(item);
                                //Clearing the list of displayed items if a search with no results is entered
                    }
                    if(items.isEmpty()){
                        helper.setTextSize(20);
                        helper.setText(R.string.no_results);
                    }
                    else{
                        helper.setTextSize(0);
                    }


                }

                ItemList itemsAdapter = new ItemList(RenterSearch.this, items);
                listViewItems.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here (optional)
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.setTextSize(20);
        helper.setText(R.string.helper);

    }
}