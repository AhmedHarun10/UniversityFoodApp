package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteListActivity extends AppCompatActivity {
    private ImageView returnButton;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference mRef;
    private FavoriteAdapter favoriteAdapter;
    private ArrayList<Favorite> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        recyclerView = findViewById(R.id.recycle_fav);
        returnButton = findViewById(R.id.previous_page_fav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        favoriteAdapter = new FavoriteAdapter(this,list);
        recyclerView.setAdapter(favoriteAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mRef = databaseReference.child("Favorites List").child(User.getInstance().getSchoolId());
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Favorite favorite = dataSnapshot.getValue(Favorite.class);
                        list.add(favorite);
                    }
                    favoriteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),HomeScreen.class);
                startActivity(intent);
            }
        });


    }
}