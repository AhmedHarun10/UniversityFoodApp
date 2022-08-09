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
import android.widget.Toast;

import com.example.universityfoodsystem.OrderDetailsActivity;
import com.example.universityfoodsystem.OrdersAdapter;
import com.example.universityfoodsystem.R;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

public class OrdersListFragment extends Fragment {
    private User user;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Vector<String> orderRestaurants = new Vector<String>();
    private Vector<String> orderNumbers = new Vector<String>();
    private Vector<String> orderDates = new Vector<String>();
    private Vector<String> orderTotal = new Vector<String>();

    public OrdersListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        user = User.getInstance();
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference orderRef = firebaseDatabase.getReference().child("Orders").child(user.getSchoolId());
        Query listQuery = orderRef.orderByKey().startAfter("0");
        listQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapShot: snapshot.getChildren()){
                    String tempRestaurant = snapShot.child("orderRestaurant").getValue().toString();
                    orderRestaurants.add(tempRestaurant);
                    String tempDate = snapShot.child("orderDateAndTime").getValue().toString();
                    orderDates.add(tempDate);
                    String tempNumber = snapShot.getKey();
                    orderNumbers.add(tempNumber);
                    String tempTotal = snapShot.child("orderTotal").getValue().toString();
                    orderTotal.add(tempTotal);
                }
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new OrdersAdapter(orderRestaurants, orderNumbers, orderDates, orderTotal, new OrdersAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                        intent.putExtra("orderNumber", item);
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