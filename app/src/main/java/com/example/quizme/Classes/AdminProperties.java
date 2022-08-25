package com.example.quizme.Classes;

public class AdminProperties {
    private String homeScreenText="";
    private int pointsPerAdd=0, pointsPerQuiz=0;
    private int index;

    public AdminProperties() {
    }

    public int getIndex() {return index;}

    public void setIndex(int index) {this.index = index;}

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
