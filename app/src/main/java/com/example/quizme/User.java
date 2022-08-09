package com.example.quizme;

public class User {
    private String name, email, pass,profile, referCode, phone;
    private  int rewardPoints=0, quizPoints=0, totalPoints=0;
    private int index=0;
    private int adminRole=0;
    private  int correctAnswers=0;
    //reward history
    //play history
    //withdraw history
    //quiz history

    public User() {
    }

    public User(String fullN, String email, String phone, String pass) {
        this.name = fullN;

        this.email = email;
        this.pass = pass;
        this.phone = phone;
    }
    public void setIndex(int i) {this.index =i;}

    public int getIndex(){return index;}

    public void setCorrectAnswer(int c){this.correctAnswers=c;}

    public int getCorrectAnswer(){return correctAnswers;}

    public int getAdminRole() {
        return adminRole;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setPhone(String phone) {this.phone = phone;}

    public void setQuizPoints(int quizPoints) {
        this.quizPoints = quizPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getPhone() {
        return phone;
    }

    public int getRewardPoints(){
        return rewardPoints;
    }

    public int getQuizPoints() {
        return quizPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }





    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
