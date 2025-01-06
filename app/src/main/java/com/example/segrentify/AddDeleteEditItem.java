package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

public class AddDeleteEditItem extends AppCompatActivity {

    EditText editTextName, editTextDesc, editTextPrice, editTextTime;
    Button buttonAdd, buttonExit;
    ListView listViewItems;
    FirebaseAuth auth;
    Spinner spinnerCategory;

    String selectedCategory;

    List<Item> items;
    FirebaseUser user;
    DatabaseReference accountdatabase;
    Account account;

    ArrayList<String> categories;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_delete_edit_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        accountdatabase = FirebaseDatabase.getInstance().getReference();
        editTextName = findViewById(R.id.addItemName);
        editTextDesc = findViewById(R.id.addItemDesciption);
        editTextPrice = findViewById(R.id.itemPrice);
        editTextTime = findViewById(R.id.rentPeriod);
        listViewItems = findViewById(R.id.itemList);
        buttonAdd = findViewById(R.id.addItem);
        buttonExit = findViewById(R.id.exit);
        spinnerCategory = findViewById(R.id.categorySpinner);

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDeleteEditItem.this,
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
                            createList(account);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors here (optional)
                            Toast.makeText(getApplicationContext(), "Failed to load account data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        buttonAdd.setOnClickListener(v -> addItem());

        listViewItems.setOnItemLongClickListener((parent, view, position, id) -> {
            Item item = items.get(position);
            showUpdateDeleteDialog(item.getId(), item.getItem_name());
            return true;
        });

        buttonExit.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            startActivity(intent);
            finish();
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
                    if(item.getOwner() == account.getEmail()){
                        items.add(item);
                    }
                }
                ItemList itemsAdapter = new ItemList(AddDeleteEditItem.this, items);
                listViewItems.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here (optional)
            }
        });
    }

    private void showUpdateDeleteDialog(final String itemID, String itemName) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.item_update, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.updateItemName);
        final EditText editTextDesc = dialogView.findViewById(R.id.updateItemDesciption);
        final EditText editTextPrice = dialogView.findViewById(R.id.updateItemPrice);
        final EditText editTextTime = dialogView.findViewById(R.id.rentPeriod5);
        final Button buttonUpdate = dialogView.findViewById(R.id.itemUpdate);
        final Button buttonDelete = dialogView.findViewById(R.id.itemDelete);
        final Spinner spinner = dialogView.findViewById(R.id.updateCategorySpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        dialogBuilder.setTitle(itemName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(view -> {
            String name = editTextName.getText().toString().trim();
            String desc = editTextDesc.getText().toString().trim();
            String price = editTextPrice.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                updateItem(itemID, name, desc, price, time);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(view -> {
            deleteItem(itemID);
            b.dismiss();
        });
    }

    private void updateItem(String id, String name, String desc, String price, String time) {
        // Check for valid price format
        for (int i = 0; i < price.length(); i++) {
            if (!Character.isDigit(price.charAt(i))) {
                if (price.charAt(i) == '.' && price.length() - i == 3) {
                    continue;
                }
                Toast.makeText(getApplicationContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // Perform the update operation in Firebase
        Item updatedItem = new Item(id, price, time, name, desc, selectedCategory, account.getEmail());
        databaseReference.child(id).setValue(updatedItem);
        Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void deleteItem(String itemID) {
        // Perform the delete operation in Firebase
        databaseReference.child(itemID).removeValue();
        Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_SHORT).show();
    }

    private void addItem() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDesc.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price) || TextUtils.isEmpty(time)) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemId = databaseReference.push().getKey();
        Item newItem = new Item(itemId, price, time, name, description, selectedCategory, account.getEmail());

        databaseReference.child(itemId).setValue(newItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                editTextName.setText("");
                editTextDesc.setText("");
                editTextPrice.setText("");
                editTextTime.setText("");

                createList(account);  // Refresh the list of items
                Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
