package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {
    private CircleImageView imageView;
    private Button takeImage;
    private Button uploadImage;
    private Button save;
    private EditText fullName;
    private EditText utaId;
    private EditText email;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        imageView = findViewById(R.id.profile_image);
        takeImage = findViewById(R.id.take_image);
        uploadImage = findViewById(R.id.upload_image);
        fullName = findViewById(R.id.full_name_field_c);
        utaId = findViewById(R.id.school_id_c);
        email = findViewById(R.id.email_field_c);
        save = findViewById(R.id.save_profile_button);
        back = findViewById(R.id.back);
        User user=User.getInstance();
        if(user!= null){
            fullName.setText(user.getFullName());
            utaId.setText(user.getSchoolId());
            email.setText(user.getEmail());
            if(!user.getmUrl().equals("none")){
                Picasso.get()
                        .load(user.getmUrl())
                        .into(imageView);
            }
        }

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent choosePhoto = new Intent(Intent.ACTION_PICK);
                choosePhoto.setType("image/*");
                startActivityForResult(choosePhoto, 0);
            }
        });
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(takePhoto, 1);
                } catch (ActivityNotFoundException e) {
                    e.getStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference mRef = databaseReference.child("Users");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot d : snapshot.getChildren()){
                            if(d.child("email").getValue().toString().equals(user.getEmail())){
                                d.child("fullName").getRef().setValue(fullName.getText().toString());
                                d.child("school id").getRef().setValue(utaId.getText().toString());
                                d.child("email").getRef().setValue(email.getText().toString());
                                d.child("mUrl").getRef().setValue(user.getmUrl());
                            }
                        }
                        Toast toast = Toast.makeText(view.getContext(), "Profile saved",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,0);
                        toast.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeScreen.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try{
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);
                        // compress image to png with no compression because of 100
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100,byteArrayOutputStream);
                        //save to local device
                        String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), bitmap,
                                "null", null);
                        // save image to user and load it to the page
                        imageView.setTag(path);
                        User.getInstance().setmUrl(path);
                        Picasso.get()
                                .load(path)
                                .into(imageView);
                    }
                    catch (Exception e){
                        e.getStackTrace();
                    }
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    // get image taking by camera
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // same as above for saving the image
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), imageBitmap,
                            "Image", null);
                    imageView.setImageBitmap(imageBitmap);
                    User.getInstance().setmUrl(path);

                }

        }
    }

}