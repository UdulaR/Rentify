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
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class ViewUsers extends AppCompatActivity {

    Button buttonExit;

    ListView listViewUsers;

    List<Account> users;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        listViewUsers = (ListView) findViewById(R.id.userList);
        buttonExit = (Button) findViewById(R.id.userExit);

        users = new ArrayList<Account>();



        listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Account user = users.get(position);

                showUpdateDeleteDialog(user.getId(),user.getFirstName(),user);
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
                users.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Account user = postSnapshot.getValue(Account.class);
                    if(!user.getRole().equals("Admin")){
                        user.setId(postSnapshot.getKey());
                        users.add(user);

                    }
                }
                UserList userAdapter = new UserList(ViewUsers.this,users);
                listViewUsers.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }


    private void showUpdateDeleteDialog(final String categoryId, String categoryName,Account user) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_user_dialogue, null);
        dialogBuilder.setView(dialogView);


        final Button buttonDisable = (Button) dialogView.findViewById(R.id.disableUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.deleteUser);

        dialogBuilder.setTitle(categoryName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    deletecUser(categoryId,user);
                    b.dismiss();
            }
        });

        buttonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableUser(categoryId,user);
                b.dismiss();
            }
        });
    }



    private void deletecUser(String id,Account user) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(id);
        dR.removeValue();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Item item = postSnapshot.getValue(Item.class);
                    if(item.getOwner().equals(user.getEmail())){
                        databaseReference.child(item.getId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle errors here (optional)
            }
        });
        Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
    }
    private void disableUser(String id,Account user) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(id);
        user.changeDisable();
        dR.setValue(user);
        Toast.makeText(getApplicationContext(), "User disabled/enabled", Toast.LENGTH_LONG).show();

    }





}