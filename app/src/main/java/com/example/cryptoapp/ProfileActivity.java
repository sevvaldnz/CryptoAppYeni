package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.GnssAntennaInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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


public class ProfileActivity extends AppCompatActivity {

    private String alinanKullanıcıId, aktifKullaniciId, Aktif_Durum;
    private TextView Username, About;
    private Button SendMessage, AcceptMessage;
    private DatabaseReference userPath, chatRequestPath, chatsPath, notifyPath;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        alinanKullanıcıId = getIntent().getStringExtra("user_id").toString();

        Toast.makeText(this, "Hello, your Id is: " + alinanKullanıcıId, Toast.LENGTH_LONG).show();

        Username = findViewById(R.id.usernameTextView);
        About = findViewById(R.id.aboutTextView);
        SendMessage = findViewById(R.id.sendMessageButton);
        AcceptMessage = findViewById(R.id.acceptMessageButton);

        Aktif_Durum = "new";

        userPath = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestPath = FirebaseDatabase.getInstance().getReference().child("Message Request");
        chatsPath = FirebaseDatabase.getInstance().getReference().child("Chats");
        notifyPath = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mAuth = FirebaseAuth.getInstance();
        aktifKullaniciId = mAuth.getCurrentUser().getUid();

        KullaniciBilgisiAl();
    }

    private void KullaniciBilgisiAl() {
        userPath.child(alinanKullanıcıId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("about"))) {
                    String username = snapshot.child("username").getValue(String.class);
                    String about = snapshot.child("about").getValue(String.class);

                    Username.setText(username);
                    About.setText(about);

                    chatTalepleriniYonet();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void chatTalepleriniYonet() {
        chatRequestPath.child(aktifKullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(alinanKullanıcıId)) {
                    DataSnapshot requestSnapshot = snapshot.child(alinanKullanıcıId);
                    if (requestSnapshot.hasChild("Request_type")) {
                        String Request_type = requestSnapshot.child("Request_type").getValue(String.class);
                        if (Request_type != null && Request_type.equals("Sent")) {
                            Aktif_Durum = "Request sent";
                            SendMessage.setText("Cancel Message Request");
                        }
                        else{
                            Aktif_Durum= "request_received";
                            SendMessage.setText("Accept Message Request");
                            AcceptMessage.setVisibility(View.VISIBLE);
                            AcceptMessage.setEnabled(true);

                            AcceptMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MesajTalebiIptal();
                                }
                            });
                        }
                    }
                }
                else{
                    chatsPath.child(aktifKullaniciId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(alinanKullanıcıId)){
                                        Aktif_Durum = "friends";
                                        SendMessage.setText("Delete this Chat");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        if (aktifKullaniciId.equals(alinanKullanıcıId)) {
            SendMessage.setVisibility(View.INVISIBLE);
        } else {
            SendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendMessage.setEnabled(false);
                    if (Aktif_Durum.equals("new")) {
                        SohbetTalebiGonder();
                    } else if (Aktif_Durum.equals("Request sent")) {
                        MesajTalebiIptal();
                    }else if (Aktif_Durum.equals("request_received")){
                        MesajTalebiKabul();
                    }
                    else if (Aktif_Durum.equals("friends")){
                        OzelSohbetiSil();
                }
                }
            });
        }
    }

    private void OzelSohbetiSil() {

        chatsPath.child(aktifKullaniciId).child(alinanKullanıcıId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatsPath.child(alinanKullanıcıId).child(aktifKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendMessage.setEnabled(true);
                                Aktif_Durum = "new";
                                SendMessage.setText("Message Request Send");

                                AcceptMessage.setVisibility(View.INVISIBLE);
                                AcceptMessage.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void MesajTalebiKabul() {
        chatsPath.child(aktifKullaniciId).child(alinanKullanıcıId).child("Chat").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            chatsPath.child(alinanKullanıcıId).child(aktifKullaniciId).child("Chats").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRequestPath.child(aktifKullaniciId).child(alinanKullanıcıId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatRequestPath.child(alinanKullanıcıId).child(aktifKullaniciId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    SendMessage.setEnabled(true);
                                                                                    Aktif_Durum ="friends";
                                                                                    SendMessage.setText("Delete this Chat");
                                                                                    AcceptMessage.setVisibility(View.INVISIBLE);
                                                                                    AcceptMessage.setEnabled(false);

                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void MesajTalebiIptal() {
        chatRequestPath.child(aktifKullaniciId).child(alinanKullanıcıId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    chatRequestPath.child(alinanKullanıcıId).child(aktifKullaniciId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendMessage.setEnabled(true);
                                Aktif_Durum = "new";
                                SendMessage.setText("Message Request Send");

                                AcceptMessage.setVisibility(View.INVISIBLE);
                                AcceptMessage.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SohbetTalebiGonder() {
        chatRequestPath.child(aktifKullaniciId).child(alinanKullanıcıId).child("Request_type").setValue("Sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestPath.child(alinanKullanıcıId).child(aktifKullaniciId).child("Request_type")
                                    .setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap <String,String> chatBildirim= new HashMap<>();
                                                chatBildirim.put("from",aktifKullaniciId);
                                                chatBildirim.put("type","request");

                                                notifyPath.child(alinanKullanıcıId).push().setValue(chatBildirim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            SendMessage.setEnabled(true);
                                                            Aktif_Durum = "Request sent";
                                                            SendMessage.setText("Cancel Message Request");
                                                        }
                                                    }
                                                });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}

