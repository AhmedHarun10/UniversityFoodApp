package com.example.universityfoodsystem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityfoodsystem.OrderProcessedActivity;
import com.example.universityfoodsystem.PaymentsActivity;
import com.example.universityfoodsystem.R;
import com.example.universityfoodsystem.UserInfo.User;

public class MealPlanPayment extends Fragment {


    public MealPlanPayment() {
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
        View view = inflater.inflate(R.layout.fragment_meal_plan_payment, container, false);
        EditText editText = view.findViewById(R.id.editTextMealPlanId);
        PaymentsActivity paymentsActivity = (PaymentsActivity) getActivity();
        String phone = paymentsActivity.GetRestaurant();
        Button buttonSubmit = view.findViewById(R.id.buttonSubmitMealPlan);
        User user = User.getInstance();
        TextView textViewID = view.findViewById(R.id.textViewSchoolID);
        String IDText = "Your School ID: " + user.getSchoolId();
        textViewID.setText(IDText);
        Bundle bundle = getArguments();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().length() == 5){
                    Intent intent = new Intent(getActivity(), OrderProcessedActivity.class);
                    intent.putExtra("price", bundle.getDouble("price"));
                    intent.putExtra("delivery", bundle.getInt("delivery"));
                    intent.putExtra("phone", phone);
                    Toast.makeText(getActivity(), "Your order has been successfully processed!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(), "Please enter a meal plan ID of length 5.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}