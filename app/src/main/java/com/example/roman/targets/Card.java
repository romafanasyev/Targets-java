package com.example.roman.targets;

public class Card {
    static final int TYPE_NOTE = 1;
    static final int TYPE_LIST = 2;
    static final int TYPE_DEADLINE = 3;
    static final int TYPE_QUESTION = 4;

    int type;
    int id;
    String title;
    String text;
    int pageid;
    boolean isDivider = false;

    Card(int id, int pageid, String title, String text)
    {
        this.id = id;
        this.pageid = pageid;
        this.title = title;
        this.text = text;
    }
    Card(int id, int pageid, String title, String text, int type)
    {
        this.id = id;
        this.pageid = pageid;
        this.title = title;
        this.text = text;
        this.type = type;
    }
    Card(){

    }

}
