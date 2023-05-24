package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private Button loginbutton;
    private EditText loginmail,loginpassword;
    private TextView createnewaccount, forgotpassword;


    private FirebaseAuth mAuth;
    private DatabaseReference userPath;

    ProgressDialog signinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Kontrol Tanımlamaları
        loginbutton=findViewById(R.id.login_button);

        loginmail=findViewById(R.id.login_email);
        loginpassword=findViewById(R.id.login_password);

        createnewaccount=findViewById(R.id.create_newaccount);
        forgotpassword=findViewById(R.id.login_forgotpassword);

        signinDialog= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        userPath= FirebaseDatabase.getInstance().getReference().child("Users");



        createnewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerActivityIntent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerActivityIntent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KullaniciyeGirisIzniVer();
            }
        });

    }

    private void KullaniciyeGirisIzniVer() {
        String email=loginmail.getText().toString();
        String password=loginpassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "email cannot be empty!", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "password cannot be empty!", Toast.LENGTH_SHORT).show();
        }
        else{
            signinDialog.setTitle("Signing in");
            signinDialog.setMessage("Please wait..");
            signinDialog.setCanceledOnTouchOutside(true);
            signinDialog.show();



            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String aktifKullaniciId = mAuth.getCurrentUser().getUid();

                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful()) {
                                            String deviceToken = task.getResult();

                                            userPath.child(aktifKullaniciId).child("device_token").setValue(deviceToken)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent mainPage = new Intent(LoginActivity.this, MainActivity.class);
                                                                mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(mainPage);
                                                                finish();
                                                                Toast.makeText(LoginActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                                                                signinDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Handle the error
                                            // You can display a toast or perform any other error handling logic
                                            Toast.makeText(LoginActivity.this, "Error getting device token", Toast.LENGTH_SHORT).show();

                                            // Dismiss the progress dialog
                                            signinDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: " + message + " Try again", Toast.LENGTH_SHORT).show();
                                signinDialog.dismiss();
                            }
                        }
                    });


        }
    }



    private void KullaniciyiAnaActivityeGonder() {
        Intent MainActivityIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(MainActivityIntent);



    }
}