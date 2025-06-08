package com.studentnotes.dao;

import com.studentnotes.model.Tag;
import com.studentnotes.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Tag entity
 */
public class TagDAO {
    
    /**
     * Create a new tag
     * 
     * @param tag Tag object to be created
     * @return true if successful, false otherwise
     */
    public boolean createTag(Tag tag) {
        String sql = "INSERT INTO tags (name) VALUES (?) ON DUPLICATE KEY UPDATE name = name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tag.getName());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tag.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            } else {
                // If no rows affected (tag already exists), get the existing tag ID
                return getTagByName(tag.getName()) != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get tag by ID
     * 
     * @param id Tag ID
     * @return Tag object if found, null otherwise
     */
    public Tag getTagById(int id) {
        String sql = "SELECT * FROM tags WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTagFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get tag by name
     * 
     * @param name Tag name
     * @return Tag object if found, null otherwise
     */
    public Tag getTagByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTagFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all tags
     * 
     * @return List of all tags
     */
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags ORDER BY name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tags.add(extractTagFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tags;
    }
    
    /**
     * Get tags for a note
     * 
     * @param noteId Note ID
     * @return List of tags for the note
     */
    public List<Tag> getTagsForNote(int noteId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t JOIN note_tags nt ON t.id = nt.tag_id WHERE nt.note_id = ? ORDER BY t.name";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(extractTagFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tags;
    }
    
    /**
     * Add tag to note
     * 
     * @param noteId Note ID
     * @param tagId Tag ID
     * @return true if successful, false otherwise
     */
    public boolean addTagToNote(int noteId, int tagId) {
        String sql = "INSERT INTO note_tags (note_id, tag_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE note_id = note_id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            stmt.setInt(2, tagId);
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Remove tag from note
     * 
     * @param noteId Note ID
     * @param tagId Tag ID
     * @return true if successful, false otherwise
     */
    public boolean removeTagFromNote(int noteId, int tagId) {
        String sql = "DELETE FROM note_tags WHERE note_id = ? AND tag_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            stmt.setInt(2, tagId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete tag
     * 
     * @param id Tag ID
     * @return true if successful, false otherwise
     */
    public boolean deleteTag(int id) {
        String sql = "DELETE FROM tags WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Helper method to extract Tag from ResultSet
     * 
     * @param rs ResultSet
     * @return Tag object
     * @throws SQLException if a database access error occurs
     */
    private Tag extractTagFromResultSet(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));
        return tag;
    }
}
