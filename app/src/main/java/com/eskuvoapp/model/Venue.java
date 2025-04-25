package com.eskuvoapp.model;

public class Venue {
    private String id;
    private String name;
    private String location;
    private int capacity;

    public Venue() {
    }

    public Venue(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
