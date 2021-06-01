package de.morgroup.eventplaner.model;

public class EventItem {
    private String name;
    private String owner;
    private String thumbnailUrl;

    public EventItem(String name, String owner, String thumbnailUrl) {
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
