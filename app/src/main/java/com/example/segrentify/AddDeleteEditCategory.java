package com.example.segrentify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddDeleteEditCategory extends AppCompatActivity {

    EditText editTextName;
    EditText editTextDesc;

    Button buttonAddCategory;
    Button buttonExit;

    ListView listViewCategories;

    List<Category> categories;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_delete_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        editTextName = (EditText) findViewById(R.id.addCategoryName);
        editTextDesc = (EditText) findViewById(R.id.addCategoryDesciption);
        listViewCategories = (ListView) findViewById(R.id.categoryList);
        buttonAddCategory = (Button) findViewById(R.id.addCategory);
        buttonExit = (Button) findViewById(R.id.exit);

        categories = new ArrayList<Category>();

        buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        listViewCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                showUpdateDeleteDialog(category.getId(),category.getName());
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

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                categories.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Category category = postSnapshot.getValue(Category.class);
                    categories.add(category);
                }
                CategoryList categoriesAdapter = new CategoryList(AddDeleteEditCategory.this, categories);
                listViewCategories.setAdapter(categoriesAdapter);
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }


    private void showUpdateDeleteDialog(final String categoryId, String categoryName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.updateCategory);
        final EditText editTextDesc  = (EditText) dialogView.findViewById(R.id.editDescription);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle(categoryName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String desc = editTextDesc.getText().toString().trim();;
                if (!TextUtils.isEmpty(name)) {
                    updateCategory(categoryId, name, desc);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletecCategory(categoryId);
                b.dismiss();
            }
        });
    }

    private void updateCategory(String id, String name, String desc) {
        for(int i = 0;i<name.length();i++){
            if(!Character.isLetter(name.charAt(i))){
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (name.length()>1 && desc.length()>1) {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("categories").child(id);
            Category category = new Category(id, name, desc);
            dR.setValue(category);
            Toast.makeText(getApplicationContext(), "Category Updated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter a valid name and description", Toast.LENGTH_LONG).show();
        }
    }

    private void deletecCategory(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("categories").child(id);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Category Deleted", Toast.LENGTH_LONG).show();
    }

    private void addCategory() {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();

        for(int i = 0;i<name.length();i++){
            if(!Character.isLetter(name.charAt(i))){
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (name.length()>1 && desc.length()>1) {
            String id = databaseReference.push().getKey();
            Category product = new Category(id, name, desc);

            databaseReference.child(id).setValue(product);
            editTextName.setText("");
            editTextDesc.setText("");

            Toast.makeText(this, "Category added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter a valid name and description", Toast.LENGTH_LONG).show();
        }
    }


}