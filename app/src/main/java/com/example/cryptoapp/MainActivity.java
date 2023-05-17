package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessAdapter myTabAccessAdapter;

    //Firebase
    private FirebaseUser mevcutKullanici;
    private FirebaseAuth mAuth;


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
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mevcutKullanici==null)
        {
            KullaniciyiLoginActivityeGonder();
        }
    }

    private void KullaniciyiLoginActivityeGonder()
    {
        Intent LoginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(LoginIntent);

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

        }

        if(item.getItemId()==R.id.main_settings_option){

        }

        if(item.getItemId()==R.id.main_sign_out_option){
            mAuth.signOut();
            Intent login = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(login);
        }
        return true;
    }
}