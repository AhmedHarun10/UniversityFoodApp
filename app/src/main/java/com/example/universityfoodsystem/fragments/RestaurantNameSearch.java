package com.example.universityfoodsystem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityfoodsystem.Order;
import com.example.universityfoodsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class RestaurantNameSearch extends Fragment {
    private Vector<String> items = new Vector<String>();
    private Vector<String> imageNames = new Vector<String>();
    private Vector<String> foodTypes = new Vector<String>();
    private Vector<String> addresses = new Vector<String>();
    private Vector<String> allRestaurants = new Vector<String>();
    private Vector<String> logos = new Vector<String>();
    private TextView textViewEnterSearchTerm;
    private DatabaseReference ref;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textViewShowingResults;
    private Button buttonBackSearch;
    private HashMap<String, String> map = new HashMap<>();

    public RestaurantNameSearch() {
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
        View view = inflater.inflate(R.layout.fragment_restaurant_name_search, container, false);
        textViewEnterSearchTerm = view.findViewById(R.id.textViewEnterSearchTerm1);
        textViewShowingResults = view.findViewById(R.id.textViewShowingResultsRestaurant);
        ref = FirebaseDatabase.getInstance().getReference().child("Restaurant2");
        buttonBackSearch = view.findViewById(R.id.buttonBackSearchRestaurant);
        Query initialQuery = ref.orderByKey().startAt("0");
        initialQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapShot: snapshot.getChildren()){
                    String temp = snapShot.child("restaurantName").getValue().toString();
                    allRestaurants.add(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button searchButton = view.findViewById(R.id.buttonRestaurantSearch);
        autoCompleteTextView = view.findViewById(R.id.editTextRestaurantName);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            autoCompleteTextView.setText(bundle.getString("input"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, allRestaurants);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PerformSearch();
            }
        });
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recyclerViewRestaurantSearch);
        if(bundle != null){
            PerformSearch();
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformSearch();
            }
        });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.getText().clear();
            }
        });
        buttonBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = new SearchFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView3,fragment,null)
                        .commit();
            }
        });

        return view;
    }
    public void PerformSearch(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }

        Query testQuery = ref.orderByKey().startAt("0");
        testQuery.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                }
                items.clear();
                imageNames.clear();
                addresses.clear();
                foodTypes.clear();
                logos.clear();
                for(DataSnapshot snapShot: snapshot.getChildren()){
                    if(snapShot.child("restaurantName").getValue().equals(autoCompleteTextView.getText().toString())) {
                        String temp = snapShot.child("restaurantName").getValue().toString();
                        items.add(temp);
                        String tempFoodTypes = snapShot.child("restaurantGenre").getValue().toString();
                        foodTypes.add(tempFoodTypes);
                        String tempAddress = snapShot.child("restaurantAddress").getValue().toString();
                        tempAddress += ", " + snapShot.child("restaurantZipcode").getValue().toString();
                        addresses.add(tempAddress);
                        if(snapShot.child("restaurantImage").getValue().toString() != null) {
                            String temp2 = snapShot.child("restaurantImage").getValue().toString();
                            imageNames.add(temp2);
                        }
                        String tempLogo = snapShot.child("restaurantLogo").getValue().toString();
                        logos.add(tempLogo);
                        String phone = snapShot.child("restaurantPhone").getValue().toString();
                        map.put(temp, phone);
                    }


                }
                if(items.isEmpty()){
                    Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                    textViewShowingResults.setText("");
                }
                else{
                    textViewEnterSearchTerm.setVisibility(View.INVISIBLE);
                    String temp = "Showing results for \"" + autoCompleteTextView.getText().toString() + "\"";
                    textViewShowingResults.setText(temp);
                }

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new CustomAdapter(items, imageNames, foodTypes, addresses, logos, new CustomAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        Toast.makeText(getActivity(), item, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), Order.class);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("restaurant", item);
                        bundle2.putString(item, map.get(item));
                        intent.putExtras(bundle2);
                        startActivity(intent);
                    }
                }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Please enter a search term first.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}