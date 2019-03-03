package com.example.roman.targets;

import java.util.ArrayList;

public class Page {

    int id;
    String title;
    boolean category = false;
    String category_name = "";
    boolean section = true;
    ArrayList<Integer> cards = new ArrayList<>();

    Page(int id, String title, boolean section)
    {
        this.id = id;
        this.title = title;
        this.section = section;
    }

    Page() {

    }

    public void setCategory(String string) {
        category = string.equals("1");
    }

    public void setCategoryName(String string) {
        category_name = string;
    }

    public void setTitle(String string) {
        title = string;
    }

    public String getCategory() {
        return category ? "1" : "0";
    }

    public String getCategoryName() {
        return category_name;
    }

    public String getTitle() {
        return title;
    }
}
