package com.example.universityfoodsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class menuAdditionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public menuAdditionFragment() {
        // Required empty public constructor
    }
    private FirebaseDatabase databaseStart;
    private DatabaseReference database_restaurant3;
    private EditText inputItemName;
    private EditText inputItemPrice;
    private Spinner inputRestaurantName;
    private String  inputPhoneNumber;
    private Button submitButton;
    private Button   exitButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_addition, container, false);

        databaseStart = FirebaseDatabase.getInstance();
        database_restaurant3 = databaseStart.getReference().child("Restaurant2");

        inputRestaurantName = view.findViewById(R.id.itemCategorySpinner);
        inputPhoneNumber = null;
        inputItemName = view.findViewById(R.id.itemName);
        inputItemPrice = view.findViewById(R.id.itemPrice);
        submitButton = view.findViewById(R.id.submitButton);
        exitButton = view.findViewById(R.id.exitButton);

        ArrayList<String> restaurantNameList = new ArrayList<>();
        ArrayList<String> restaurantPhoneNumber = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter<String>((Context) getActivity(),android.R.layout.simple_spinner_item, restaurantNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        database_restaurant3.addValueEventListener(new ValueEventListener() {  // Each time a new restaurant is added to the database, update the dropdown menu.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantNameList.clear();
                restaurantPhoneNumber.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    restaurantNameList.add(snapshot1.child("restaurantName").getValue().toString());
                    restaurantPhoneNumber.add(snapshot1.child("restaurantPhone").getValue().toString());
                }
                inputRestaurantName.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                inputRestaurantName.setAdapter(adapter);
            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        inputRestaurantName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inputPhoneNumber = null;
                String text = parent.getItemAtPosition(position).toString();
                inputPhoneNumber = restaurantPhoneNumber.get(position).toString();
                //Toast.makeText(getActivity(),phone,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DataSnapshot data= database_restaurant3.getChildren();
                //String restaurantNameSelected = inputRestaurantName.getSelectedItem().toString();
                try {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                } catch (Exception e) {

                }

                String itemNameInputed = inputItemName.getText().toString();
                String itemPriceInputed = inputItemPrice.getText().toString();

                //menuHelperClass helperClass = new menuHelperClass(itemNameInputed,itemPriceInputed);
                database_restaurant3.child(inputPhoneNumber).child("menu").child(itemNameInputed).setValue(itemPriceInputed);
                Toast.makeText(getActivity(), "Added " + itemNameInputed, Toast.LENGTH_SHORT);
                inputPhoneNumber = null;
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeScreen.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}