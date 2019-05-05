package com.example.roman.targets;

public class Card {
    CardType type;
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
    Card(){

    }

    enum CardType
    {
        Note, List, Question, Deadline
    }
    public String getType()
    {
        switch (type)
        {
            case Note:
                return "note";
            case List:
                return "list";
            case Deadline:
                return "deadline";
            case Question:
                return "question";
        }
        return "stupid compilator";
    }
}
