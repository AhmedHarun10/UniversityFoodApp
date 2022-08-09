package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

public class AdvertisementUpload extends AppCompatActivity {
    Uri imagePath; // URI used to path to a selected image.
    ImageView image_preview; // Preview image.
    TextView previewTextView; // Text indicating preview image.
    FirebaseStorage storage = FirebaseStorage.getInstance(); // Set up firebase storage for ref later.
    // Constant width and height parameters to check image against:
    final int EXACT_IMAGE_WIDTH = 600;
    final int EXACT_IMAGE_HEIGHT = 125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_upload);
        // Set up our buttons.
        Button choose_button = findViewById(R.id.chooseButton);
        Button upload_button = findViewById(R.id.uploadButton);
        image_preview = findViewById(R.id.imagePreview);
        /* Set behavior for when the choose button is clicked.
        Expected behavior: open the gallery app or similar to search for images.*/
        choose_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FileChooser();
            }
        });
        // Set the behavior for when the upload button is clicked.
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUploader();
            }
        });
    }

    /* FileChooser() is called by the choose_button being clicked. It sets the intent to go open
    a "GET_CONTENT" application for images.*/
    private void FileChooser(){
        Intent fileChoiceIntent = new Intent();
        fileChoiceIntent.setType("image/*");
        fileChoiceIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(fileChoiceIntent, "Select an image"), 1);
    }

    /* FileUploader() is called by the upload_button being clicked. It uploads the file if the imagePath is
    not set to null. This only occurs if the file chosen was valid.*/
    private void FileUploader(){
        if (imagePath != null){
            /* Right now I have it set to randomly generate a name for the image. This is because the
            * image names were being difficult as the ones generated in the AVD contained colons which
            * threw Glide for a loop when it tried to fetch it.*/
            Random rnd = new Random(System.currentTimeMillis());
            StorageReference storageRef = storage.getReference().child("banner_ads/"+String.valueOf(rnd.nextInt(10000)));
            UploadTask uploadTask = storageRef.putFile(imagePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    CharSequence toastText = "File upload failed.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getBaseContext(), toastText, duration);
                    toast.show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    CharSequence toastText = "File successfully uploaded.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getBaseContext(), toastText, duration);
                    toast.show();
                    imagePath = null;
                    Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                    startActivity(intent);
                }
            });
        }
        // If the user attempts to upload with an image chosen, we send a toast informing them of their error.
        else{
            CharSequence toastText = "Choose a file first.";
            int duration = Toast.LENGTH_SHORT;
            Toast noFile = Toast.makeText(this, toastText, duration);
            noFile.show();
        }
    }
    /* Here we outline what happens on the activity changing, which happens after the intent changes
       in FileChooser().*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode 1 indicates result from FileChooser().
        if(resultCode == RESULT_OK && requestCode == 1){
            imagePath = data.getData();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            ContentResolver cr = getContentResolver();
            InputStream imageStream = null;
            // Using a try/catch in case we are unable to open the image stream.
            try {
                /* We open the image URI as a stream and decode it using bitmap factory to get the
                height and width of the image, to compare against the constants we set as the required
                image height and width. We set up the bitmapfactory options such that it does not fully
                load the image into memory, saving some space.*/
                imageStream = cr.openInputStream(imagePath);
                BitmapFactory.decodeStream(imageStream, null, options);
                int height = options.outHeight;
                int width = options.outWidth;

                // If the width and height exactly match, set the image_preview as the image path.
                if(height == EXACT_IMAGE_HEIGHT && width == EXACT_IMAGE_WIDTH) {
                    image_preview.setImageURI(imagePath);
                    previewTextView = findViewById(R.id.previewTextView);
                    previewTextView.setText("Preview Image:");
                }
                /* If the width and height are wrong, send an error toast as well as resetting the
                path and preview to null.*/
                else {
                    CharSequence toastText = "The file needs to be 600x100";
                    int duration = Toast.LENGTH_SHORT;
                    Toast wrongFile = Toast.makeText(this, toastText, duration);
                    wrongFile.show();
                    imagePath = null;
                    image_preview.setImageURI(null);
                    previewTextView.setText(null);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}