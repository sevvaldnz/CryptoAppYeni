package com.example.cryptoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;


public class RequestsFragment extends Fragment {

    private View TaleplerFragmentView;

    private RecyclerView taleplerlistem;
    private DatabaseReference chatRequestPath, usersPath, chatsPath;
    private FirebaseAuth mAuth;
    private String aktifKullaniciId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RequestsFragment() {

    }

    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TaleplerFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth=FirebaseAuth.getInstance();
        aktifKullaniciId=mAuth.getCurrentUser().getUid();

        chatRequestPath= FirebaseDatabase.getInstance().getReference().child("Message Request");
        usersPath= FirebaseDatabase.getInstance().getReference().child("Users");
        chatsPath= FirebaseDatabase.getInstance().getReference().child("Chats");

        taleplerlistem= TaleplerFragmentView.findViewById(R.id.chat_talepleri_listesi);
        taleplerlistem.setLayoutManager(new LinearLayoutManager(getContext()));

        return TaleplerFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> secenekler = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(chatRequestPath.child(aktifKullaniciId), Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, TaleplerViewHolder> adapter = new FirebaseRecyclerAdapter<Users, TaleplerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull TaleplerViewHolder holder, int position, @NonNull Users model) {
                holder.itemView.findViewById(R.id.chat_kabul_buttonu).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.chat_iptal_buttonu).setVisibility(View.VISIBLE);

                final String kullanici_id_listesi = getRef(position).getKey();

                DatabaseReference requestTypeAl = getRef(position).child("Request_type").getRef();
                requestTypeAl.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String type=snapshot.getValue().toString();
                            if(type.equals("Received")){
                                usersPath.child(kullanici_id_listesi).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        final String talepKullaniciAdi = snapshot.child("username").getValue().toString();
                                        final String talepKullaniciHakkinda = snapshot.child("about").getValue().toString();

                                        holder.username.setText(talepKullaniciAdi);
                                        holder.about.setText("User wants to communicate with you!");

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence secenekler[]= new CharSequence[]{
                                                        "Accept",
                                                        "Decline"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(talepKullaniciAdi+" Message Request");

                                                builder.setItems(secenekler, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if(i==0){
                                                            acceptChatRequest(kullanici_id_listesi);
                                                        }
                                                        if(i==1){
                                                            cancelChatRequest(kullanici_id_listesi);
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public TaleplerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout, parent, false);
                TaleplerViewHolder holder = new TaleplerViewHolder(view);
                return holder;
            }
        };
        taleplerlistem.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    private void acceptChatRequest(String userId) {
        chatsPath.child(aktifKullaniciId).child(userId).child("Chats")
                .setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatsPath.child(userId).child(aktifKullaniciId)
                                    .child("Chats").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestPath.child(aktifKullaniciId).child(userId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestPath.child(userId).child(aktifKullaniciId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Toast.makeText(getContext(), "Chat saved", Toast.LENGTH_LONG).show();
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

    private void cancelChatRequest(String userId) {
        chatRequestPath.child(aktifKullaniciId).child(userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestPath.child(userId).child(aktifKullaniciId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Chat request canceled", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });
    }

    public static class TaleplerViewHolder extends RecyclerView.ViewHolder{

        TextView username, about;
        Button kabulButtonu, iptalButtonu;

        public TaleplerViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.usernameEditText);
            about=itemView.findViewById(R.id.aboutEditText);
            kabulButtonu=itemView.findViewById(R.id.chat_kabul_buttonu);
            iptalButtonu=itemView.findViewById(R.id.chat_iptal_buttonu);
        }
    }

}

