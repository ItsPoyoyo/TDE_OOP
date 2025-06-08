package com.studentnotes.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/")
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getServletPath();
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);

        // Handle static resources first
        if (isStaticResource(pathInfo)) {
            request.getRequestDispatcher(pathInfo).forward(request, response);
            return;
        }

        // Handle application routes
        switch (pathInfo) {
            case "/":
                if (isLoggedIn) {
                    response.sendRedirect("dashboard.html");
                } else {
                    response.sendRedirect("login.html");
                }
                break;

            case "/login":
            case "/login.html":
                if (isLoggedIn) {
                    response.sendRedirect("dashboard.html");
                } else {
                    request.getRequestDispatcher("/login.html").forward(request, response);
                }
                break;

            case "/register":
            case "/register.html":
                if (isLoggedIn) {
                    response.sendRedirect("dashboard.html");
                } else {
                    request.getRequestDispatcher("/register.html").forward(request, response);
                }
                break;

            case "/dashboard":
            case "/dashboard.html":
                if (isLoggedIn) {
                    request.getRequestDispatcher("/dashboard.html").forward(request, response);
                } else {
                    response.sendRedirect("login.html");
                }
                break;

            case "/note":
            case "/note.html":
                if (isLoggedIn) {
                    request.getRequestDispatcher("/note.html").forward(request, response);
                } else {
                    response.sendRedirect("login.html");
                }
                break;

            default:
                // For all other cases, let the container handle it
                request.getRequestDispatcher(pathInfo).forward(request, response);
                break;
        }
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/fonts/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico");
    }
}