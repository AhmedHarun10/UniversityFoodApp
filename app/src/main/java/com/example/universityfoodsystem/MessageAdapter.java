package com.example.universityfoodsystem;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    Context context;
    ArrayList<Message> list;
    private OnNotificationListener onNotificationListener;
    public MessageAdapter(Context context, ArrayList<Message> list,OnNotificationListener listener) {
        this.context = context;
        this.list = list;
        onNotificationListener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.message,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       Message message = list.get(position);
       holder.message.setText(message.getMessage());
       holder.sender.setText(message.getSender());
       holder.imageView.setTag(message.getmUrl());
        Glide.with(context)
                .load(message.getmUrl())
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)

                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView sender;
        private TextView message;
        private TextView receiver;
        private CircleImageView imageView;
        private RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender_name);
            message = itemView.findViewById(R.id.message_r);
            imageView = itemView.findViewById(R.id.sender_image);
            relativeLayout = itemView.findViewById(R.id.card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNotificationListener.onNotificationClick(getAdapterPosition(),sender.getText().toString(),
                            imageView.getTag().toString(),message.getText().toString());
                }
            });
        }

    }
}
