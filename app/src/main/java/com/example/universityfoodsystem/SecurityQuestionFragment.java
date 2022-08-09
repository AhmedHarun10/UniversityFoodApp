package com.example.universityfoodsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.universityfoodsystem.UserInfo.SecurityQuestion;
import com.example.universityfoodsystem.UserInfo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SecurityQuestionFragment extends Fragment {
    private Spinner securityQuestionOne;
    private EditText securityAnswerOne;
    private Spinner securityQuestionTwo;
    private EditText securityAnswerTwo;
    private Button prevButton;
    private Button doneButton;
    private String []userInfo;
    private String errorEmptyMessage = "This field can't be empty";
    private User user;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_security_question, container, false);
        securityQuestionOne = (Spinner)view.findViewById(R.id.security_question_1);
        securityQuestionTwo = view.findViewById(R.id.security_question_2);
        securityAnswerOne = view.findViewById(R.id.security_answer_1);
        securityAnswerTwo = view.findViewById(R.id.security_answer_2);
        prevButton = view.findViewById(R.id.prev_button_s);
        doneButton = view.findViewById(R.id.done_button_s);

        if(getArguments() != null){
            userInfo = getArguments().getStringArray("UserInfo");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.security_question_list1
                ,R.layout.custom_spinner);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.security_question_list2
                ,R.layout.custom_spinner);
        securityQuestionOne.setAdapter(adapter1);
        securityQuestionTwo.setAdapter(adapter2);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer1 = securityAnswerOne.getText().toString();
                String answer2 = securityAnswerTwo.getText().toString();
                if(validateAnswers(answer1,answer2)){
                    // perform authentication of the information
                    firebaseAuth = FirebaseAuth.getInstance();
                    /*
                    user = new User(userInfo[0],userInfo[1],
                            new SecurityQuestion(securityQuestionOne.getSelectedItem().toString(),answer1,
                                    securityQuestionTwo.getSelectedItem().toString(),answer2));
                     */
                    // fullname email , username, password, schoolid
                    user = User.getInstance();
                    user.setFullName(userInfo[0]);
                    user.setEmail(userInfo[1]);
                    user.setSchoolId(userInfo[4]);
                    user.setSecurityQuestion(new SecurityQuestion(securityQuestionOne.getSelectedItem().toString(),answer1,
                            securityQuestionTwo.getSelectedItem().toString(),answer2));
                    user.setPassword(userInfo[3]);
                    firebaseAuth.createUserWithEmailAndPassword(userInfo[1],userInfo[3])
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseDatabase database =  FirebaseDatabase.getInstance();
                                        DatabaseReference mRef =  database.getReference().child("Users").child(userInfo[4]);
                                        mRef.child("fullName").setValue(user.getFullName());
                                        mRef.child("security question").setValue(user.getSecurityQuestion());
                                        mRef.child("email").setValue(user.getEmail());
                                        mRef.child("school id").setValue(user.getSchoolId());
                                        mRef.child("mUrl").setValue("none");
                                        mRef.child("password").setValue(userInfo[3]);
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                                        fragmentTransaction.replace(R.id.fragment_container,LoginFragment.class,null)
                                                .commit();
                                        Toast toast = Toast.makeText(getContext(),"Registration Successful",
                                                Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP,0,0);
                                        toast.show();
                                    }
                                    else{
                                      Toast toast=   Toast.makeText(getContext(),
                                              "Registration unnSuccessful",
                                              Toast.LENGTH_SHORT);
                                      toast.setGravity(Gravity.TOP,0,0);
                                      toast.show();

                                    }
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
                fragmentTransaction.replace(R.id.fragment_container,RegistrationFragment.class,null)
                        .commit();
            }
        });

    }
    private boolean validateAnswers(String answer1,String answer2){
        if(securityAnswerOne.getText().toString().isEmpty()){
            securityAnswerOne.setError(errorEmptyMessage);
            securityAnswerOne.requestFocus();
            return false;
        }else if (securityAnswerTwo.getText().toString().isEmpty()){
            securityAnswerTwo.setError(errorEmptyMessage);
            securityAnswerTwo.requestFocus();
            return false;
        }
        return true;
    }
}