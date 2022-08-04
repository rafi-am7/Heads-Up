package com.example.quizme.Classes;

public class Data {
    private String homeScreenText="";
    private int pointsPerAdd=0, pointsPerQuiz=0;

    public Data() {
    }

    public String getHomeScreenText() {
        return homeScreenText;
    }

    public void setHomeScreenText(String homeScreenText) {
        this.homeScreenText = homeScreenText;
    }

    public int getPointsPerAdd() {
        return pointsPerAdd;
    }

    public void setPointsPerAdd(int pointsPerAdd) {
        this.pointsPerAdd = pointsPerAdd;
    }

    public int getPointsPerQuiz() {
        return pointsPerQuiz;
    }

    public void setPointsPerQuiz(int pointsPerQuiz) {
        this.pointsPerQuiz = pointsPerQuiz;
    }
}
