package com.studentnotes.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/app/*")  // Only intercept URLs starting with /app/
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo(); // path after /app
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);

        // Handle root path: /app/
        if (path == null || path.equals("/")) {
            response.sendRedirect(isLoggedIn ? "/dashboard.html" : "/login.html");
            return;
        }

        // Route-based logic
        switch (path) {
            case "/dashboard":
                if (!isLoggedIn) {
                    response.sendRedirect("/login.html");
                    return;
                }
                request.getRequestDispatcher("/dashboard.html").forward(request, response);
                break;

            case "/note":
                if (!isLoggedIn) {
                    response.sendRedirect("/login.html");
                    return;
                }
                request.getRequestDispatcher("/note.html").forward(request, response);
                break;

            default:
                response.sendRedirect("/error.html");
        }
    }
}
