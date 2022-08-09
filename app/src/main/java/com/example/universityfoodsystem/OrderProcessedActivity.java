package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OrderProcessedActivity extends AppCompatActivity {
    private String phone;
    private double price;
    private String priceFormatted;
    private int delivery;
    private boolean deliveryBool;
    private TextView textViewRestaurant;
    private TextView textViewTotal;
    private String restaurantName;
    private String userEmail;
    private String userID;
    private Button button;
    private String imageName;
    private ImageView imageView;
    private MyCart cart;
    private ArrayList<Item> cartList = new ArrayList<Item>();
    private int orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_processed);
        textViewRestaurant = findViewById(R.id.textViewOrderProcessingRestaurant);
        textViewTotal = findViewById(R.id.textViewOrderProcessingTotal);
        imageView = findViewById(R.id.imageViewOrderProcessingImage);
        button = findViewById(R.id.buttonOrderProcessed);

        phone = getIntent().getExtras().getString("phone");

        User user = User.getInstance();
        userEmail = user.getEmail();
        userID = user.getSchoolId();

        price = getIntent().getExtras().getDouble("price");
        delivery = getIntent().getExtras().getInt("delivery");
        if(delivery == 0){
            deliveryBool = false;
        }
        else{
            deliveryBool = true;
        }


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Restaurant2");
        DatabaseReference orderRef = firebaseDatabase.getReference().child("Orders");
        FirebaseStorage storage = FirebaseStorage.getInstance();


        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        priceFormatted = "Total: " + formatter.format(price);
        cart = MyCart.getInstance();
        cartList = cart.getCart();
        textViewTotal.setText(priceFormatted);
        Query query = databaseReference.orderByKey().equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot SnapShot: snapshot.getChildren()){
                    restaurantName = SnapShot.child("restaurantName").getValue().toString();
                    imageName = SnapShot.child("restaurantLogo").getValue().toString();
                }

                textViewRestaurant.setText("Your order for\n" + restaurantName);
                DatabaseReference messageRef = firebaseDatabase.getReference().child("Messages");
                messageRef.child(userID).child("receiver").setValue(user.getEmail());
                messageRef.child(userID).child("sender").setValue(restaurantName);
                messageRef.child(userID).child("mUrl").setValue("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRaIiNMya3lxSnFfuO07l-WUI7r8_tfwOFrmw&usqp=CAU");
                messageRef.child(userID).child("message").setValue("Your order is on the way!");
                StorageReference storageRef = storage.getReference().child("restaurant_logos/" + imageName);
                Glide.with(getBaseContext())
                        .load(storageRef)
                        .into(imageView);

                Query orderNumbersQuery = orderRef.child(userID).orderByKey().startAt("0");
                orderNumber = 10000;
                orderNumbersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapShot1: snapshot.getChildren()){
                            orderNumber -= 1;
                        }
                        orderNumber -= 1;
                        String orderNumberString = String.valueOf(orderNumber);
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd 'at' KK:mm:ss a z");
                        Date date = new Date(System.currentTimeMillis());
                        Date date2 = new Date(System.currentTimeMillis());
                        if(deliveryBool == true){
                            date2.setTime(date2.getTime() + TimeUnit.MINUTES.toMillis(30));
                        }
                        else{
                            date2.setTime(date2.getTime() + TimeUnit.MINUTES.toMillis(20));
                        }

                        orderRef.child(userID).child(orderNumberString).child("orderRestaurant").setValue(restaurantName);
                        orderRef.child(userID).child(orderNumberString).child("orderDelivery").setValue(deliveryBool);
                        orderRef.child(userID).child(orderNumberString).child("orderDateAndTime").setValue(dateFormatter.format(date));
                        orderRef.child(userID).child(orderNumberString).child("orderExpected").setValue(dateFormatter.format(date2));
                        orderRef.child(userID).child(orderNumberString).child("orderTotal").setValue(formatter.format(price));
                        for(int i = 0; i < cartList.size(); i++){
                            Item current = cartList.get(i);
                            orderRef.child(userID).child(orderNumberString).child("orderDetails").child(String.valueOf(i)).child("itemName").setValue(current.getText1());
                            orderRef.child(userID).child(orderNumberString).child("orderDetails").child(String.valueOf(i)).child("itemPrice").setValue(current.getText2());
                            orderRef.child(userID).child(orderNumberString).child("orderDetails").child(String.valueOf(i)).child("itemQuantity").setValue(current.getText3());
                        }
                        MyCart.reset();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                startActivity(intent);
            }
        });



    }
}