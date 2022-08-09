package com.example.universityfoodsystem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.universityfoodsystem.DeliveryActivity;
import com.example.universityfoodsystem.ExampleAdapter;
import com.example.universityfoodsystem.Item;
import com.example.universityfoodsystem.MyCart;
import com.example.universityfoodsystem.Order;
import com.example.universityfoodsystem.PaymentsActivity;
import com.example.universityfoodsystem.R;

import java.text.NumberFormat;
import java.util.ArrayList;


public class OrderFragment_shopping_cart extends Fragment {
    ArrayList<Item> mExampleList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String moneyString;
    private Double total;
    private Button btn_placeorder;
    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_shopping_cart, container, false);
        RelativeLayout rl = (RelativeLayout)v;
        buildRecyclerView(rl);
        btn_placeorder = v.findViewById(R.id.btn_placeorder);
        btn_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DeliveryActivity.class);
                Order order = (Order) getActivity();
                intent.putExtra("phone", order.GetRestaurant());
                intent.putExtra("price", total);
                startActivity(intent);
            }
        });

        return v;
    }
    public void buildRecyclerView(RelativeLayout rl) {
        mRecyclerView = rl.findViewById(R.id.recycler_cart);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        MyCart cart = MyCart.getInstance();
        mExampleList = cart.getCart();
        String total = getTotal(mExampleList);
        TextView tvTotal = rl.findViewById(R.id.tv_total);
        tvTotal.setText(total);
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



    }

    public String getTotal(ArrayList<Item> cart){
        total = 0.00;
        Double doublePrice = 0.00;
        for(int i = 0; i < cart.size(); i++){
            String price = cart.get(i).getText2();
            String newPrice = price.replace("$","");
            doublePrice = Double.parseDouble(newPrice);
            total += doublePrice;
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        moneyString = formatter.format(total);
        return moneyString;
    }
}