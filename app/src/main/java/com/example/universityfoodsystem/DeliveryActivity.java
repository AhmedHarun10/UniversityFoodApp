package com.example.universityfoodsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DeliveryActivity extends AppCompatActivity {

    private Button pickUp;
    private Button delivery;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        pickUp = findViewById(R.id.pickup_option);
        delivery = findViewById(R.id.delivery_option);
        Double price = getIntent().getExtras().getDouble("price");
        phone = getIntent().getExtras().getString("phone");
        Bundle bundle = new Bundle();
        bundle.putDouble("price", price);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryFragment deliveryFragment = new DeliveryFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                deliveryFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.delivery_container,deliveryFragment).addToBackStack(null)
                .commit();
            }
        });

        pickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PaymentsActivity.class);
                intent.putExtra("price", price);
                intent.putExtra("delivery", 0);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
    }
    public String GetRestaurant(){
        return phone;
    }
}