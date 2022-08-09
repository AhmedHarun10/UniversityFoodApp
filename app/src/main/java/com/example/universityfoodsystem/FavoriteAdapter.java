package com.example.universityfoodsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Favorite> list;

    public FavoriteAdapter() {
    }

    public FavoriteAdapter(Context context, ArrayList<Favorite> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FavoriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.MyViewHolder holder, int position) {
        Favorite favorite = list.get(position);
        holder.restaurantName.setText(favorite.getRestaurantName());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String pathToImage = "restaurant_logos/" + favorite.imageUrl;
        StorageReference storageRef = storage.getReference().child(pathToImage);
        Glide.with(context)
                .load(storageRef)
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .into(holder.restaurantImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView restaurantImage;
        private TextView restaurantName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);



            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference mRef = databaseReference.child("Favorites List").child(User.getInstance().getSchoolId());
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Favorite favorite = dataSnapshot.getValue(Favorite.class);
                                if(favorite.restaurantName.equals(restaurantName.getText().toString())){
                                    Toast.makeText(view.getContext(),restaurantName.getText().toString()+" has been removed",Toast.LENGTH_SHORT).show();
                                    dataSnapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    return true;
                }
            });
        }
    }
}
