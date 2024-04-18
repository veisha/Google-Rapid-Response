package com.hevit.googlerapidresponse;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    String name;
    String mail;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    boolean isNewUser = true;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try{
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                auth = FirebaseAuth.getInstance();
//                                Glide.with(MainActivity.this).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(imageView);
                                name = auth.getCurrentUser().getDisplayName();
                                mail = auth.getCurrentUser().getEmail();
                                Toast.makeText(MainActivity.this, "Signed in succesfully", Toast.LENGTH_SHORT).show();
                                // Start the new activity here
                                String UID = auth.getUid();
                                Log.d("USER: ", UID);
                                // Read from the database
                                // Read from the database
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
                                                    if(yourObject.getUid().equals(UID)){
                                                        isNewUser = false;
                                                        Intent intent = new Intent(MainActivity.this, DashBoard.class);
                                                        startActivity(intent);
                                                        finish(); // Optional: finish the current activity if you don't want to keep it in the back stack
                                                    }

                                                }
                                            }
                                            if(isNewUser){
                                                Intent intent = new Intent(MainActivity.this, UserSignUp.class);
                                                startActivity(intent);
                                                finish(); // Optional: finish the current activity if you don't want to keep it in the back stack
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

                            }
                            else{
                                Toast.makeText(MainActivity.this, "Sign in failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, options);

        auth = FirebaseAuth.getInstance();

        ImageButton imageButton = findViewById(R.id.btnGoogleAuth);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);

            }
        });

    }


}