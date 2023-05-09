package com.example.kyselyapp;

public class Question {
    private int id;
    private String text;
    private boolean expanded;

    public Question(int id, String text) {
        this.id = id;
        this.text = text;
        this.expanded = false;
    }

    public int getId() {
        return id;
    }
    public String getSId() {
        return ""+id;
    }

    public String getText() {
        return text;
    }
}

