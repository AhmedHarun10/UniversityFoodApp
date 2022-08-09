package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.fragments.Fragment_addToCart;
import com.example.universityfoodsystem.fragments.OrderFragment_shopping_cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Order extends AppCompatActivity {
    ImageView restaurant_picture;
    ListView menuItems;
    Bundle bundle2 = new Bundle();
    Bundle prices = new Bundle();
    String phone;
    int minteger = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Bundle bundle = getIntent().getExtras();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String restaurant_name = bundle.getString("restaurant");
        phone = bundle.getString(restaurant_name);
        // getting ImageView by its id
        restaurant_picture = findViewById(R.id.rPicture);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Restaurant2").child(phone);
        DatabaseReference getImage = databaseReference.child("restaurantImage");
        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String link = "restaurant_photos/" + dataSnapshot.getValue(String.class);
                StorageReference storageRef = storage.getReference().child(link);
                Glide.with(getApplicationContext())
                        .load(storageRef)
                        .into(restaurant_picture);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(Order.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });

        menuItems = findViewById(R.id.menu_items);
        ArrayList<String> menu = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Restaurant2").child(phone).child("menu");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menu.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    String item = snapshot1.getKey().toString();
                    menu.add(item);
                    prices.putString(item, snapshot1.getValue().toString());
                }
                ArrayAdapter adapter = new ArrayAdapter(getBaseContext() , R.layout.custom_listview, menu);
                menuItems.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        menuItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entry = (String) parent.getItemAtPosition(position);

                Fragment_addToCart fragobj = new Fragment_addToCart();
                FragmentManager fg = getSupportFragmentManager();
                FragmentTransaction ft = fg.beginTransaction();

                Bundle sendData = new Bundle();
                bundle.putString("Item", entry);
                bundle.putString(entry, prices.getString(entry));
                fragobj.setArguments(bundle);

                ft.replace(R.id.mainOrderPage, fragobj);
                ft.commit();
            }
        });
        ImageButton shoppingCart = findViewById(R.id.imageButton2);
        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new OrderFragment_shopping_cart());
            }
        });


    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();
        ft.replace(R.id.mainOrderPage, fragment).addToBackStack(null);
        ft.commit();
    }

    public String GetRestaurant(){
        return phone;
    }
}