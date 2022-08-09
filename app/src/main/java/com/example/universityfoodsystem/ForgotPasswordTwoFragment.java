package com.example.universityfoodsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPasswordTwoFragment extends Fragment {

    private Spinner securityQuestion;
    private EditText securityAnswer;
    private Button prevButton;
    private Button nextButton;
    private String []userQuestions = new String[]{"What is you favorite car?",
            "What were your first pair of shoes?"};
    private String errorEmptyMessage = "This field can't be empty";
    private String errorFormatPassword = "Password must be at least 6 characters";
    private String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_two, container, false);
        securityQuestion = view.findViewById(R.id.security_question_f);
        securityAnswer = view.findViewById(R.id.security_answer_f);
        prevButton = view.findViewById(R.id.prev_button_f2);
        nextButton = view.findViewById(R.id.next_button_f2);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()!=null) {
            userQuestions[0] = getArguments().getString("question1");
            userQuestions[1] = getArguments().getString("question2");
            email = getArguments().getString("email").toString();
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.custom_spinner, userQuestions);
        securityQuestion.setAdapter(adapter);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer_f  = securityAnswer.getText().toString();
                if(validate(answer_f) ){
                    boolean isValid = checkSecurityAnswer(answer_f,securityQuestion);
                    if(isValid) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                                    fragmentTransaction.replace(R.id.fragment_container,LoginFragment.class,null)
                                            .commit();
                                    Toast toast = Toast.makeText(getContext(), "Please check your email and follow link to reset password", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP,0,0);
                                    toast.show();
                                }else{
                                    Toast toast = Toast.makeText(getContext(), "Error email could not be sent, enter a valid email", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP,0,0);
                                    toast.show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                                    fragmentTransaction.replace(R.id.fragment_container,ForgotPasswordFragment.class,null)
                                            .commit();

                                }
                            }
                        });
                    }else{
                        Toast toast = Toast.makeText(getContext(), "Incorrect answer,try again", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,0);
                        toast.show();
                    }
                }
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left_to_right, R.anim.slide_right_to_left);
                fragmentTransaction.replace(R.id.fragment_container,ForgotPasswordFragment.class,null)
                        .commit();
            }
        });



    }

    private boolean checkSecurityAnswer(String answer_f, Spinner securityQuestion) {
        String question1 = getArguments().getString("question1").toString().trim();
        String question2 = getArguments().getString("question2").toString().trim();
        String answer1 =getArguments().getString("answer1").toString().trim();
        String answer2 =getArguments().getString("answer2").toString().trim();
        boolean isCorrect = false;
        if(question1.equals(securityQuestion.getSelectedItem().toString().trim())){
            isCorrect = answer1.equals(answer_f);
        }else{
            isCorrect = answer2.equals(answer_f);
        }
        return isCorrect;
    }

    private boolean validate(String answer_f){
        if(answer_f.isEmpty()){
            securityAnswer.setError(errorEmptyMessage);
            securityAnswer.requestFocus();
            return false;
        }
        return true;
    }
}