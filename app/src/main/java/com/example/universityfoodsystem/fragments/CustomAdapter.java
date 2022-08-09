package com.example.universityfoodsystem.fragments;

import android.content.ClipData;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.universityfoodsystem.ItemClickListener;
import com.example.universityfoodsystem.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>  {


    private Vector<String> items;
    private Vector<String> imageNames;
    private Vector<String> foodTypes;
    private Vector<String> addresses;
    private Vector<String> logos;
    public Context context;
    int x;

    public interface ItemClickListener {
        void onItemClick(String item);
    }
    private ItemClickListener clickListener;
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public CustomAdapter(Vector<String> items, Vector<String> imageNames, Vector<String> foodTypes, Vector<String> addresses, Vector<String> logos, ItemClickListener clickListener){
        this.clickListener = clickListener;
        this.items = items;
        this.imageNames = imageNames;
        this.foodTypes = foodTypes;
        this.addresses = addresses;
        this.logos = logos;
        x = 0;
    }
    public CustomAdapter(Vector<String> items, Vector<String> imageNames, Vector<String> foodTypes, Vector<String> addresses, ItemClickListener clickListener, int y){
        this.clickListener = clickListener;
        this.items = items;
        this.imageNames = imageNames;
        this.foodTypes = foodTypes;
        this.addresses = addresses;
        x = 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private final TextView foodTypeTextView;
        private final TextView addressTextView;
        private final ImageView imageViewLogo;
        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.restaurantNameTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewRestaurantImage);
            foodTypeTextView = (TextView) itemView.findViewById(R.id.foodTypeTextView);
            addressTextView = (TextView) itemView.findViewById(R.id.addressTextView);
            imageViewLogo = (ImageView) itemView.findViewById(R.id.imageViewLogo);
        }

        public TextView getTextView() {
            return textView;
        }
        public ImageView getImageView() {
            return imageView;
        }
        public TextView getFoodTypeTextView(){
            return foodTypeTextView;
        }
        public TextView getAddressTextView(){
            return addressTextView;
        }
        public ImageView getImageViewLogo() {
            return imageViewLogo;
        }

        public void bind (String item, ItemClickListener clickListener){
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    clickListener.onItemClick(item);
                }
            });
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (x == 1){
            viewHolder.getTextView().setWidth(20);
        }
        if(items.get(position).length() > 16){
            String newString = items.get(position).substring(0, 13) + "...";
            viewHolder.getTextView().setText(newString);
        }
        else{
            viewHolder.getTextView().setText(items.get(position));
        }
        viewHolder.getAddressTextView().setText(addresses.get(position));
        Random rnd = new Random(System.currentTimeMillis());
        double x = (rnd.nextFloat() + 1) * 2.5;
        DecimalFormat df = new DecimalFormat("#.#");
        String withFormat = df.format(x);
        if(withFormat.contains(".")){
            viewHolder.getFoodTypeTextView().setText(withFormat);
        }
        else{
            viewHolder.getFoodTypeTextView().setText(withFormat + ".0");
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String pathToImage = "restaurant_photos/" + imageNames.get(position);
        StorageReference storageRef = storage.getReference().child(pathToImage);
        Glide.with(viewHolder.itemView)
                .load(storageRef)
                .centerCrop()
                .into(viewHolder.getImageView());
        String pathToImage2 = "restaurant_logos/" + logos.get(position);
        Glide.with(viewHolder.itemView)
                .load(storage.getReference().child(pathToImage2))
                .centerCrop()
                .into(viewHolder.getImageViewLogo());
        viewHolder.bind(items.get(position), clickListener);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


}
