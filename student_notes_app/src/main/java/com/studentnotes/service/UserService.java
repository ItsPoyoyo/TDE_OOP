package com.studentnotes.service;

import com.studentnotes.dao.UserDAO;
import com.studentnotes.model.User;

/**
 * Service class for User-related operations
 */
public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Register a new user
     * 
     * @param username Username
     * @param password Password
     * @param email Email
     * @return User object if registration successful, null otherwise
     */
    public User registerUser(String username, String password, String email) {
        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            return null;
        }
        
        User user = new User(username, password, email);
        boolean success = userDAO.createUser(user);
        
        return success ? user : null;
    }
    
    /**
     * Authenticate user
     * 
     * @param username Username
     * @param password Password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        return userDAO.authenticateUser(username, password);
    }
    
    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
    
    /**
     * Update user
     * 
     * @param user User object to be updated
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
    
    /**
     * Delete user
     * 
     * @param id User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }
}
