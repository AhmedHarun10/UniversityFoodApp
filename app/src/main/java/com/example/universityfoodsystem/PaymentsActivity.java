package com.example.universityfoodsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.TextView;

import com.example.universityfoodsystem.fragments.ChoosePaymentMethod;
import com.example.universityfoodsystem.fragments.MealPlanPayment;

import java.text.NumberFormat;

public class PaymentsActivity extends AppCompatActivity {
    private String total;
    private String totalFees;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        phone = getIntent().getStringExtra("phone");
        Double price = getIntent().getExtras().getDouble("price");
        Double processingFee = price * 0.1;
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String subTotal = formatter.format(price);
        String processingFeeString = formatter.format(processingFee);
        if(getIntent().getIntExtra("delivery", 0) == 1){
            price = price + processingFee + 1.5;
        }
        else{
            price = price + processingFee;
        }
        String priceString = formatter.format(price);
        TextView textViewFees = findViewById(R.id.textViewPaymentFees);
        TextView textViewPaymentTotal = findViewById(R.id.textViewPaymentTotal);
        if(getIntent().getIntExtra("delivery", 0) == 1){
            total = "Subtotal:\nProcessing fee:\nDelivery Fee:\nTotal: ";
            totalFees = subTotal + "\n" + processingFeeString + "\n$1.50\n" + priceString;
        }
        else{
            total = "Subtotal:\nProcessing fee:\nTotal: ";
            totalFees = subTotal + "\n" + processingFeeString + "\n" + priceString;
        }
        textViewFees.setText(total);
        textViewPaymentTotal.setText(totalFees);
        Bundle bundle = new Bundle();
        bundle.putDouble("price", price);
        bundle.putInt("delivery", getIntent().getIntExtra("delivery", 0));
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new ChoosePaymentMethod();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewPayments,fragment,null).addToBackStack(null)
                .commit();
    }

    public String GetRestaurant(){
        return phone;
    }
}