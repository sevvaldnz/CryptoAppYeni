package com.example.cryptoapp;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cryptoapp.ChatActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View OzelSohbetlerView;
    private RecyclerView sohbetlerListesi;

    //Firebase
    private DatabaseReference sohbetYolu,kullaniciYolu;
    private FirebaseAuth mYetki;
    private String aktifkullaniciId;




    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        OzelSohbetlerView= inflater.inflate(R.layout.fragment_chats, container, false);

        //Firebase
        mYetki=FirebaseAuth.getInstance();
        aktifkullaniciId=mYetki.getCurrentUser().getUid();
        sohbetYolu= FirebaseDatabase.getInstance().getReference().child("Sohbetler").child(aktifkullaniciId);
        kullaniciYolu= FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

        //Recyler
        sohbetlerListesi=OzelSohbetlerView.findViewById(R.id.sohbetlerListesi);
        sohbetlerListesi.setLayoutManager(new LinearLayoutManager(getContext()));

        return OzelSohbetlerView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> secenekler = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(sohbetYolu,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,SohbetlerViewHolder>adapter=new FirebaseRecyclerAdapter<Users, SohbetlerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull final SohbetlerViewHolder holder, int position, @NonNull Users model) {


                final String kullaniciIdleri = getRef(position).getKey();
                final String[] resimAl = {"Varsayılan Resim"};

                //Veritabanından veri çağırma

                kullaniciYolu.child(kullaniciIdleri).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.hasChild("username")){
                            final String adAl=dataSnapshot.child("username").getValue().toString();
                            final String durumAl=dataSnapshot.child("about").getValue().toString();

                            }
                            final String adAl=dataSnapshot.child("username").getValue().toString();
                            final String durumAl=dataSnapshot.child("about").getValue().toString();

                            //Veri tabanından gelen adı ve durumu kontrollere aktarma
                            holder.kullaniciAdi.setText(adAl);

                            //Veri tabanından kullanıcı durumuna yönelik verileri çekme
                            if(dataSnapshot.child("kullaniciDurumu").hasChild("durum")) {
                                String durum = dataSnapshot.child("kullaniciDurumu").child("durum").getValue().toString();
                                String zaman = dataSnapshot.child("kullaniciDurumu").child("zaman").getValue().toString();
                            }



                            //Her satıra tıklandığında
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Chat aktivitesine git Intentle veri gonder
                                    Intent chatAktivite = new Intent(getContext(), ChatActivity.class);
                                    chatAktivite.putExtra("kullanici_id_ziyaret",kullaniciIdleri);
                                    chatAktivite.putExtra("kullanici_adi_ziyaret",adAl);
                                    chatAktivite.putExtra("resim_ziyaret", resimAl[0]);
                                    startActivity(chatAktivite);

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @NonNull
            @Override
            public SohbetlerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_layout,viewGroup,false);

                return new SohbetlerViewHolder(view);

            }
        };

        sohbetlerListesi.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    public static class SohbetlerViewHolder extends RecyclerView.ViewHolder
    {
        //Kontroller

        TextView kullaniciAdi,kullaniciDurumu;


        public SohbetlerViewHolder(@NonNull View itemView) {
            super(itemView);

            //Kontrol tanımlamaları

            kullaniciAdi=itemView.findViewById(R.id.kullanici_adi_gosterme_chat_activity);
            kullaniciDurumu=itemView.findViewById(R.id.kullanici_durumu_gosterme_chat_activity);
        }
    }
}

