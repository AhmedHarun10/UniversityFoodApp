package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageUploadRestaurant extends AppCompatActivity {
    private String phone;
    private String address;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri imagePath;
    private Uri logoPath;
    private Button imageButton;
    private Button logoButton;
    private Button submitButton;
    private String coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_restaurant);
        imageButton =  findViewById(R.id.buttonChooseRestaurantImage);
        logoButton = findViewById(R.id.buttonChooseRestaurantLogo);
        submitButton = findViewById(R.id.buttonChooseRestaurantImageSubmit);

        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");
        if(address != null){
            address = CleanUpAddress(address);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://geocode.search.hereapi.com/v1/geocode?q=" + address + "&apikey=7hA6zccQAund4OEOpUtH8E6UiZ2J_eim5VIKIfHtw_g";

            JsonObjectRequest addressRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArr1 = response.getJSONArray("items");
                        JSONObject jsonObj1 = jsonArr1.getJSONObject(0);
                        JSONObject jsonAddress = jsonObj1.getJSONObject("address");
                        JSONObject jsonObj2 = jsonObj1.getJSONObject("position");
                        String jsonLat = jsonObj2.getString("lat");
                        String jsonLong = jsonObj2.getString("lng");
                        coords = jsonLat + "," + jsonLong;

                    } catch (JSONException e) {
                        Log.e("coords error", "error");
                    }
                }}, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(addressRequest);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser(1);
            }
        });
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser(2);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePath != null && logoPath != null){
                    FileUploader();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("testaurant").child(phone);
                    ref.child("restaurantImage").setValue(phone);
                    ref.child("restaurantLogo").setValue(phone);
                    ref.child("restaurantCoords").setValue(coords);
                    Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show();
                    Intent newIntent = new Intent(getBaseContext(), AdvertisementUpload.class);
                    startActivity(newIntent);
                }
                else{
                    Toast.makeText(getBaseContext(), "Please select an image and a logo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FileChooser(int code) {
        Intent fileChoiceIntent = new Intent();
        fileChoiceIntent.setType("image/*");
        fileChoiceIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(fileChoiceIntent, "Select an image"), code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode 1 indicates result from FileChooser().
        if (resultCode == RESULT_OK && requestCode == 1) {
            imagePath = data.getData();
            Toast.makeText(this, imagePath.toString(), Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_OK && requestCode == 2){
            logoPath = data.getData();
            Toast.makeText(this, logoPath.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void FileUploader(){
        StorageReference storageRef = storage.getReference().child("restaurant_photos/" + phone);
        UploadTask uploadTask = storageRef.putFile(imagePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath = null;
                Log.e("good", "oh yes");
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("oops", "oh no2");
            }
        });

        StorageReference storageRef2 = storage.getReference().child("restaurant_logos/" + phone);
        UploadTask uploadTask2 = storageRef2.putFile(logoPath);
        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                logoPath = null;
                Log.e("good", "oh yes2");
            }
        });
        uploadTask2.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("oops", "oh no3");
            }
        });
    }

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
}