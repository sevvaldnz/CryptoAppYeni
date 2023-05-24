package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button updateButton;
    private EditText usernameEditText;
    private EditText aboutEditText;

    private FirebaseAuth mAuth;
    private DatabaseReference dataPath;
    private String mevcutKullaniciId;
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        dataPath= FirebaseDatabase.getInstance().getReference();

        mevcutKullaniciId=mAuth.getCurrentUser().getUid();

        updateButton=findViewById(R.id.updateButton);
        usernameEditText=findViewById(R.id.usernameEditText);
        aboutEditText=findViewById(R.id.aboutEditText);
        settingsToolbar=findViewById(R.id.settings_toolbar);

        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AyarlariGuncelle();
            }
        });

        KullaniciBilgisiAl();
    }

    private void KullaniciBilgisiAl() {
        dataPath.child("Users").child(mevcutKullaniciId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&& snapshot.hasChild("username")){
                            String kullaniciAdiniAl=snapshot.child("username").getValue().toString();
                            String kullaniciDurumunuAl = snapshot.child("about").getValue().toString();

                            usernameEditText.setText(kullaniciAdiniAl);
                            aboutEditText.setText(kullaniciDurumunuAl);
                            
                        } else {
                            Toast.makeText(SettingsActivity.this, "Please update your profile information", Toast.LENGTH_LONG).show();
                            
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void AyarlariGuncelle() {
        String createUsername= usernameEditText.getText().toString();
        String createAbout = aboutEditText.getText().toString();

        if(TextUtils.isEmpty(createUsername)){
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(createAbout)){
            Toast.makeText(this, "About cannot be empty", Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String,String> profilHaritasi= new HashMap<>();
            profilHaritasi.put("uid",mevcutKullaniciId);
            profilHaritasi.put("username",createUsername);
            profilHaritasi.put("about",createAbout);

            dataPath.child("Users").child(mevcutKullaniciId).setValue(profilHaritasi)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                                Intent mainPage = new Intent(SettingsActivity.this,MainActivity.class);
                                mainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainPage);
                                finish();
                            }
                            else{
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Hata: "+message , Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}