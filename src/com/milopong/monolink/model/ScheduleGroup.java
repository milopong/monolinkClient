package com.milopong.monolink.model;

import java.util.ArrayList;

public class ScheduleGroup implements Cloneable {

    private String Name;
    private ArrayList<Schedule> Items;
    

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<Schedule> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Schedule> Items) {
        this.Items = Items;
    }
    

}