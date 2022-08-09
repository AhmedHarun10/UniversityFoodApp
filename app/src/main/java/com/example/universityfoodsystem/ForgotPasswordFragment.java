package com.example.universityfoodsystem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.universityfoodsystem.UserInfo.SecurityQuestion;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordFragment extends Fragment {

    private Spinner securityQuestion;
    private EditText securityAnswer;
    private EditText email;
    private EditText newPassword;
    private Button prevButton;
    private Button nextButton;
    private String []userQuestions = new String[]{"What is you favorite car?",
            "What were your first pair of shoes?"};
    private TextInputLayout textInputLayout;
    private EditText schoolId;

    private String errorEmptyMessage = "This field can't be empty";
    private String errorFormatEmail = "Enter a valid email";
    private String errorSchoolId = "School id must be exactly 10 characters";
    private SecurityQuestion securityQuestions;
    private Bundle bundle = new Bundle();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        email = view.findViewById(R.id.email_f);
        prevButton = view.findViewById(R.id.prev_button_f);
        nextButton = view.findViewById(R.id.next_button_f);
        schoolId = view.findViewById(R.id.school_id_f);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String question1;
                String email_f = email.getText().toString();
                String schoolId_f = schoolId.getText().toString();
                if(validate(schoolId_f,email_f)){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(schoolId_f);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                bundle.putString("question1", snapshot.child("security question")
                                        .child("question1").getValue().toString());
                                bundle.putString("answer1", snapshot.child("security question")
                                        .child("answer1").getValue().toString());
                                bundle.putString("question2", snapshot.child("security question")
                                        .child("question2").getValue().toString());
                                bundle.putString("answer2", snapshot.child("security question")
                                        .child("answer2").getValue().toString());
                                bundle.putString("email",email_f);
                                ForgotPasswordTwoFragment fragment = new ForgotPasswordTwoFragment();
                                fragment.setArguments(bundle);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                                fragmentTransaction.replace(R.id.fragment_container,fragment,null)
                                        .commit();
                            }else{
                                Toast toast = Toast.makeText(getContext(),"Account not found, enter a valid id",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,0);
                                toast.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }


                    });
                }
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                fragmentTransaction.replace(R.id.fragment_container,LoginFragment.class,null)
                        .commit();
            }
        });

    }
    private boolean validate(String schoolId_f,String email_f){
        if (schoolId_f.isEmpty()){
            schoolId.setError(errorEmptyMessage);
            schoolId.requestFocus();
            return false;
        }else if(schoolId_f.length() != 10){
            schoolId.setError(errorSchoolId);
            schoolId.requestFocus();
            return false;
        }else if (email_f.isEmpty()) {
            email.setError(errorEmptyMessage);
            email.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_f).matches()){
            email.setError(errorFormatEmail);
            email.requestFocus();
            return false;
        }
        return true;
    }
}