package com.example.universityfoodsystem.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.universityfoodsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;


public class SearchFragment extends Fragment {

    public int length;
    private String mParam1;
    private String mParam2;
    private Vector<String> autoComplete = new Vector<String>();
    private Vector<Integer> typeOfAutoComplete = new Vector<Integer>();
    private FragmentManager fragmentManager;

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();


        Button locationSearchButton = view.findViewById(R.id.locationSearchFragmentButton);
        Button foodSearchButton = view.findViewById(R.id.foodTypeSearchFragmentButton);
        Button restaurantSearchButton = view.findViewById(R.id.restaurantSearchFragmentButton);
        Spinner spinner = view.findViewById(R.id.spinnerSearchFragment);
        AutoCompleteTextView editText = view.findViewById(R.id.editTextSearchFragment);
        String[] searches = {"Choose...", "Location", "Food Type", "Restaurant"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, searches);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Restaurant2");
        Query lengthQuery = ref.orderByKey().startAt("0");
        lengthQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                length = 0;
                for(DataSnapshot snapShot: snapshot.getChildren()){
                    length += 1;
                    String temp = snapShot.child("restaurantGenre").getValue().toString();
                    if (!autoComplete.contains(temp)){
                        autoComplete.add(temp);
                        typeOfAutoComplete.add(0);
                    }
                    String temp2 = snapShot.child("restaurantName").getValue().toString();
                    if(!autoComplete.contains(temp2)){
                        autoComplete.add(temp2);
                        typeOfAutoComplete.add(1);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, autoComplete);
        editText.setThreshold(3);
        editText.setAdapter(arrayAdapter);
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(typeOfAutoComplete.get(autoComplete.indexOf(selectedItem)) == 0){
                    if(!editText.getText().toString().matches("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("input", editText.getText().toString());
                        TransactFragment(new FoodSearch(), bundle);
                    }
                    else{
                        TransactFragment(new FoodSearch(), null);
                    }
                }
                else{
                    if(!editText.getText().toString().matches("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("input", editText.getText().toString());
                        TransactFragment(new RestaurantNameSearch(), bundle);
                    }
                    else{
                        TransactFragment(new RestaurantNameSearch(), null);
                    }
                }
            }
        });

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Bundle bundle = new Bundle();
                    bundle.putInt("length", length);
                    if(!editText.getText().toString().matches("")){
                        bundle.putString("input", editText.getText().toString());
                    }
                    TransactFragment(new LocationSearch(), bundle);

                }
                else if(position == 2){
                    if(!editText.getText().toString().matches("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("input", editText.getText().toString());
                        TransactFragment(new FoodSearch(), bundle);
                    }
                    else{
                        TransactFragment(new FoodSearch(), null);
                    }
                }
                else if (position == 3){
                    if(!editText.getText().toString().matches("")){
                        Bundle bundle = new Bundle();
                        bundle.putString("input", editText.getText().toString());
                        TransactFragment(new RestaurantNameSearch(), bundle);
                    }
                    else{
                        TransactFragment(new RestaurantNameSearch(), null);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("length", length);
                TransactFragment(new LocationSearch(), bundle);
            }
        });
        foodSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactFragment(new FoodSearch(), null);
            }
        });
        restaurantSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactFragment(new RestaurantNameSearch(), null);
            }
        });

        return view;
    }

    public void TransactFragment(Fragment fragment, Bundle bundle){
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView3,fragment,null).addToBackStack(null)
                .commit();

    }
}