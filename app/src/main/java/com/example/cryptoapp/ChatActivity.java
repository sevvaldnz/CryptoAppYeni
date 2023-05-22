package com.example.cryptoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    private String IdMesajAlici, AdMesajAlici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        IdMesajAlici = getIntent().getExtras().get("kullanici_adi_ziyaret").toString();
        AdMesajAlici = getIntent().getExtras().get("kullanici_adi_ziyaret").toString();

        Toast.makeText(this, IdMesajAlici + " " + AdMesajAlici, Toast.LENGTH_SHORT).show();
    }
}