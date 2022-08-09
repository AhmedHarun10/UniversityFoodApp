package com.example.universityfoodsystem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.HomeScreen;
import com.example.universityfoodsystem.Order;
import com.example.universityfoodsystem.R;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Vector;


public class HomeFragment extends Fragment {
    private Vector<String> items = new Vector<String>();
    private Vector<String> imageNames = new Vector<String>();
    private Vector<String> foodTypes = new Vector<String>();
    private Vector<String> addresses = new Vector<String>();
    private HashMap<String, String> map = new HashMap<>(); //map restaurant name with phone number for identification.
    private Vector<String> logos = new Vector<String>();
    private ImageView imageView;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView = view.findViewById(R.id.imageViewLogoHomeScreen);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Restaurant2");
        Query restaurantsQuery = ref.orderByKey().startAt("0");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHomeScreen);
        recyclerView.setLayoutManager(layoutManager);
        User user = User.getInstance();
        TextView textViewWelcomeUser = view.findViewById(R.id.textViewWelcomeUser);
        if (!(user.getFullName() == null)){
            String[] fullName = user.getFullName().split(" ");
            String name = "Welcome, " + fullName[0];
            textViewWelcomeUser.setText(name);
        }
        Bundle bundle = this.getArguments();
        if(bundle != null){
            StorageReference pathReference;
            FirebaseStorage storage = FirebaseStorage.getInstance();
            pathReference = storage.getReference("gifs/orderontheway2.gif");
            Log.e(pathReference.toString(),imageView.toString());
            Glide
                    .with(getActivity())
                    .load(pathReference)
                    .into(imageView);
        }

        restaurantsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                imageNames.clear();
                foodTypes.clear();
                addresses.clear();
                logos.clear();
                for (DataSnapshot snapShot : snapshot.getChildren()) {
                    String temp = snapShot.child("restaurantName").getValue().toString();
                    items.add(temp);
                    String tempFoodTypes = snapShot.child("restaurantGenre").getValue().toString();
                    foodTypes.add(tempFoodTypes);
                    String tempAddress = snapShot.child("restaurantAddress").getValue().toString();
                    tempAddress += ", " + snapShot.child("restaurantZipcode").getValue().toString();
                    addresses.add(tempAddress);
                    if (snapShot.child("restaurantImage").getValue().toString() != null) {
                        String temp2 = snapShot.child("restaurantImage").getValue().toString();
                        imageNames.add(temp2);
                    }
                    String tempLogo = snapShot.child("restaurantLogo").getValue().toString();
                    logos.add(tempLogo);
                    String phone = snapShot.child("restaurantPhone").getValue().toString();
                    map.put(temp, phone);
                }
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new CustomAdapter(items, imageNames, foodTypes, addresses,logos, new CustomAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), Order.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("restaurant", item);
                        bundle.putString(item, map.get(item));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}