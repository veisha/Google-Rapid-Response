package com.hevit.googlerapidresponse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSignUp extends AppCompatActivity {

    String fName;
    String lName;
    String age;
    String sex;
    String contactNum;
    String medicalHistory;

    String address;

    FirebaseAuth auth;

    String email;
    Users newUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        EditText firstN = findViewById(R.id.firstNameInput);
        EditText lastN = findViewById(R.id.lastNameInput);
        EditText AGE = findViewById(R.id.ageInput);
        EditText SEX = findViewById(R.id.genderInput);
        EditText contact = findViewById(R.id.contactInput);
        EditText medHist = findViewById(R.id.healthHistoryInput);
        EditText addr = findViewById(R.id.addressInput);
        Button button = findViewById(R.id.button2);
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fName = String.valueOf(firstN.getText());
                lName = String.valueOf(lastN.getText());
                age = String.valueOf(AGE.getText());
                sex = String.valueOf(SEX.getText());
                contactNum = String.valueOf(contact.getText());
                medicalHistory = String.valueOf(medHist.getText());
                address = String.valueOf(addr.getText());

                if (fName != null && lName != null && age != null && sex != null && contactNum != null && medicalHistory !=null){
                    newUser = new Users();
                    // Set values using setters
                    newUser.setFirstName(fName);
                    newUser.setLastName(lName);
                    newUser.setAge(age);
                    newUser.setGender(sex);
                    newUser.setPhoneNumber(contactNum);
                    newUser.setMedicalHistory(medicalHistory);
                    newUser.setAddress(address);
                    newUser.setUid(auth.getCurrentUser().getUid());
                    newUser.setEmail(auth.getCurrentUser().getEmail());

                    String userId = myRef.push().getKey(); // Generate a unique key for the new user
                    myRef.child(userId).setValue(newUser);

                }
            }
        });

    }
}