package com.hevit.googlerapidresponse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DashBoard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    ImageButton btn;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("distress");
    DatabaseReference userRef = database.getReference("users");
    FirebaseAuth auth;
    Users newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.dropdown_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        btn = findViewById(R.id.imageButton);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot exists and has children
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // Iterate through the children nodes
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Get the value of each child node
                        Users yourObject = snapshot.getValue(Users.class);

                        // Handle the fetched object here (e.g., display in UI, store in a list, etc.)
                        Log.d("TITE", "Fetched object: " + yourObject.getUid());
                        if(yourObject.getUid()!=null){
                            if(yourObject.getUid().equals(auth.getCurrentUser().getUid())){
                                newUser = yourObject;
                            }

                        }
                    }

                } else {
                    // Handle case where the dataSnapshot is empty
                    Log.d("NOTTITE", "No data found");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String, String> distress = new HashMap<String, String>();

                String userId = myRef.push().getKey(); // Generate a unique key for the new user
                myRef.child(userId).setValue(distress);
                distress.put("address", newUser.getAddress());
                distress.put("age", newUser.getAge());
                distress.put("email", newUser.getEmail());
                distress.put("firstName", newUser.getFirstName());
                distress.put("gender", newUser.getGender());
                distress.put("lastName", newUser.getLastName());
                distress.put("medicalHistory", newUser.getMedicalHistory());
                distress.put("phoneNumber", newUser.getPhoneNumber());
                distress.put("uid", newUser.getUid());

            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}