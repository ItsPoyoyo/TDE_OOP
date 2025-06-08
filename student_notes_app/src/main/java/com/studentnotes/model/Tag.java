package com.studentnotes.model;

/**
 * Tag model class representing a tag in the system
 */
public class Tag {
    private int id;
    private String name;

    // Default constructor
    public Tag() {
    }

    // Constructor with name
    public Tag(String name) {
        this.name = name;
    }

    // Constructor with all fields
    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
