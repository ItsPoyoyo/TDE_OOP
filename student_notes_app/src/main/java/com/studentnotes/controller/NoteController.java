package com.studentnotes.controller;

import com.studentnotes.model.Note;
import com.studentnotes.model.Tag;
import com.studentnotes.service.NoteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;

/**
 * Servlet for handling note operations
 */
@WebServlet("/api/notes/*")
public class NoteController extends HttpServlet {
    private NoteService noteService;
    
    @Override
    public void init() {
        noteService = new NoteService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Get all notes for the user
            List<Note> notes = noteService.getNotesByUserId(userId);
            sendNotesJsonResponse(response, notes);
        } else if (pathInfo.equals("/favorites")) {
            // Get favorite notes for the user
            List<Note> notes = noteService.getFavoriteNotes(userId);
            sendNotesJsonResponse(response, notes);
        } else if (pathInfo.startsWith("/category/")) {
            // Get notes by category for the user
            String category = pathInfo.substring("/category/".length());
            List<Note> notes = noteService.getNotesByCategory(userId, category);
            sendNotesJsonResponse(response, notes);
        } else if (pathInfo.startsWith("/search/")) {
            // Search notes for the user
            String query = pathInfo.substring("/search/".length());
            List<Note> notes = noteService.searchNotes(userId, query);
            sendNotesJsonResponse(response, notes);
        } else {
            try {
                // Get note by ID
                int noteId = Integer.parseInt(pathInfo.substring(1));
                Note note = noteService.getNoteById(noteId);
                
                if (note != null && note.getUserId() == userId) {
                    // Get tags for the note
                    List<Tag> tags = noteService.getTagsForNote(noteId);
                    sendNoteJsonResponse(response, note, tags);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Create a new note
            try {
                JSONObject jsonRequest = parseJsonRequest(request);
                
                String title = (String) jsonRequest.get("title");
                String content = (String) jsonRequest.get("content");
                String category = (String) jsonRequest.get("category");
                
                if (title == null || title.trim().isEmpty()) {
                    sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "error", "Title is required");
                    return;
                }
                
                Note note = noteService.createNote(userId, title, content, category);
                
                if (note != null) {
                    // Add tags if provided
                    if (jsonRequest.containsKey("tags")) {
                        JSONArray tagsArray = (JSONArray) jsonRequest.get("tags");
                        for (Object tagObj : tagsArray) {
                            String tagName = (String) tagObj;
                            noteService.addTagToNote(note.getId(), tagName);
                        }
                    }
                    
                    sendJsonResponse(response, HttpServletResponse.SC_CREATED, 
                            "success", "Note created successfully");
                } else {
                    sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "error", "Failed to create note");
                }
            } catch (ParseException e) {
                sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "error", "Invalid JSON format");
            }
        } else if (pathInfo.startsWith("/tag/")) {
            try {
                // Add tag to note
                String[] parts = pathInfo.substring("/tag/".length()).split("/");
                int noteId = Integer.parseInt(parts[0]);
                String tagName = parts[1];
                
                Note note = noteService.getNoteById(noteId);
                
                if (note != null && note.getUserId() == userId) {
                    boolean success = noteService.addTagToNote(noteId, tagName);
                    
                    if (success) {
                        sendJsonResponse(response, HttpServletResponse.SC_OK, 
                                "success", "Tag added successfully");
                    } else {
                        sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                "error", "Failed to add tag");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int noteId = Integer.parseInt(pathInfo.substring(1));
            Note note = noteService.getNoteById(noteId);
            
            if (note != null && note.getUserId() == userId) {
                JSONObject jsonRequest = parseJsonRequest(request);
                
                String title = (String) jsonRequest.get("title");
                String content = (String) jsonRequest.get("content");
                String category = (String) jsonRequest.get("category");
                Boolean isFavorite = (Boolean) jsonRequest.get("isFavorite");
                
                if (title != null && !title.trim().isEmpty()) {
                    note.setTitle(title);
                }
                
                if (content != null) {
                    note.setContent(content);
                }
                
                if (category != null) {
                    note.setCategory(category);
                }
                
                if (isFavorite != null) {
                    note.setFavorite(isFavorite);
                }
                
                boolean success = noteService.updateNote(note);
                
                if (success) {
                    sendJsonResponse(response, HttpServletResponse.SC_OK, 
                            "success", "Note updated successfully");
                } else {
                    sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "error", "Failed to update note");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ParseException e) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "error", "Invalid JSON format");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        if (pathInfo.startsWith("/tag/")) {
            try {
                // Remove tag from note
                String[] parts = pathInfo.substring("/tag/".length()).split("/");
                int noteId = Integer.parseInt(parts[0]);
                int tagId = Integer.parseInt(parts[1]);
                
                Note note = noteService.getNoteById(noteId);
                
                if (note != null && note.getUserId() == userId) {
                    boolean success = noteService.removeTagFromNote(noteId, tagId);
                    
                    if (success) {
                        sendJsonResponse(response, HttpServletResponse.SC_OK, 
                                "success", "Tag removed successfully");
                    } else {
                        sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                "error", "Failed to remove tag");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            try {
                // Delete note
                int noteId = Integer.parseInt(pathInfo.substring(1));
                boolean success = noteService.deleteNote(noteId, userId);
                
                if (success) {
                    sendJsonResponse(response, HttpServletResponse.SC_OK, 
                            "success", "Note deleted successfully");
                } else {
                    sendJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                            "error", "Note not found or not owned by user");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
    
    private JSONObject parseJsonRequest(HttpServletRequest request) throws IOException, ParseException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(sb.toString());
    }
    
    private void sendNotesJsonResponse(HttpServletResponse response, List<Note> notes) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        
        JSONArray jsonArray = new JSONArray();
        
        for (Note note : notes) {
            JSONObject jsonNote = new JSONObject();
            jsonNote.put("id", note.getId());
            jsonNote.put("title", note.getTitle());
            jsonNote.put("content", note.getContent());
            jsonNote.put("category", note.getCategory());
            jsonNote.put("isFavorite", note.isFavorite());
            jsonNote.put("createdAt", note.getCreatedAt() != null ? note.getCreatedAt().toString() : null);
            jsonNote.put("updatedAt", note.getUpdatedAt() != null ? note.getUpdatedAt().toString() : null);
            
            jsonArray.add(jsonNote);
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toJSONString());
        out.flush();
    }
    
    private void sendNoteJsonResponse(HttpServletResponse response, Note note, List<Tag> tags) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        
        JSONObject jsonNote = new JSONObject();
        jsonNote.put("id", note.getId());
        jsonNote.put("title", note.getTitle());
        jsonNote.put("content", note.getContent());
        jsonNote.put("category", note.getCategory());
        jsonNote.put("isFavorite", note.isFavorite());
        jsonNote.put("createdAt", note.getCreatedAt() != null ? note.getCreatedAt().toString() : null);
        jsonNote.put("updatedAt", note.getUpdatedAt() != null ? note.getUpdatedAt().toString() : null);
        
        JSONArray jsonTags = new JSONArray();
        for (Tag tag : tags) {
            JSONObject jsonTag = new JSONObject();
            jsonTag.put("id", tag.getId());
            jsonTag.put("name", tag.getName());
            jsonTags.add(jsonTag);
        }
        
        jsonNote.put("tags", jsonTags);
        
        PrintWriter out = response.getWriter();
        out.print(jsonNote.toJSONString());
        out.flush();
    }
    
    private void sendJsonResponse(HttpServletResponse response, int status, 
            String type, String message) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(status);
        
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", type);
        jsonResponse.put("message", message);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toJSONString());
        out.flush();
    }
}
