package com.studentnotes.dao;

import com.studentnotes.model.Note;
import com.studentnotes.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Note entity
 */
public class NoteDAO {
    
    /**
     * Create a new note
     * 
     * @param note Note object to be created
     * @return true if successful, false otherwise
     */
    public boolean createNote(Note note) {
        String sql = "INSERT INTO notes (user_id, title, content, category, is_favorite) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, note.getUserId());
            stmt.setString(2, note.getTitle());
            stmt.setString(3, note.getContent());
            stmt.setString(4, note.getCategory());
            stmt.setBoolean(5, note.isFavorite());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        note.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get note by ID
     * 
     * @param id Note ID
     * @return Note object if found, null otherwise
     */
    public Note getNoteById(int id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNoteFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all notes for a user
     * 
     * @param userId User ID
     * @return List of notes for the user
     */
    public List<Note> getNotesByUserId(int userId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? ORDER BY updated_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notes;
    }
    
    /**
     * Get notes by category for a user
     * 
     * @param userId User ID
     * @param category Category
     * @return List of notes for the user in the specified category
     */
    public List<Note> getNotesByCategory(int userId, String category) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? AND category = ? ORDER BY updated_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notes;
    }
    
    /**
     * Get favorite notes for a user
     * 
     * @param userId User ID
     * @return List of favorite notes for the user
     */
    public List<Note> getFavoriteNotes(int userId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? AND is_favorite = true ORDER BY updated_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notes;
    }
    
    /**
     * Update note
     * 
     * @param note Note object to be updated
     * @return true if successful, false otherwise
     */
    public boolean updateNote(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ?, category = ?, is_favorite = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.setString(3, note.getCategory());
            stmt.setBoolean(4, note.isFavorite());
            stmt.setInt(5, note.getId());
            stmt.setInt(6, note.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Toggle favorite status of a note
     * 
     * @param noteId Note ID
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean toggleFavorite(int noteId, int userId) {
        String sql = "UPDATE notes SET is_favorite = NOT is_favorite WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, noteId);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete note
     * 
     * @param id Note ID
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteNote(int id, int userId) {
        String sql = "DELETE FROM notes WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Search notes by title or content
     * 
     * @param userId User ID
     * @param query Search query
     * @return List of notes matching the search query
     */
    public List<Note> searchNotes(int userId, String query) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? AND (title LIKE ? OR content LIKE ?) ORDER BY updated_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchQuery = "%" + query + "%";
            stmt.setInt(1, userId);
            stmt.setString(2, searchQuery);
            stmt.setString(3, searchQuery);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return notes;
    }
    
    /**
     * Helper method to extract Note from ResultSet
     * 
     * @param rs ResultSet
     * @return Note object
     * @throws SQLException if a database access error occurs
     */
    private Note extractNoteFromResultSet(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setId(rs.getInt("id"));
        note.setUserId(rs.getInt("user_id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setCategory(rs.getString("category"));
        note.setFavorite(rs.getBoolean("is_favorite"));
        note.setCreatedAt(rs.getTimestamp("created_at"));
        note.setUpdatedAt(rs.getTimestamp("updated_at"));
        return note;
    }
}
