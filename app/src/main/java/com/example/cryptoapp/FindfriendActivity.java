package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import Model.Users;

public class FindfriendActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView Findfriend_recycler_list;

    private DatabaseReference kullanıcıyolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        // RecyclerView
        Findfriend_recycler_list = findViewById(R.id.findfriend_recyler_list);
        Findfriend_recycler_list.setLayoutManager(new LinearLayoutManager(this));

        // Toolbar
        mToolbar = findViewById(R.id.findfriend_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Choose Contact");

        kullanıcıyolu = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Query
        Query query = kullanıcıyolu;

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, FindfriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, FindfriendViewHolder>(options) {

                    @NonNull
                    @Override
                    public FindfriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_layout, viewGroup, false);
                        return new FindfriendViewHolder(view);
                    }

                    @SuppressLint("RecyclerView")
                    @Override
                    protected void onBindViewHolder(@NonNull FindfriendViewHolder holder, int position, @NonNull Users model) {
                        holder.username.setText(model.getUsername());
                        holder.about.setText(model.getAbout());

                        // Click Listener
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tıklanan_kullanıcı_Id_göster = getRef(position).getKey();
                                Intent profileIntent = new Intent(FindfriendActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("user_id", tıklanan_kullanıcı_Id_göster);
                                startActivity(profileIntent);
                            }
                        });
                    }
                };

        Findfriend_recycler_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    public static class FindfriendViewHolder extends RecyclerView.ViewHolder {

        TextView username, about;

        public FindfriendViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assignments
            username = itemView.findViewById(R.id.usernameEditText);
            about = itemView.findViewById(R.id.aboutEditText);
        }
    }
}
