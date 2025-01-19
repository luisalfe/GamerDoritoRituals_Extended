package com.luis.gamerdoritorituals.model;

public class Game {
    private String title;
    private int imageResId;

    public Game(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
