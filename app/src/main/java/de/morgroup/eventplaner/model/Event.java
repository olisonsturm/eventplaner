package de.morgroup.eventplaner.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Event implements Comparable<Event> {
    private String id;
    private String name;
    private String owner;
    private String time;
    private String description;
    private Timestamp day;
    private String thumbnailUrl;
    private ArrayList<String> member;
    private String joinLink;

    @Override
    public int compareTo(Event o) {
        return getDay().toDate().compareTo(o.getDay().toDate());
    }

    public Event() {
    }

    public Event(String id, String name, String owner, String time, String description, Timestamp day, String thumbnailUrl, ArrayList<String> member, String joinLink) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.time = time;
        this.description = description;
        this.day = day;
        this.thumbnailUrl = thumbnailUrl;
        this.member = member;
        this.joinLink = joinLink;
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

    public Timestamp getDay() {
        return day;
    }

    public void setDay(Timestamp day) {
        this.day = day;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJoinLink() {
        return joinLink;
    }

    public void setJoinLink(String joinLink) {
        this.joinLink = joinLink;
    }
}