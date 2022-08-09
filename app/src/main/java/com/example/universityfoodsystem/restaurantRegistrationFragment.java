package com.example.universityfoodsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.universityfoodsystem.restaurantInfo.restaurantHelperClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class restaurantRegistrationFragment extends Fragment{
    private FirebaseDatabase databaseStart;
    private DatabaseReference database;
    private EditText inputName;
    private EditText inputAddress;
    private EditText inputZipcode;
    private EditText inputState;
    private EditText inputPhone;
    private EditText inputGenre;
    private Button submitButton;
    private FragmentManager fragmentManager;

    public restaurantRegistrationFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment when the view is created
         View view = inflater.inflate(R.layout.restaurant_registration_fragment, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        inputName = view.findViewById(R.id.restaurantNameInput);
        inputAddress = view.findViewById(R.id.restaurantAddressInput);
        inputState = view.findViewById(R.id.restaurantStateInput);
        inputZipcode = view.findViewById(R.id.restaurantZipcodeInput);
        inputPhone = view.findViewById(R.id.restaurantPhoneInput);
        inputGenre = view.findViewById(R.id.restaurantGenreInput);
        submitButton = view.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() { // When submit is pressed, upload the data to firebase
            @Override
            public void onClick(View view){
                databaseStart = FirebaseDatabase.getInstance();
                database = databaseStart.getReference( "Restaurant2");

                String restaurantName = inputName.getText().toString();
                String restaurantAddress = inputAddress.getText().toString();
                String restaurantZip = inputZipcode.getText().toString();
                String restaurantPhone = inputPhone.getText().toString();
                String restaurantState = inputState.getText().toString();
                String restaurantGenre = inputGenre.getText().toString();
                restaurantHelperClass helperClass = new restaurantHelperClass(restaurantName,restaurantAddress,restaurantState,restaurantZip,restaurantPhone,restaurantGenre);

                database.child(restaurantPhone).setValue(helperClass);
                Intent intent = new Intent(getActivity(), ImageUploadRestaurant.class);
                intent.putExtra("phone", restaurantPhone);
                intent.putExtra("address", restaurantAddress + ", " + restaurantZip);
                startActivity(intent);
            }
                                        }

    );


    return view;
    }

}