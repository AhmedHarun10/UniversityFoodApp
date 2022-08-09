package com.example.universityfoodsystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements OnNotificationListener {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MessageAdapter messageAdapter;
    ArrayList<Message> list;
    CardView cardView;

    private EditText recipients;
    private EditText subject;
    private EditText body;
    private Button send;
    private Button cancel;
    private Dialog dialog;
    private ImageView imageView;
    NotificationManager notificationManager;
    private boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.previous_page);
        databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        list = new ArrayList<>();
        messageAdapter = new MessageAdapter(getApplicationContext(),list,this);
        recyclerView.setAdapter(messageAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "App notifications";
            String description = "notify user";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeScreen.class);
                startActivity(intent);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {
                    for(DataSnapshot d : snapshot.getChildren()){
                        Message message = d.getValue(Message.class);
                        if(message != null & User.getInstance() != null &&
                            User.getInstance().getEmail().equals(message.getReceiver())){
                            list.add(message);
                            notifyUser("Notification from UFS");
                        }
                    }
                    messageAdapter.notifyDataSetChanged();


                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public void onNotificationClick(int position,String receiver,String url,String receivedMessage) {

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);
        recipients = dialog.findViewById(R.id.recipient);
        recipients.setText(receiver);
        subject = dialog.findViewById(R.id.title);
        body = dialog.findViewById(R.id.body);
        body.setHint(receiver+" said: "+receivedMessage);
        send = dialog.findViewById(R.id.send_email);
        cancel = dialog.findViewById(R.id.cancel_email);
        dialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef =databaseReference.child("Messages");
        String [] recipientList = receiver.split(",");
        send.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_SEND);
            intent1.setType("text/plain");
            intent1.putExtra(Intent.EXTRA_EMAIL, recipientList);
            intent1.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString().trim());
            intent1.putExtra(Intent.EXTRA_TEXT, body.getText().toString().trim());
            if (intent1.resolveActivity(view.getContext().getPackageManager()) != null) {
                Message message = new Message(User.getInstance().getEmail(),body.getText().toString(),
                        receiver,"url");
                FirebaseDatabase database =  FirebaseDatabase.getInstance();
                DatabaseReference mRef =  database.getReference().child("Messages");
                mRef.push().setValue(message);
                removeFromDatabase(mRef,receiver,receivedMessage);
                Toast toast = Toast.makeText(this, "Message sent",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,0);
                toast.show();
                dialog.dismiss();
            }

        });
        cancel.setOnClickListener(view -> dialog.dismiss());




    }

    private void removeFromDatabase(DatabaseReference mRef,String receiver,String receivedMessage) {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    if(d.child("receiver").getValue().toString()
                    .equals(User.getInstance().getEmail()) &&
                    d.child("sender").getValue().toString().equals(receiver)
                    &&d.child("message").getValue().toString().equals(receivedMessage)){
                        d.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean verifyReceiver(DatabaseReference mRef,String email){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d: snapshot.getChildren()){
                    if(d.child("email").getValue().toString()
                            .equals(email)){
                        isFound = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return isFound;
    }
    private void notifyUser(String stirng){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(stirng)
                .setContentText("You have a new message")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(0, builder.build());
    }

}