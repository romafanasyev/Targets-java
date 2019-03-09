package com.example.roman.targets;

public class Card {
    int id;
    String title;
    String text;
    int pageid;

    Card(int id, int pageid, String title, String text)
    {
        this.id = id;
        this.pageid = pageid;
        this.title = title;
        this.text = text;
    }
    Card(){

    }

}
