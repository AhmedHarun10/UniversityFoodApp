package com.example.universityfoodsystem.UserInfo;

import android.app.Application;

public class User {
    String fullName;
    String email;
    String schoolId;
    String mUrl;
    SecurityQuestion securityQuestion;
    String password;

    private static User instance;

    public String getSchoolId() {
        return schoolId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    private User(){

    }
    public static User getInstance(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }
    public static void clearInstance(){
        if(instance != null){
            instance = null;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
}
