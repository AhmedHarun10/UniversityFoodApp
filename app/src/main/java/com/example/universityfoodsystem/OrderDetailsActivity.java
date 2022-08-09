package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OrderDetailsActivity extends AppCompatActivity {
    private ArrayList<Item> orderItems = new ArrayList<Item>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private String restaurantName;
    private String orderTotal;
    private String orderDate;
    private String orderExpected;
    private String deliveryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        layoutManager = new LinearLayoutManager(this);
        TextView textViewRestaurant = findViewById(R.id.textViewOrderDetailsRestaurant);
        TextView textViewDate = findViewById(R.id.textViewOrderDetailsDate);
        TextView textViewTotal = findViewById(R.id.textViewOrderDetailsTotal);
        TextView textViewOrderNum = findViewById(R.id.textViewOrderDetailsNumber);
        TextView textViewExpectedDate = findViewById(R.id.textViewOrderDetailsExpected);

        Button buttonCancelOrder = findViewById(R.id.buttonCancelOrder);
        buttonCancelOrder.setVisibility(View.INVISIBLE);

        User user = User.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = firebaseDatabase.getReference().child("Orders").child(user.getSchoolId()).child(getIntent().getExtras().getString("orderNumber"));
        Query orderQuery = orderRef.orderByKey().startAt("0");
        orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantName = snapshot.child("orderRestaurant").getValue().toString();
                orderTotal = snapshot.child("orderTotal").getValue().toString();
                orderDate = snapshot.child("orderDateAndTime").getValue().toString();
                orderExpected = snapshot.child("orderExpected").getValue().toString();
                deliveryString = snapshot.child("orderDelivery").getValue().toString();
                Date currentDate = new Date(System.currentTimeMillis());
                Date dateFiveMinutes = new Date(System.currentTimeMillis());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd 'at' KK:mm:ss a z");;
                SimpleDateFormat etaFormatter = new SimpleDateFormat("KK:mm:ss a");
                try {
                    Date orderTime = dateFormatter.parse(orderDate);
                    Date expectedTime = dateFormatter.parse(orderExpected);
                    if(expectedTime.compareTo(currentDate) > 0){
                        if(deliveryString.matches("true")){
                            Date dateFifteen = new Date(System.currentTimeMillis());
                            dateFifteen.setTime(orderTime.getTime() + TimeUnit.MINUTES.toMillis(15));
                            if(currentDate.compareTo(dateFifteen) < 0){
                                textViewExpectedDate.setText("Your order is being worked on. \nETA: " + etaFormatter.format(expectedTime));

                            }
                            else {
                                textViewExpectedDate.setText("Your order is on the way! \nETA: " + etaFormatter.format(expectedTime));
                            }
                        }
                        else{
                            textViewExpectedDate.setText("Your order is being worked on. \nPick up at: " + etaFormatter.format(expectedTime));
                        }
                    }
                    else{
                        Date dateFifteenAfter = new Date(System.currentTimeMillis());
                        dateFifteenAfter.setTime(expectedTime.getTime() + TimeUnit.MINUTES.toMillis(15));
                        if(deliveryString.matches("true")){
                            textViewExpectedDate.setText("Delivered!");
                        }
                        else if(currentDate.compareTo(dateFifteenAfter) < 0){
                            textViewExpectedDate.setText("Order ready for pickup!");
                        }
                        else{
                            textViewExpectedDate.setText("Order complete!");
                        }

                    }

                    dateFiveMinutes.setTime(orderTime.getTime() + TimeUnit.MINUTES.toMillis(5));
                    if(currentDate.compareTo(dateFiveMinutes) < 0){
                        buttonCancelOrder.setVisibility(View.VISIBLE);
                    }


                } catch (ParseException e) {
                    e.printStackTrace();

                }
                textViewRestaurant.setText(restaurantName);
                textViewTotal.setText(orderTotal);
                textViewDate.setText(orderDate);
                textViewOrderNum.setText("Order Number: " + String.valueOf(10000 - Integer.parseInt(getIntent().getExtras().getString("orderNumber"))));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query orderItemsQuery = orderRef.child("orderDetails").orderByKey().startAt("0");
        orderItemsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapShot: snapshot.getChildren()){
                    Item tempItem = new Item(snapShot.child("itemName").getValue().toString(), snapShot.child("itemPrice").getValue().toString(), snapShot.child("itemQuantity").getValue().toString());
                    orderItems.add(tempItem);

                }
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new ExampleAdapter(orderItems));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "This order has been cancelled!", Toast.LENGTH_SHORT).show();
                orderRef.removeValue();
            }
        });
        Button ratingBtn = findViewById(R.id.leaveReview);
        RatingBar ratingStars = findViewById(R.id.ratingBar);
        float rating = 0;
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OrderDetailsActivity.this, "Thank you for letting us know!", Toast.LENGTH_LONG).show();
            }
        });

    }
}