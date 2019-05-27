package com.example.roman.targets;

import java.util.ArrayList;
import java.util.Calendar;

public class Card {

    static final int TYPE_NOTE = 1;
    static final int TYPE_LIST = 2;
    static final int TYPE_DEADLINE = 3;
    static final int TYPE_QUESTION = 4;

    int type;
    int id;
    String title="";
    String text="";
    int pageid;
    boolean hasDivider = false;
    boolean divider = false;

    ArrayList<Integer> questions = new ArrayList<>();
    ArrayList<Integer> links = new ArrayList<>();
    ArrayList<Integer> points = new ArrayList<>();

    private Calendar start = Calendar.getInstance(), end = Calendar.getInstance();

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
    Card(int id, int pageid, String text, Calendar end)
    {
        this.id = id;
        this.pageid = pageid;
        type = TYPE_DEADLINE;
        this.text = text;
        start = Calendar.getInstance();
        this.end = end;
    }

    public String Start()
    {
        if (start == null) return "";
        return String.format("%s.%s, %s:%s", start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE));
    }
    public String End()
    {
        if (end == null) return "";
        return String.format("%s.%s, %s:%s", end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE));
    }

    public String getTitle(){
        return title;
    }
    public String getText(){
        return text;
    }

}
