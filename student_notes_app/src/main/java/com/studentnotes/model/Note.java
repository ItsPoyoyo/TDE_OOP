package com.studentnotes.model;

import java.sql.Timestamp;

/**
 * Note model class representing a note in the system
 */
public class Note {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String category;
    private boolean isFavorite;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Note() {
    }

    // Constructor with essential fields
    public Note(int userId, String title, String content, String category) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.isFavorite = false;
    }

    // Constructor with all fields except timestamps
    public Note(int userId, String title, String content, String category, boolean isFavorite) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.isFavorite = isFavorite;
    }

    // Constructor with all fields
    public Note(int id, int userId, String title, String content, String category, 
                boolean isFavorite, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.isFavorite = isFavorite;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", isFavorite=" + isFavorite +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
