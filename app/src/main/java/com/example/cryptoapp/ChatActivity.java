package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptoapp.adapter.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Model.Messages;

public class ChatActivity extends AppCompatActivity {

    private String IdMesajAlici, AdMesajAlici, IdMesajGonderen;
    private TextView username, lastseen;
    private Toolbar ChatToolbar;
    private Button SendMessageButton;
    private EditText enterMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference messagePath, userPath;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager LinearLayoutManager;
    private MessageAdapter MessageAdapter;
    private RecyclerView kullaniciMesajlariListesi;
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_KEY = "mysecretkey12345";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        IdMesajAlici = getIntent().getExtras().get("kullanici_id_ziyaret").toString();
        AdMesajAlici = getIntent().getExtras().get("kullanici_adi_ziyaret").toString();

        username=findViewById(R.id.kullanici_adi_gosterme_chat_activity);
        SendMessageButton=findViewById(R.id.sendButton);
        enterMessage=findViewById(R.id.girilen_mesaj);

        MessageAdapter = new MessageAdapter(messagesList);
        kullaniciMesajlariListesi=findViewById(R.id.kullanicilarin_ozel_mesajlarinin_listesi);
        LinearLayoutManager= new LinearLayoutManager(this);
        kullaniciMesajlariListesi.setLayoutManager(LinearLayoutManager);
        kullaniciMesajlariListesi.setAdapter(MessageAdapter);

        mAuth= FirebaseAuth.getInstance();
        messagePath= FirebaseDatabase.getInstance().getReference();
        userPath= FirebaseDatabase.getInstance().getReference();

        IdMesajGonderen= mAuth.getCurrentUser().getUid();

        ChatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        username.setText(AdMesajAlici);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MesajGonder();
            }
        });
    }

    private void SonGorulmeyiGoster() {
        userPath.child("Users").child(IdMesajGonderen).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("User_Status")) {
                    DataSnapshot userStatusSnapshot = snapshot.child("User_Status");
                    if (userStatusSnapshot.hasChild("status")) {
                        String durum = userStatusSnapshot.child("status").getValue().toString();
                        String tarih = userStatusSnapshot.child("date").getValue().toString();
                        String zaman = userStatusSnapshot.child("time").getValue().toString();

                        if (durum.equals("online")) {
                            lastseen.setText("online");
                        } else if (durum.equals("offline")) {
                            lastseen.setText("Last seen: " + tarih + " " + zaman);
                        }
                    }
                } else {
                    lastseen.setText("offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        messagePath.child("Messages").child(IdMesajGonderen).child(IdMesajAlici)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages = snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        MessageAdapter.notifyDataSetChanged();
                        kullaniciMesajlariListesi.smoothScrollToPosition(kullaniciMesajlariListesi.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void MesajGonder() {
        String mesajMetni = enterMessage.getText().toString();

        if(TextUtils.isEmpty(mesajMetni)){
            Toast.makeText(this, "You must write something!", Toast.LENGTH_SHORT).show();
        }
        else{
            String sendMessagePath="Messages/"+IdMesajGonderen+"/"+IdMesajAlici;
            String getMessagePath="Messages/"+IdMesajAlici+"/"+IdMesajGonderen;

            DatabaseReference userMessageKeyPath = messagePath.child("Messages").child(IdMesajGonderen).child(IdMesajAlici).push();

            String mesajEklemeId = userMessageKeyPath.getKey();

            String sifreliMesaj = encryptMessage(mesajMetni, AES_KEY);

            Map mesajMetniGovdesi= new HashMap();
            mesajMetniGovdesi.put("from",IdMesajGonderen);
            mesajMetniGovdesi.put("message",sifreliMesaj);
            mesajMetniGovdesi.put("type","text");

            Map MesajGovdesiDetaylari = new HashMap<>();
            MesajGovdesiDetaylari.put(sendMessagePath+"/"+mesajEklemeId,mesajMetniGovdesi);
            MesajGovdesiDetaylari.put(getMessagePath+"/"+mesajEklemeId,mesajMetniGovdesi);

            messagePath.updateChildren(MesajGovdesiDetaylari).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message Sent!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(ChatActivity.this, "Message could not be Sent!", Toast.LENGTH_SHORT).show();
                    }
                    enterMessage.setText("");
                }
            });
        }
    }

    private String encryptMessage(String message, String secretKey) {
        try {
            Key aesKey = new SecretKeySpec(secretKey.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            return new String(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}








