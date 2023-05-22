package com.example.cryptoapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private View ContactsView;
    private RecyclerView ContactsList;
    private DatabaseReference chatsPath, usersPath;
    private FirebaseAuth mAuth;
    private String aktifKullaniciId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        // Inflate the layout for this fragment
        ContactsView= inflater.inflate(R.layout.fragment_contacts, container, false);
        ContactsList=ContactsView.findViewById(R.id.contacts_list);
        ContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth= FirebaseAuth.getInstance();

        aktifKullaniciId=mAuth.getCurrentUser().getUid();

        chatsPath= FirebaseDatabase.getInstance().getReference().child("Chats").child(aktifKullaniciId);
        usersPath = FirebaseDatabase.getInstance().getReference().child("Users");


        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions secenekler= new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(chatsPath,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,KisilerViewHolder>adapter = new FirebaseRecyclerAdapter<Users, KisilerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull KisilerViewHolder holder, int position, @NonNull Users model) {
                String tiklananSatirKullaniciIdsi = getRef(position).getKey();

                usersPath.child(tiklananSatirKullaniciIdsi).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("about")){
                            String Username = snapshot.child("username").getValue().toString();
                            String About = snapshot.child("about").getValue().toString();

                            holder.Username.setText(Username);
                            holder.About.setText(About);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public KisilerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_user_layout,parent,false);
                KisilerViewHolder viewHolder = new KisilerViewHolder(view);
                        return viewHolder;
            }
        };
        ContactsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class KisilerViewHolder extends RecyclerView.ViewHolder{

        TextView Username, About;
        public KisilerViewHolder(@NonNull View itemView) {
            super(itemView);

            Username=itemView.findViewById(R.id.usernameEditText);
            About=itemView.findViewById(R.id.aboutEditText);
        }
    }
}