package com.example.cryptoapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptoapp.MainActivity;
import com.example.cryptoapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.util.List;

import Model.Messages;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MesajlarViewHolder> {
    private List<Messages> kullaniciMesajlariListesi;

    //Firebase
    private FirebaseAuth mYetki;
    private DatabaseReference kullanicilarYolu;

    //Adaptör
    public MessageAdapter(List<Messages> kullaniciMesajlariListesi) {
        this.kullaniciMesajlariListesi = kullaniciMesajlariListesi;
    }



    //ViewHolder
    public class MesajlarViewHolder extends RecyclerView.ViewHolder {

        //Ozel mesajlar layouttaki kontroller
        public TextView gonderenMesajMetni, aliciMesajMetni;
        public ImageView gonderenImageView;

        public MesajlarViewHolder(@NonNull View itemView) {
            super(itemView);

            //Ozel mesajlar layouttaki kontrol tanımlamaları
            aliciMesajMetni = itemView.findViewById(R.id.alici_mesaj_metni);
            gonderenMesajMetni = itemView.findViewById(R.id.gonderen_mesaj_metni);
            gonderenImageView= itemView.findViewById(R.id.mesaj_profil_resmi);

        }
    }

    @NonNull
    @Override
    public MesajlarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.private_messages_layout, viewGroup, false);

        //Firebase tanımlama
        mYetki = FirebaseAuth.getInstance();

        return new MesajlarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MesajlarViewHolder mesajlarViewHolder, @SuppressLint("RecyclerView") int i) {
        String mesajGonderenId = mYetki.getCurrentUser().getUid();

        //Model tanımlama
        Messages mesajlar = kullaniciMesajlariListesi.get(i);

        String kimdenKullaniciId = mesajlar.getFrom();
        String kimdenMesajTuru = mesajlar.getType();

        //Veritabanı yolu
        kullanicilarYolu = FirebaseDatabase.getInstance().getReference().child("Users").child(kimdenKullaniciId);

        //Firebaseden veri çekme
        kullanicilarYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Görünmez yapma
        mesajlarViewHolder.aliciMesajMetni.setVisibility(View.GONE);

        mesajlarViewHolder.gonderenMesajMetni.setVisibility(View.GONE);
        mesajlarViewHolder.gonderenImageView.setVisibility(View.GONE);

        if (kimdenMesajTuru.equals("text")) {
            if (kimdenKullaniciId.equals(mesajGonderenId)) {
                mesajlarViewHolder.gonderenImageView.setVisibility(View.INVISIBLE);
                mesajlarViewHolder.gonderenMesajMetni.setVisibility(View.VISIBLE);
                mesajlarViewHolder.gonderenMesajMetni.setBackgroundResource(R.drawable.sender_messages_layout);
                mesajlarViewHolder.gonderenMesajMetni.setTextColor(Color.BLACK);
                mesajlarViewHolder.gonderenMesajMetni.setText(mesajlar.getMessage()); // Mesaj metnini ayarla

            } else {
                mesajlarViewHolder.gonderenImageView.setVisibility(View.VISIBLE);
                mesajlarViewHolder.aliciMesajMetni.setVisibility(View.VISIBLE);
                mesajlarViewHolder.aliciMesajMetni.setBackgroundResource(R.drawable.recipient_messages_layout);
                mesajlarViewHolder.aliciMesajMetni.setTextColor(Color.BLACK);
                mesajlarViewHolder.aliciMesajMetni.setText(mesajlar.getMessage()); // Mesaj metnini ayarla
            }
        }
    }

        @Override
        public int getItemCount () {
            return kullaniciMesajlariListesi.size();
        }
    }


