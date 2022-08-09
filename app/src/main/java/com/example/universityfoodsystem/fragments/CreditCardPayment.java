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
import android.widget.Toast;

import com.example.universityfoodsystem.HomeScreen;
import com.example.universityfoodsystem.OrderProcessedActivity;
import com.example.universityfoodsystem.PaymentsActivity;
import com.example.universityfoodsystem.R;

import java.time.LocalDate;

public class CreditCardPayment extends Fragment {
    private EditText editTextCreditCardNumber;
    private EditText editTextCVV;
    private EditText editTextExpMonth;
    private EditText editTextExpYear;
    private Button buttonSubmitCreditCard;


    public CreditCardPayment() {
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
        View view = inflater.inflate(R.layout.fragment_credit_card_payment, container, false);
        PaymentsActivity paymentsActivity = (PaymentsActivity) getActivity();
        String phone = paymentsActivity.GetRestaurant();
        Bundle bundle = getArguments();

        editTextCreditCardNumber = view.findViewById(R.id.editTextCreditCard);
        editTextCVV = view.findViewById(R.id.editTextCVV);
        editTextExpMonth = view.findViewById(R.id.editTextExpDateMonth);
        editTextExpYear = view.findViewById(R.id.editTextExpDateYear);
        buttonSubmitCreditCard = view.findViewById(R.id.buttonSubmitCreditCard);
        buttonSubmitCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidateCardInfo() == 1){
                    Intent intent = new Intent(getActivity(), OrderProcessedActivity.class);
                    intent.putExtra("price", bundle.getDouble("price"));
                    intent.putExtra("delivery", bundle.getInt("delivery"));
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    public int ValidateCardInfo(){
        if(editTextCreditCardNumber.getText().length() != 16){
            Toast.makeText(getActivity(), "Enter a valid credit card number.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(editTextCVV.getText().length() != 3 && editTextCVV.getText().length() != 4){
            Toast.makeText(getActivity(), "Enter a valid CVV.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(editTextExpMonth.getText().length() != 2 || Integer.parseInt(editTextExpMonth.getText().toString()) > 12 || Integer.parseInt(editTextExpMonth.getText().toString()) < 1){
            Toast.makeText(getActivity(), "Enter a valid expiration date.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(editTextExpYear.getText().length() != 2 || Integer.parseInt(editTextExpYear.getText().toString()) < 21 || Integer.parseInt(editTextExpYear.getText().toString()) > 41){
            Toast.makeText(getActivity(), "Enter a valid expiration date.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        if(Integer.parseInt(editTextExpYear.getText().toString()) == 21 && Integer.parseInt(editTextExpMonth.getText().toString()) < 12){
            Toast.makeText(getActivity(), "Enter a valid expiration date.", Toast.LENGTH_SHORT).show();
            return 0;
        }
        return 1;
    }
}