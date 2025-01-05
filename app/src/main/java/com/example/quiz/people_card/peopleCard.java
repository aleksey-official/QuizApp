package com.example.quiz.people_card;

public class peopleCard {
    String image, name, nick, id, subscribes;

    public peopleCard(String image, String name, String nick, String id, String subscribes) {
        this.image = image;
        this.name = name;
        this.nick = nick;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
