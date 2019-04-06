package com.example.roman.targets;

public class Card {
    CardType type;
    int id;
    String title;
    String text;
    int pageid;

    //TODO: change constructor
    Card(int id, int pageid, String title, String text)
    {
        this.id = id;
        this.pageid = pageid;
        this.title = title;
        this.text = text;
    }
    Card(){

    }

    enum CardType
    {
        Note, List, Question, Deadline
    }
}
