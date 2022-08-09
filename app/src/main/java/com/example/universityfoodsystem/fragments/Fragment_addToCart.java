package com.example.universityfoodsystem.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityfoodsystem.Item;
import com.example.universityfoodsystem.MyCart;
import com.example.universityfoodsystem.Order;
import com.example.universityfoodsystem.R;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Fragment_addToCart extends Fragment {
    int minteger = 1;
    FrameLayout fl;

    public Fragment_addToCart() {
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
        View v = inflater.inflate(R.layout.fragment_add_to_cart, container, false);
        String item = getArguments().getString("Item");
        final String[] price = {getArguments().getString(item)};
        Double originalPrice = Double.parseDouble(price[0].replace("$", ""));
        fl = (FrameLayout) v;

        TextView tvItem = (TextView)v.findViewById(R.id.cartItem);
        tvItem.setText(item);
        TextView tvPrice = (TextView)fl.findViewById(R.id.price);
        tvPrice.setText(price[0]);

        ImageButton inc = fl.findViewById(R.id.increase);
        ImageButton dec = fl.findViewById(R.id.decrease);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price[0] = increaseInteger(fl, price[0], originalPrice);
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price[0] = decreaseInteger(fl, price[0], originalPrice);
            }
        });
        Button addToCart = fl.findViewById(R.id.button2);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Items have been added to your cart", Toast.LENGTH_SHORT).show();
                MyCart cart = MyCart.getInstance();
                cart.addItems(item, price[0], String.valueOf(minteger));
                moveToNewActivity();
            }
        });
        return v;
    }
    public String increaseInteger(View view, String price, Double ogPrice) {
        if(minteger < 10){
            minteger = minteger + 1;
            String newPrice = price.replace("$","");
            double doublePrice = Double.parseDouble(newPrice);
            doublePrice += ogPrice;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String moneyString = formatter.format(doublePrice);
            if (moneyString.endsWith(".00")) {
                int centsIndex = moneyString.lastIndexOf(".00");
                if (centsIndex != -1) {
                    moneyString = moneyString.substring(1, centsIndex);
                }
            }
            display(minteger, fl, moneyString);
            return moneyString;
        }
        return price;
    }

    public String decreaseInteger(View view, String price, Double ogPrice) {
        if(minteger > 1){
            minteger = minteger - 1;
            String newPrice = price.replace("$","");
            double doublePrice = Double.parseDouble(newPrice);
            doublePrice -= ogPrice;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            String moneyString = formatter.format(doublePrice);
            if (moneyString.endsWith(".00")) {
                int centsIndex = moneyString.lastIndexOf(".00");
                if (centsIndex != -1) {
                    moneyString = moneyString.substring(1, centsIndex);
                }
            }
            display(minteger, fl, moneyString);
            return moneyString;
        }
        return price;
    }

    private void display(int number, View v, String price) {
        TextView displayInteger = (TextView)fl.findViewById(R.id.quantity);
        displayInteger.setText("" + number);

        TextView tvPrice = (TextView)fl.findViewById(R.id.price);
        tvPrice.setText(price);
    }
    private void moveToNewActivity () {
        Bundle bundle = getActivity().getIntent().getExtras();
        Intent i = new Intent(getActivity(), Order.class);
        i.putExtras(bundle);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }


}