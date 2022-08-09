package com.example.universityfoodsystem.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.universityfoodsystem.R;


public class ChoosePaymentMethod extends Fragment {
    FragmentManager fragmentManager;
    private double price;
    private int delivery;

    public ChoosePaymentMethod() {
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
        View view = inflater.inflate(R.layout.fragment_choose_payment_method, container, false);
        Button buttonCreditCard = view.findViewById(R.id.buttonCreditCard);
        Button buttonMealPlan = view.findViewById(R.id.buttonMealPlan);
        fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = getArguments();
        if(bundle != null){
            price = bundle.getDouble("price");
            delivery = bundle.getInt("delivery", 0);
        }

        buttonCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CreditCardPayment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerViewPayments,fragment,null).addToBackStack(null)
                        .commit();
            }
        });
        buttonMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MealPlanPayment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerViewPayments,fragment,null).addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}