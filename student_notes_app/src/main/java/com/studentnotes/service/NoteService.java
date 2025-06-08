package com.studentnotes.service;

import com.studentnotes.dao.NoteDAO;
import com.studentnotes.dao.TagDAO;
import com.studentnotes.model.Note;
import com.studentnotes.model.Tag;

import java.util.List;

/**
 * Service class for Note-related operations
 */
public class NoteService {
    private NoteDAO noteDAO;
    private TagDAO tagDAO;
    
    public NoteService() {
        this.noteDAO = new NoteDAO();
        this.tagDAO = new TagDAO();
    }
    
    /**
     * Create a new note
     * 
     * @param userId User ID
     * @param title Title
     * @param content Content
     * @param category Category
     * @return Note object if creation successful, null otherwise
     */
    public Note createNote(int userId, String title, String content, String category) {
        Note note = new Note(userId, title, content, category);
        boolean success = noteDAO.createNote(note);
        
        return success ? note : null;
    }
    
    /**
     * Get note by ID
     * 
     * @param id Note ID
     * @return Note object if found, null otherwise
     */
    public Note getNoteById(int id) {
        return noteDAO.getNoteById(id);
    }
    
    /**
     * Get all notes for a user
     * 
     * @param userId User ID
     * @return List of notes for the user
     */
    public List<Note> getNotesByUserId(int userId) {
        return noteDAO.getNotesByUserId(userId);
    }
    
    /**
     * Get notes by category for a user
     * 
     * @param userId User ID
     * @param category Category
     * @return List of notes for the user in the specified category
     */
    public List<Note> getNotesByCategory(int userId, String category) {
        return noteDAO.getNotesByCategory(userId, category);
    }
    
    /**
     * Get favorite notes for a user
     * 
     * @param userId User ID
     * @return List of favorite notes for the user
     */
    public List<Note> getFavoriteNotes(int userId) {
        return noteDAO.getFavoriteNotes(userId);
    }
    
    /**
     * Update note
     * 
     * @param note Note object to be updated
     * @return true if successful, false otherwise
     */
    public boolean updateNote(Note note) {
        return noteDAO.updateNote(note);
    }
    
    /**
     * Toggle favorite status of a note
     * 
     * @param noteId Note ID
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean toggleFavorite(int noteId, int userId) {
        return noteDAO.toggleFavorite(noteId, userId);
    }
    
    /**
     * Delete note
     * 
     * @param id Note ID
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteNote(int id, int userId) {
        return noteDAO.deleteNote(id, userId);
    }
    
    /**
     * Search notes by title or content
     * 
     * @param userId User ID
     * @param query Search query
     * @return List of notes matching the search query
     */
    public List<Note> searchNotes(int userId, String query) {
        return noteDAO.searchNotes(userId, query);
    }
    
    /**
     * Add tag to note
     * 
     * @param noteId Note ID
     * @param tagName Tag name
     * @return true if successful, false otherwise
     */
    public boolean addTagToNote(int noteId, String tagName) {
        // Get or create tag
        Tag tag = tagDAO.getTagByName(tagName);
        if (tag == null) {
            tag = new Tag(tagName);
            boolean success = tagDAO.createTag(tag);
            if (!success) {
                return false;
            }
        }
        
        return tagDAO.addTagToNote(noteId, tag.getId());
    }
    
    /**
     * Remove tag from note
     * 
     * @param noteId Note ID
     * @param tagId Tag ID
     * @return true if successful, false otherwise
     */
    public boolean removeTagFromNote(int noteId, int tagId) {
        return tagDAO.removeTagFromNote(noteId, tagId);
    }
    
    /**
     * Get tags for a note
     * 
     * @param noteId Note ID
     * @return List of tags for the note
     */
    public List<Tag> getTagsForNote(int noteId) {
        return tagDAO.getTagsForNote(noteId);
    }
    
    /**
     * Get all tags
     * 
     * @return List of all tags
     */
    public List<Tag> getAllTags() {
        return tagDAO.getAllTags();
    }
}
