package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference rootReference;
    private FirebaseAuth mAuth;
    private Button registerButton;
    private EditText registerEmail, registerPassword;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        // Control Initialization
        registerButton = findViewById(R.id.register_button);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        signIn = findViewById(R.id.account);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();

                // Create a new user account using Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String deviceToken) {
                                                    String currentUserID = mAuth.getCurrentUser().getUid();
                                                    rootReference.child("Users").child(currentUserID).setValue("");
                                                    rootReference.child("Users").child(currentUserID).child("device_token")
                                                            .setValue(deviceToken);

                                                    // User account created successfully
                                                    FirebaseUser user = mAuth.getCurrentUser();

                                                    // Perform any additional actions after the registration process here
                                                    // For example, you can add the user's name to their profile:
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName("User's Name")
                                                            .build();
                                                    user.updateProfile(profileUpdates);

                                                    // Redirect the user to another page
                                                    Intent mainPageIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    mainPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainPageIntent);
                                                    finish(); // Close the RegisterActivity
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Failed to retrieve the device token
                                                    Toast.makeText(RegisterActivity.this, "Failed to register. Please try again.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Registration failed
                                    Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginActivityIntent);
            }
        });
    }
}
