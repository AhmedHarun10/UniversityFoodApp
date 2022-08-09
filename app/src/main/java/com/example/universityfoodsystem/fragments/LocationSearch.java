package com.example.universityfoodsystem.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.universityfoodsystem.Order;
import com.example.universityfoodsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.universityfoodsystem.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;


public class LocationSearch extends Fragment {
    private String coords = new String();
    private Vector<String> items = new Vector<String>();
    private Vector<String> imageNames = new Vector<String>();
    private Vector<String> foodTypes = new Vector<String>();
    private Vector<String> logos = new Vector<String>();
    private String coordinates = new String();
    private Vector<Integer> distances = new Vector<Integer>();
    private Vector<String> coordsVector = new Vector<String>();
    private Vector<String> coordsVector2 = new Vector<String>();
    private Vector<String> distancesFormatted = new Vector<String>();
    private RecyclerView recyclerView;
    private CountDownLatch doneWithJSONRequests;
    private int length;
    private TextView textViewEnterSearchTerm;
    private ProgressBar loadingSearch;
    private EditText editText;
    private Button searchButton;
    private TextView textViewShowingResults;
    private Button buttonBackSearch;
    private HashMap<String, String> map = new HashMap<>();

    public LocationSearch() {
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
        View view = inflater.inflate(R.layout.fragment_location_search, container, false);
        textViewEnterSearchTerm = view.findViewById(R.id.textViewEnterSearchTerm3);
        buttonBackSearch = view.findViewById(R.id.buttonBackSearchLocation);
        editText = view.findViewById(R.id.editTextTextPostalAddress);
        searchButton = view.findViewById(R.id.buttonLocationSearch);
        loadingSearch = view.findViewById(R.id.locationSearchLoading);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView = view.findViewById(R.id.recyclerViewLocationSearch);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.INVISIBLE);
        textViewShowingResults = view.findViewById(R.id.textViewShowingResultsLocation);
        textViewShowingResults.setVisibility(View.INVISIBLE);
        loadingSearch.setVisibility(View.INVISIBLE);

        // Get the bundle from the SearchFragment. The bundle contains the number of restaurants in the DB
        Bundle bundle = this.getArguments();
        // Use the bundle and grab the length to start a CountDownLatch.
        if (bundle != null) {
            length = bundle.getInt("length", 0);
            editText.setText(bundle.getString("input", ""));
            if(!bundle.getString("input", "").matches("") ){
                PerformSearch();
            }
        }
        doneWithJSONRequests = new CountDownLatch(length);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformSearch();
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

