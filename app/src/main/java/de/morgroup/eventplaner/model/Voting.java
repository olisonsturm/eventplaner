package de.morgroup.eventplaner.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class Voting implements Comparable<Voting> {
    private String id;
    private String name;
    private Timestamp endTime;
    private ArrayList<String> options;

    @Override
    public int compareTo(Voting o) {
        return getEndTime().toDate().compareTo(o.getEndTime().toDate());
    }

    public Voting() {
    }

    public Voting(String id, String name, Timestamp endTime, ArrayList<String> options) {
        this.id = id;
        this.name = name;
        this.endTime = endTime;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

}