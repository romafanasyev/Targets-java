package com.example.roman.targets;

public class Point {
    int id;
    String text = "";
    boolean checked = false;

    Point()
    {
        MainActivity.allPointsList.add(this);
        id = MainActivity.allPointsList.size();
        MainActivity.db.addPoint(this);
    }
    void save()
    {
        MainActivity.db.editPoint(this);
    }
    void delete()
    {
        MainActivity.allPointsList.remove(id);
        MainActivity.db.removePoint(this);
        //TODO: dependent => :destroy
    }
}
