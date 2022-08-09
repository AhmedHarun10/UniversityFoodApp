package com.example.universityfoodsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;


public class RegistrationFragment extends Fragment {

    private EditText fullName;
    private EditText email;
    private EditText username;
    private EditText password;
    private EditText schoolId;
    private Button prevButton;
    private Button nextButton;
    private TextInputLayout passwordWrapper;
    private String errorEmptyMessage = "This field can't be empty";
    private String errorFormatEmail = "Enter a valid email";
    private String errorFormatPassword = "Password must be at least 6 characters";
    private String errorSchoolId = "School id must be exactly 10 characters";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        fullName = view.findViewById(R.id.full_name_field_r);
        email = view.findViewById(R.id.email_field_r);
        username = view.findViewById(R.id.username_field_r);
        schoolId = view.findViewById(R.id.school_id_r);
        password = view.findViewById(R.id.password_field_r);
        prevButton = view.findViewById(R.id.prev_button_r);
        nextButton = view.findViewById(R.id.next_button_r);
        passwordWrapper = view.findViewById(R.id.password_wrapper_r);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextButton.setOnClickListener(view1 -> {
            String errorMessage = "This field can't be empty";
            String fullName_r = fullName.getText().toString();
            String email_r = email.getText().toString();
            String username_r = username.getText().toString();
            String password_r = password.getText().toString();
            String schoolId_r = schoolId.getText().toString();

            if(validateUserLogin(fullName_r,email_r,username_r,password_r,schoolId_r)) {
                Bundle bundle = new Bundle();
                SecurityQuestionFragment securityQuestionFragment = new SecurityQuestionFragment();
                String []userInfo = {fullName_r,email_r,username_r,password_r,schoolId_r};
                bundle.putStringArray("UserInfo",userInfo);
                securityQuestionFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                    fragmentTransaction.replace(R.id.fragment_container, securityQuestionFragment, null)
                            .commit();

            }
        });
        prevButton.setOnClickListener(view12 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_down,R.anim.slide_up);
            fragmentTransaction.replace(R.id.fragment_container,LoginFragment.class,null)
                    .commit();
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordWrapper.setPasswordVisibilityToggleEnabled(true);
            }
        });

    }
    private boolean validateUserLogin(String fullName_r,String email_r,String username_r,String password_r,String schoolId_r){
        if(fullName_r.isEmpty()) {
            fullName.setError(errorEmptyMessage);
            fullName.requestFocus();
            return false;
        }else if (schoolId_r.isEmpty()){
            schoolId.setError(errorEmptyMessage);
            schoolId.requestFocus();
            return false;
        }else if(schoolId_r.length() != 10){
            schoolId.setError(errorSchoolId);
            schoolId.requestFocus();
            return false;
        }else if (email_r.isEmpty()) {
            email.setError(errorEmptyMessage);
            email.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_r).matches()){
            email.setError(errorFormatEmail);
            email.requestFocus();
            return false;
        }else if(username_r.isEmpty()){
            Toast.makeText(getContext(),"USERNAME",Toast.LENGTH_SHORT).show();
            username.setError(errorEmptyMessage);
            username.requestFocus();
            return false;
        }else if (password_r.isEmpty()) {
            passwordWrapper.setPasswordVisibilityToggleEnabled(false);
            password.setError(errorEmptyMessage);
            password.requestFocus();
            return false;
        }else if(password_r.length() < 6){
            passwordWrapper.setPasswordVisibilityToggleEnabled(false);
            password.setError(errorFormatPassword);
            password.requestFocus();
            return false;
        }

        return true;
    }
}