package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Users;


public class FindfriendActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView.Recycler Findfriend_recyler_list;

    private DatabaseReference kullanıcıyolu;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);

        //recyler
        Findfriend_recyler_list=findViewById(R.id.Findfriend_recyler_list);
        Findfriend_recyler_list.setViewCacheSize(new LinearLayoutManager(context list));


        //Toolbar
        mToolbar=findViewById(R.id.findfriend_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");

        kullanıcıyolu= FirebaseDatabase.getInstance().getReference().child(Users);

    }
    @Override
    protected void onStart(){
        super.onStart();
        // in start

        //sorgu

        FirebaseOptions.<Users> seçenekler=new FirebaseOptions.Builder<Users>().setQuery (kullanıcıyolu Users.class)
                .build();


        Object seçenekler;
        activityFirebaseRecyclerAdapter<Users, FindfriendActivity>adapter = new activityFirebaseRecyclerAdapte <users, FindfriendActivity>(seçenekler){
        @Override
        protected void onBindViewHolder(@NonNull FindfriendActivity, int position, @NonNull Users)

            holder.username.setText(model.getusername);
            holder.about.setText(model.getabout);


            //Tıklandığında
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View){
                    String tıklanan_kullanıcı_Id_göster= getReferrer(position).getKey()
                }
            }

        };

        {
            @NonNull
            @Override
            public FindfriendActivity onCreateViewHolder(ViewGroup viewGroup, int i){

               View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_user_layout, viewGroup, false);
                FindfriendActivity viewHolder = new FindfriendActivity(view);
                return viewHolder;
        }
        }    } ;


    FindfriendActivity.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    adapter.startListening;

       public static class FindfriendViewHolder extends  RecyclerView.ViewHolder{

           TextView username, about;


    }
     public static FindfriendActivity(@NonNull View itemView){
        super(itemView);

        //Tanımlar
        username=itemView.findViewById(R.id.usernameEditText);
        about=itemView.findViewById(R.id.aboutEditText);
     }

}