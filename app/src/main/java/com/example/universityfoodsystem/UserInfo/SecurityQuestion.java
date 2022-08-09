package com.example.universityfoodsystem.UserInfo;

public class SecurityQuestion {
    String question1;
    String answer1;
    String Question2;
    String answer2;

    public SecurityQuestion(String question1, String answer1, String question2, String answer2) {
        this.question1 = question1;
        this.answer1 = answer1;
        Question2 = question2;
        this.answer2 = answer2;
    }

    public SecurityQuestion() {
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getQuestion2() {
        return Question2;
    }

    public void setQuestion2(String question2) {
        Question2 = question2;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }
}
