package com.example.universityfoodsystem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universityfoodsystem.UserInfo.User;
import com.example.universityfoodsystem.fragments.CustomAdapter;
import com.example.universityfoodsystem.restaurantInfo.restaurantHelperClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{

    private Vector<String> orderRestaurants = new Vector<String>();
    private Vector<String> orderNumbers = new Vector<String>();
    private Vector<String> orderDates = new Vector<String>();
    private Vector<String> orderTotals = new Vector<String>();

    public interface ItemClickListener {
        void onItemClick(String item);
    }
    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewRestaurants;
        private final TextView textViewTotals;
        private final TextView textViewDates;
        private final TextView textViewNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View
            textViewRestaurants = (TextView) itemView.findViewById(R.id.textViewOrdersRestaurant);
            textViewTotals = (TextView) itemView.findViewById(R.id.textViewOrdersTotal);
            textViewDates = (TextView) itemView.findViewById(R.id.textViewOrdersDate);
            textViewNumber = (TextView) itemView.findViewById(R.id.textViewOrdersNumber);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    addToFavorite(itemView);
                    return true;
                }
            });
        }

        private void addToFavorite(View view) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mRef1 = databaseReference.child("Favorites List");
            DatabaseReference mref2 = databaseReference.child("Restaurant2");
            mref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot d : snapshot.getChildren()){
                        if(d.child("restaurantName").getValue().toString().equals(textViewRestaurants.getText().toString())){
                            Favorite newFavorite = new Favorite(d.child("restaurantName").getValue().toString(),
                                    d.child("restaurantLogo").getValue().toString());
                            isNewRestaurant(view,newFavorite.restaurantName,mRef1,newFavorite);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        private void isNewRestaurant(View view,String restaurantName,DatabaseReference fRef,Favorite newFavorite) {
            DatabaseReference mref = fRef.child(User.getInstance().getSchoolId());
            mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isNew = true;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Favorite favorite = dataSnapshot.getValue(Favorite.class);
                        if(favorite.restaurantName.equals(restaurantName)){
                            isNew = false;
                        }
                    }
                    if(isNew){
                        mref.push().setValue(newFavorite);
                        Toast.makeText(view.getContext(),restaurantName+" has been successfully added to  favorites",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(view.getContext(),restaurantName+"is already in your favorites list",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        public TextView getTextViewRestaurants() {
            return textViewRestaurants;
        }
        public TextView getTextViewTotals(){
            return textViewTotals;
        }
        public TextView getTextViewDates(){
            return textViewDates;
        }
        public TextView getTextViewNumber(){
            return textViewNumber;
        }

        public void bind (String item, ItemClickListener clickListener){
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    clickListener.onItemClick(item);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.orders_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    public OrdersAdapter(Vector<String> orderRestaurants, Vector<String> orderNumbers, Vector<String> orderDates, Vector<String> orderTotals, ItemClickListener clickListener) {
        this.orderRestaurants = orderRestaurants;
        this.clickListener = clickListener;
        this.orderNumbers = orderNumbers;
        this.orderDates = orderDates;
        this.orderTotals = orderTotals;
    }

    @Override
    public void onBindViewHolder (ViewHolder viewHolder, final int position)  {
        viewHolder.getTextViewRestaurants().setText(orderRestaurants.get(position));
        viewHolder.getTextViewTotals().setText(orderTotals.get(position));
        viewHolder.getTextViewDates().setText(orderDates.get(position));
        int orderNumber = 10000 - Integer.parseInt(orderNumbers.get(position));
        String orderNumberString = "Order no. " + String.valueOf(orderNumber);
        viewHolder.getTextViewNumber().setText(orderNumberString);
        viewHolder.bind(orderNumbers.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return orderRestaurants.size();
    }

}
