package com.example.quiz.quiz_preview;

public class preview {
    String image, type, question;

    public preview(String image, String type, String question) {
        this.image = image;
        this.type = type;
        this.question = question;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
