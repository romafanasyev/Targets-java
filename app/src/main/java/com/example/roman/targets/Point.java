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
        MainActivity.allPointsList.remove(indexOf(id));
        MainActivity.db.removePoint(this);
        //TODO: dependent => :destroy
    }
    private int indexOf(int id)
    {
        for (int i=0; i<MainActivity.allPointsList.size(); i++)
        {
            if (MainActivity.allPointsList.get(i).id == id)
                return i;
        }
        return -1;
    }
}
