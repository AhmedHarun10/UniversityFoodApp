package com.example.universityfoodsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.universityfoodsystem.UserInfo.SecurityQuestion;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    private EditText email;
    private EditText password;
    private TextView createAccount;
    private TextView forgotPassword;
    private TextInputLayout passwordWrapper;
    private Button login_button;
    private String errorEmptyMessage = "This field can't be empty";
    private String errorFormatEmail = "Enter a valid email";
    private String errorFormatPassword = "Password must be at least 6 characters";
    private FirebaseAuth firebaseAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.email_l);
        password = view.findViewById(R.id.password);
        login_button = view.findViewById(R.id.login_button);
        createAccount = view.findViewById(R.id.create_account);
        forgotPassword = view.findViewById(R.id.forgot_password);
        passwordWrapper = view.findViewById(R.id.password_wrapper);
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createAccount.setOnClickListener(view12 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_container, RegistrationFragment.class, null)
                    .commit();

        });
        forgotPassword.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_container, ForgotPasswordFragment.class, null)
                    .commit();
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_s = email.getText().toString().trim();
                String password_s = password.getText().toString().trim();

                if (validateUserLogin(email_s,password_s)) {
                    firebaseAuth.signInWithEmailAndPassword(email_s,password_s)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        // if firebase successfully authenticates the user , we
                                        // can then load the home screen with their account logged in
                                        Toast toast = Toast.makeText(getActivity(), "Login successful",
                                                Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP,0,0);
                                        toast.show();
                                        User user = User.getInstance();

                                        SecurityQuestion question = new SecurityQuestion();
                                        FirebaseDatabase database =  FirebaseDatabase.getInstance();
                                        DatabaseReference mRef =  database.getReference().child("Users");
                                        mRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot d : snapshot.getChildren()){
                                                    if(d.exists()) {
                                                        if (d.child("email").getValue().toString().equals(email_s)) {
                                                            user.setFullName(d.child("fullName").getValue().toString());
                                                            user.setEmail(d.child("email").getValue().toString());
                                                            question.setQuestion1(d.child("security question")
                                                                    .child("question1").getValue().toString());
                                                            question.setQuestion2(d.child("security question")
                                                                    .child("question2").getValue().toString());
                                                            question.setAnswer1(d.child("security question")
                                                                    .child("answer1").getValue().toString());
                                                            question.setAnswer2(d.child("security question")
                                                                    .child("answer2").getValue().toString());
                                                            user.setSecurityQuestion(question);
                                                            user.setSchoolId(d.child("school id").getValue().toString());
                                                            user.setmUrl(d.child("mUrl").getValue().toString());
                                                        }
                                                    }
                                                }
                                                Intent intent = new Intent(getContext(),HomeScreen.class);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    } else {
                                        // else a toast is displayed showing the problem
                                        Toast toast = Toast.makeText(getActivity(), "Incorrect email or password, try again",
                                                Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP,0,0);
                                        toast.show();

                                    }
                                }
                            });
                }
            }
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
    private boolean validateUserLogin(String email_s,String password_s){
        if (email_s.isEmpty()) {
            email.setError(errorEmptyMessage);
            email.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email_s).matches()){
            email.setError(errorFormatEmail);
            email.requestFocus();
            return false;
        }else if (password_s.isEmpty()) {
            passwordWrapper.setPasswordVisibilityToggleEnabled(false);
            password.setError(errorEmptyMessage);
            password.requestFocus();
             return false;
        }else if(password_s.length() < 6){
            passwordWrapper.setPasswordVisibilityToggleEnabled(false);
            password.setError(errorFormatPassword);
            password.requestFocus();
             return false;
        }
        return true;
    }
}