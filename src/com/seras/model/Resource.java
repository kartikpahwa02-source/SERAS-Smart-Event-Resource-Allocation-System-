package com.seras.model;

public class Resource {

    private int id;
    private String name;
    private String type; // Hall, Lab, Equipment

    public Resource(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}