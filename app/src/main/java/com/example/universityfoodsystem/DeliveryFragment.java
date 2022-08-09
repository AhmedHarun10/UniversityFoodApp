package com.example.universityfoodsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeliveryFragment extends Fragment {
    private EditText deliveryAddress;
    private EditText deliveryInstruction;
    private Button submitButton;
    Double price;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_delivery, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            price = bundle.getDouble("price");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deliveryAddress = view.findViewById(R.id.postalAddress);
        deliveryInstruction = view.findViewById(R.id.delivery_instruction);
        submitButton = view.findViewById(R.id.submit_button_delivery);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!deliveryAddress.getText().toString().matches("")){
                    DeliveryActivity deliveryActivity = (DeliveryActivity) getActivity();
                    Intent intent = new Intent(getActivity(), PaymentsActivity.class);
                    intent.putExtra("price", price);
                    intent.putExtra("delivery", 1);
                    intent.putExtra("phone", deliveryActivity.GetRestaurant());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(),"Enter an address first.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}