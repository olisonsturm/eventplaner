package de.morgroup.eventplaner.model;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private String owner;
    private String time;
    private String day;
    private String month;
    private String thumbnailUrl;
    private ArrayList<String> member;

    public Event() {
    }

    public Event(String id, String name, String owner, String time, String day, String month, String thumbnailUrl, ArrayList<String> member) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.time = time;
        this.day = day;
        this.month = month;
        this.thumbnailUrl = thumbnailUrl;
        this.member = member;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArrayList<String> getMember() {
        return member;
    }

    public void setMember(ArrayList<String> member) {
        this.member = member;
    }
}
