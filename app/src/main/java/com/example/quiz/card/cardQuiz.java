package com.example.quiz.card;

public class cardQuiz {
    String nameQuiz, typePublick, countQuestion, imageQuiz, userImage, idQuiz, play;

    public cardQuiz(String nameQuiz, String typePublick, String countQuestion, String imageQuiz, String userImage, String idQuiz, String play) {
        this.nameQuiz = nameQuiz;
        this.typePublick = typePublick;
        this.countQuestion = countQuestion;
        this.imageQuiz = imageQuiz;
        this.userImage = userImage;
        this.idQuiz = idQuiz;
        this.play = play;
    }

    public String getNameQuiz() {
        return nameQuiz;
    }

    public void setNameQuiz(String nameQuiz) {
        this.nameQuiz = nameQuiz;
    }

    public String getTypePublick() {
        return typePublick;
    }

    public void setTypePublick(String typePublick) {
        this.typePublick = typePublick;
    }

    public String getCountQuestion() {
        return countQuestion;
    }

    public void setCountQuestion(String countQuestion) {
        this.countQuestion = countQuestion;
    }

    public String getImageQuiz() {
        return imageQuiz;
    }

    public void setImageQuiz(String imageQuiz) {
        this.imageQuiz = imageQuiz;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(String idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }
}
