package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;

public class ChatsFragment extends Fragment {

    private View OzelSohbetlerView;
    private RecyclerView sohbetlerListesi;

    // Firebase
    private DatabaseReference sohbetYolu, kullaniciYolu;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        OzelSohbetlerView = inflater.inflate(R.layout.fragment_chats, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String aktifKullaniciId = currentUser.getUid();
        sohbetYolu = FirebaseDatabase.getInstance().getReference().child("Chats").child(aktifKullaniciId);
        kullaniciYolu = FirebaseDatabase.getInstance().getReference().child("Users");

        // RecyclerView
        sohbetlerListesi = OzelSohbetlerView.findViewById(R.id.sohbetler_listesi);
        sohbetlerListesi.setLayoutManager(new LinearLayoutManager(getContext()));

        return OzelSohbetlerView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> secenekler = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(sohbetYolu, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, SohbetlerViewHolder> adapter = new FirebaseRecyclerAdapter<Users, SohbetlerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull final SohbetlerViewHolder holder, int position, @NonNull Users model) {
                final String kullaniciIdleri = getRef(position).getKey();

                kullaniciYolu.child(kullaniciIdleri).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String adAl = dataSnapshot.child("username").getValue().toString();
                            String durumAl = dataSnapshot.child("about").getValue().toString();

                            holder.kullaniciAdi.setText(adAl);

                            if (dataSnapshot.hasChild("User_Status")) {
                                DataSnapshot userStatusSnapshot = dataSnapshot.child("User_Status");
                                if (userStatusSnapshot.hasChild("status")) {
                                    String durum = userStatusSnapshot.child("status").getValue().toString();
                                    String tarih = userStatusSnapshot.child("date").getValue().toString();
                                    String zaman = userStatusSnapshot.child("time").getValue().toString();

                                    if (durum.equals("online")) {
                                        holder.kullaniciDurumu.setText("online");
                                    } else if (durum.equals("offline")) {
                                        holder.kullaniciDurumu.setText("Last seen: " + tarih + " " + zaman);
                                    }
                                }
                            } else {
                                holder.kullaniciDurumu.setText("offline");
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatAktivite = new Intent(getContext(), ChatActivity.class);
                                    chatAktivite.putExtra("kullanici_id_ziyaret", kullaniciIdleri);
                                    chatAktivite.putExtra("kullanici_adi_ziyaret", adAl);
                                    startActivity(chatAktivite);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                    }
                });
            }

            @NonNull
            @Override
            public SohbetlerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_layout, viewGroup, false);
                return new SohbetlerViewHolder(view);
            }
        };

        sohbetlerListesi.setAdapter(adapter);
        adapter.startListening();
    }

    public static class SohbetlerViewHolder extends RecyclerView.ViewHolder {
        // Controls
        TextView kullaniciAdi, kullaniciDurumu;

        public SohbetlerViewHolder(@NonNull View itemView) {
            super(itemView);

            // Control tanımlamaları
            kullaniciAdi = itemView.findViewById(R.id.usernameEditText);
            kullaniciDurumu = itemView.findViewById(R.id.aboutEditText);
        }
    }
}
