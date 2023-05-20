package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessAdapter myTabAccessAdapter;

    //Firebase
    private FirebaseUser mevcutKullanici;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.main_page_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CryptoApp");

        myViewPager=findViewById((R.id.main_tabs_pager));
        myTabAccessAdapter= new TabAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessAdapter);

        myTabLayout=findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        mAuth=FirebaseAuth.getInstance();
        mevcutKullanici=mAuth.getCurrentUser();
        UsersReference= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mevcutKullanici==null)
        {
            KullaniciyiLoginActivityeGonder();
        }
        else{
            KullanicininVarliginiDogrula();
        }
    }

    private void KullanicininVarliginiDogrula() {
        String mevcutKullaniciId = mAuth.getCurrentUser().getUid();
        UsersReference.child("Users").child(mevcutKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("username").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(settings);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void KullaniciyiLoginActivityeGonder()
    {
        Intent LoginIntent = new Intent(MainActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_choose_contact_option){
            Intent Findfriend= new Intent(MainActivity.this,FindfriendActivity.class);
            startActivity(Findfriend);


        }

        if(item.getItemId()==R.id.main_settings_option){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);

        }

        if(item.getItemId()==R.id.main_sign_out_option){
            mAuth.signOut();
            Intent login = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(login);
        }
        return true;
    }
}