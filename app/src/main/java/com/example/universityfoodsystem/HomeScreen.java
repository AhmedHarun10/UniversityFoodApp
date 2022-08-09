// Mohammad's working on this

package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.UserInfo.User;
import com.example.universityfoodsystem.fragments.HomeFragment;
import com.example.universityfoodsystem.fragments.OrdersListFragment;
import com.example.universityfoodsystem.fragments.SearchFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.Random;
import java.util.Vector;

public class HomeScreen extends AppCompatActivity {

    private EditText recipients;
    private EditText subject;
    private EditText body;
    private Button send;
    private Button cancel;
    private Dialog dialog;
    private int ad;
    StorageReference pathReference;
    ImageView bannerAd;
    public Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        /* Set the navigation host fragment to the one in the xml file, then get the controller from it.
        * According to google's documentation, trying to go directly to the navcontroller will result in
        * errors (which it did for me)*/
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView3);

        /* Set the bottom navigation view to the one in the xml file.*/
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNavView);
        FragmentManager fragmentManager = getSupportFragmentManager();


        /* Setup a listener for the navigation bar.*/
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                /* Switch statement for the navigation bar items. Refer to res>menu>menu_bottom_navigation.xml for the menu item information.*/
                switch(item.getItemId()){
                    case R.id.bottomNavHome:
                        Fragment fragmentHome = new HomeFragment();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainerView3,fragmentHome,null).addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.bottomNavSearch:
                        Fragment fragmentSearch = new SearchFragment();
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.fragmentContainerView3,fragmentSearch,null).addToBackStack(null)
                                .commit();
                        return true;
                    case R.id.bottomNavChat:
                        sendEmail();
                        return true;
                    case R.id.bottomNavUser:
                        View view = findViewById(R.id.bottomNavUser);
                        userClick(view);
                        return true;
                    case R.id.bottomNavOrders:
                        Fragment fragmentOrders = new OrdersListFragment();
                        FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                        fragmentTransaction3.replace(R.id.fragmentContainerView3,fragmentOrders,null).addToBackStack(null)
                                .commit();
                        return true;

                }
                return false;
            };




        });

        if (getIntent().getIntExtra("order", 0) == 1){
            Bundle bundle = new Bundle();
            bundle.putInt("order", 1);
            Fragment fragmentHome = new HomeFragment();
            fragmentHome.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView3,fragmentHome,null).addToBackStack(null)
                    .commit();
        }

        /* Getting banner ads from firebase storage and displaying them on the home screen.*/
        // Start by creating a storage reference to the banner ads folder.
        StorageReference storageRef = storage.getReference().child("banner_ads");

        // Create a vector to hold the URLS for each file in the banner ads folder.
        Vector <String> fileNames = new Vector<String>();

        // List all files
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item : listResult.getItems()){
                    // For each file, add the URL to the vector.
                    fileNames.add(item.toString());
                }
                // Create a new random seed.
                Random rnd = new Random(System.currentTimeMillis());
                // Set the path reference to a random one of the banner ads.
                ad = rnd.nextInt(fileNames.size());
                pathReference = storage.getReferenceFromUrl(fileNames.get(ad));
                bannerAd = findViewById(R.id.imageView2);
                // Using Glide, set the ImageView to the banner ad that was randomly selected.
                Log.e(pathReference.toString(), "test");
                Glide.with(getBaseContext())
                        .load(pathReference)
                        .into(bannerAd);
                // Set the path reference to a random one of the banner ads.
                h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ad == fileNames.size() - 1){
                            ad = 0;
                        }
                        else{
                            ad += 1;
                        }
                        pathReference = storage.getReferenceFromUrl(fileNames.get(ad));
                        bannerAd = findViewById(R.id.imageView2);

                        // Using Glide, set the ImageView to the banner ad that was randomly selected.
                        Glide.with(getBaseContext())
                                .load(pathReference)
                                .into(bannerAd);

                        h.postDelayed(this,30000);
                    }
                }, 30000);



            }
        });


    }

    private void userClick(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),view);
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Favorite Restaurants");
        popupMenu.getMenu().add("Add Restaurant");
        popupMenu.getMenu().add("Edit Menu Items");
        popupMenu.getMenu().add("Log out");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()){
                    case "Profile":
                        Intent intent = new Intent(getApplicationContext(),CreateProfileActivity.class);
                        h.removeCallbacksAndMessages(null);
                        startActivity(intent);
                        break;
                    case "Add Restaurant":
                        Intent intent1 = new Intent(getApplicationContext(), Restaurant_Registration_Zaky.class);
                        startActivity(intent1);
                        break;
                    case "Favorite Restaurants":
                        Intent intent2 = new Intent(getApplicationContext(),FavoriteListActivity.class);
                        startActivity(intent2);
                        Toast.makeText(getApplicationContext(),"FAVORITES",Toast.LENGTH_SHORT).show();
                        break;
                    case "Log out":
                        User.clearInstance();
                        Intent intent3 = new Intent(getApplicationContext(),LoginAndRegisteration.class);
                        startActivity(intent3);
                        break;
                    case "Edit Menu Items":
                        Intent intent4 = new Intent(getApplicationContext(), menuAdditionActivity.class);
                        startActivity(intent4);
                        break;
                    default:
                       Toast.makeText(getApplicationContext(),"DEFAULT",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }

    public void sendEmail(){

        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        h.removeCallbacksAndMessages(null);
        startActivity(intent);

    }


}