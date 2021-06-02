package de.morgroup.eventplaner.model;

public class EventItem {
    private String name;
    private String owner;
    private int thumbnailUrl;

    public EventItem(String name, String owner, int thumbnailUrl) {
        this.name = name;
        this.owner = owner;
        this.thumbnailUrl = thumbnailUrl;
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

    public int getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(int thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
