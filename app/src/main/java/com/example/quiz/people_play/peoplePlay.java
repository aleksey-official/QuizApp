package com.example.quiz.people_play;

public class peoplePlay {
    String image, name, score, id;

    public peoplePlay(String image, String name, String score, String id) {
        this.image = image;
        this.name = name;
        this.score = score;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
