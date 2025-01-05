package com.example.quiz.people;

public class People {
    String image, name, id, subscribes;

    public People(String image, String name, String id, String subscribes) {
        this.image = image;
        this.name = name;
        this.id = id;
        this.subscribes = subscribes;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscribes() {
        return subscribes;
    }

    public void setSubscribes(String subscribes) {
        this.subscribes = subscribes;
    }
}
