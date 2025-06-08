package com.studentnotes.controller;

import com.studentnotes.model.User;
import com.studentnotes.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONObject;

/**
 * Servlet for handling user authentication
 */
@WebServlet("/api/auth/*")
public class AuthController extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() {
        userService = new UserService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (pathInfo) {
            case "/user":
                handleGetUser(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void handleGetUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            String username = (String) session.getAttribute("username");
            sendJsonResponse(response, HttpServletResponse.SC_OK, "success", "User data retrieved", username);
        } else {
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "error", "Unauthorized", null);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "error", "Username and password are required", null);
            return;
        }
        
        User user = userService.authenticateUser(username, password);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            
            sendJsonResponse(response, HttpServletResponse.SC_OK, 
                    "success", "Login successful", user.getUsername());
        } else {
            sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "error", "Invalid username or password", null);
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        if (username == null || password == null || email == null || 
                username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "error", "Username, password, and email are required", null);
            return;
        }
        
        User user = userService.registerUser(username, password, email);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, 
                    "success", "Registration successful", user.getUsername());
        } else {
            sendJsonResponse(response, HttpServletResponse.SC_CONFLICT, 
                    "error", "Username already exists", null);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        sendJsonResponse(response, HttpServletResponse.SC_OK, 
                "success", "Logout successful", null);
    }
    
    private void sendJsonResponse(HttpServletResponse response, int status, 
            String type, String message, String username) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(status);
        
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", type);
        jsonResponse.put("message", message);
        if (username != null) {
            jsonResponse.put("username", username);
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toJSONString());
        out.flush();
    }
}

