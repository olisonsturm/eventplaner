package de.morgroup.eventplaner.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class Voting /*implements Comparable<Voting>*/ {
    private String id;
    private String name;
    private Timestamp endTime;
    private String description;
    private ArrayList<String> options;
    private Map<String, ArrayList<String>> votes;

/*    @Override
    public int compareTo(Voting o) {
        return getEndTime().toDate().compareTo(o.getEndTime().toDate());
    }*/

    public Voting() {
    }

    public Voting(String id, String name, String owner, Timestamp endTime, Timestamp day, String thumbnailUrl, ArrayList<String> options, Map<String, ArrayList<String>> votes) {
        this.id = id;
        this.name = name;
        this.endTime = endTime;
        this.options = options;
        this.votes = votes;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, ArrayList<String>> getVotes() {
        return votes;
    }

    public void setVotes(Map<String, ArrayList<String>> votes) {
        this.votes = votes;
    }

}