    /* GetCoords() uses the Here API to retrieve the coordinates of the address that the user inputs.
    * It then accesses the firebase database for the information of all the restaurants in the database.
    * Then, it calls GetDistance() to get the distance from the user's input location to each restaurant.*/
    public void GetCoords(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest addressRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray jsonArr1 = response.getJSONArray("items");
                    JSONObject jsonObj1 = jsonArr1.getJSONObject(0);
                    JSONObject jsonAddress = jsonObj1.getJSONObject("address");
                    if(!jsonAddress.getString("countryCode").equals("USA")){
                        AddressFailure();
                    }
                    else {
                        JSONObject jsonObj2 = jsonObj1.getJSONObject("position");
                        String jsonLat = jsonObj2.getString("lat");
                        String jsonLong = jsonObj2.getString("lng");
                        coords = jsonLat + "," + jsonLong;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Restaurant2");
                        Query testQuery = ref.orderByKey().startAt("0");
                        testQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                                }
                                items.clear();
                                imageNames.clear();
                                foodTypes.clear();
                                distances.clear();
                                logos.clear();
                                for (DataSnapshot snapShot : snapshot.getChildren()) {
                                    String temp = snapShot.child("restaurantName").getValue().toString();
                                    items.add(temp);
                                    String tempFoodTypes = snapShot.child("restaurantGenre").getValue().toString();
                                    foodTypes.add(tempFoodTypes);
                                    if (snapShot.child("restaurantImage").getValue() != null) {
                                        String temp2 = snapShot.child("restaurantImage").getValue().toString();
                                        imageNames.add(temp2);
                                    }
                                    String tempLogo = snapShot.child("restaurantLogo").getValue().toString();
                                    logos.add(tempLogo);
                                    coordinates = snapShot.child("restaurantCoords").getValue().toString();
                                    coordsVector2.add(coordinates);
                                    String phone = snapShot.child("restaurantPhone").getValue().toString();
                                    map.put(temp, phone);

                                }
                                if (items.isEmpty()) {
                                    Toast.makeText(getActivity(), "No results found.", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < coordsVector2.size(); i++) {
                                        String url2 = "https://router.hereapi.com/v8/routes?transportMode=car&origin=" + coords + "&destination=" + coordsVector2.get(i) + "&return=summary&apikey=7hA6zccQAund4OEOpUtH8E6UiZ2J_eim5VIKIfHtw_g";
                                        GetDistance(url2, coordsVector2.get(i));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(getActivity(), "Please enter a search term first.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    }catch (JSONException e) {
                        e.printStackTrace();
                        AddressFailure();
                    }
                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AddressFailure();
            }
        });
        requestQueue.add(addressRequest);
    }

    /* GetDistance() takes a URL for a JSONRequest and the coordinates used in the request. It then gets the distance between the two coordinates.
    * from the JSONObject that is returned and adds it to the distances vector, as well as the coordinates used.*/
    public void GetDistance(String url, String coordsIn){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    JSONArray jsonArr1 = response.getJSONArray("routes");
                    JSONObject jsonArr2 = jsonArr1.getJSONObject(0);
                    JSONArray jsonArr3 = jsonArr2.getJSONArray("sections");
                    JSONObject jsonObj2 = jsonArr3.getJSONObject(0);
                    JSONObject jsonObj3 = jsonObj2.getJSONObject("summary");
                    String jsonLength = jsonObj3.getString("length");
                    distances.add(Integer.parseInt(jsonLength));
                    coordsVector.add(coordsIn);
                    try {
                        doneWithJSONRequests.countDown();
                    }
                    catch(Exception e){

                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    AddressFailure();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("response", "oof");
                AddressFailure();
            }
        });
        requestQueue.add(jsonRequest);
    }

    /* CleanUpAddress() takes the user's address input and cleans it up for URL usage.
    * Valid addresses can contain spaces or commas.*/
    public String CleanUpAddress(String address){
        char[] addressChars = address.toCharArray();
        char x;
        address = "";
        for(int i = 0; i < addressChars.length; i++){
            if(addressChars[i] == ' '){
                address += '+';
            }
            else if (addressChars[i] == ','){
                address += "%2C";
            }
            else{
                address += addressChars[i];
            }
        }

        return address;
    }

    /* Because of the asynchronous nature of the JSONRequests through volley, the distances in the distances
    * vector may not match up correctly to the restaurants. This method fixes that issue. */
    public void CheckVectors(){
        int i, j;
        int temp;
        String tempString1;

        for(i = 0; i < coordsVector.size(); i++){
            for(j = 0; j < coordsVector2.size(); j++){
                if(coordsVector2.get(j).equals(coordsVector.get(i))){
                    temp = distances.get(j);
                    tempString1 = coordsVector.get(j);
                    distances.set(j, distances.get(i));
                    coordsVector.set(j, coordsVector.get(i));
                    distances.set(i, temp);
                    coordsVector.set(i, tempString1);
                }
            }
        }
    }

    // Sort all the information based on the distances
    public void BubbleSort(){
        int i, j;
        int temp;
        String tempString1;
        String tempString2;
        String tempString3;
        String tempString4;

        for (i = 0; i < distances.size() - 1; i++){
            for(j = 0; j < distances.size() - i - 1; j++){
                if(distances.get(j) > distances.get(j+1)){
                    temp = distances.get(j);
                    tempString1 = items.get(j);
                    tempString2 = foodTypes.get(j);
                    tempString3 = imageNames.get(j);
                    tempString4 = logos.get(j);
                    distances.set(j, distances.get(j+1));
                    items.set(j, items.get(j+1));
                    foodTypes.set(j, foodTypes.get(j+1));
                    imageNames.set(j, imageNames.get(j+1));
                    logos.set(j, logos.get(j+1));
                    distances.set(j+1, temp);
                    items.set(j+1, tempString1);
                    foodTypes.set(j+1, tempString2);
                    imageNames.set(j+1, tempString3);
                    logos.set(j+1, tempString4);
                }

            }
        }
    }

    // Format the distances in the vector from meters to miles with 2 decimal points.
    public void FormatDistances(){
        int i;
        float x;
        for(i = 0; i < distances.size(); i++){
            x = (float) distances.get(i)/1609;
            DecimalFormat df = new DecimalFormat("#.##");
            String withFormat = df.format(x);
            distancesFormatted.add(withFormat + " mi");
        }
    }

    public void AddressFailure(){
        Toast.makeText(getActivity(), "Invalid address, try again.", Toast.LENGTH_SHORT).show();
        loadingSearch.setVisibility(View.INVISIBLE);
        Bundle bundle = new Bundle();
        Log.e("what is the ", String.valueOf(length));
        bundle.putInt("length", length);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = new LocationSearch();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView3,fragment,null)
                .commit();
    }

    public void PerformSearch(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }

        loadingSearch.setVisibility(View.VISIBLE);
        textViewEnterSearchTerm.setVisibility(View.INVISIBLE);
        String editTextString = editText.getText().toString();
        String url = "https://geocode.search.hereapi.com/v1/geocode?q=" + CleanUpAddress(editTextString) + "&apikey=7hA6zccQAund4OEOpUtH8E6UiZ2J_eim5VIKIfHtw_g";
        GetCoords(url);
        // We use a handler to start a new Thread to await our CountDownLatch
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Our CountDownLatch doneWithJSONRequests awaits the completion of all the JSONRequests done in the GetDistance() method.
                // Once we are done waiting, we can set our RecyclerView with all the restaurants in order of distance from the input location.
                try {
                    doneWithJSONRequests.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!distances.isEmpty()) {
                            try {
                                String temp = "Showing results for \"" + editText.getText().toString() + "\"";
                                textViewShowingResults.setVisibility(View.VISIBLE);
                                textViewShowingResults.setText(temp);
                                editText.setVisibility(View.INVISIBLE);
                                searchButton.setVisibility(View.INVISIBLE);
                                loadingSearch.setVisibility(View.INVISIBLE);
                                CheckVectors();
                                BubbleSort();
                                FormatDistances();
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(new CustomAdapter(items, imageNames, foodTypes, distancesFormatted, logos, new CustomAdapter.ItemClickListener() {
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

                            } catch (Exception e) {

                            }
                        }
                        else{
                            AddressFailure();
                        }
                    }
                });
            }
        }).start();
    }
}