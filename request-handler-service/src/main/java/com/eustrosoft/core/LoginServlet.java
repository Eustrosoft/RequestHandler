package com.eustrosoft.core;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        urlPatterns = {"/login"}
)
public class LoginServlet extends HttpServlet {

    private final String username = "admin";
    private final String password = "password";

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("pwd");

        if (this.username.equals(username) && this.password.equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(1 * 60);

            Cookie message = new Cookie("message", "Welcome");
            response.addCookie(message);
            response.sendRedirect("api/dispatch");
        } else {
            response.sendRedirect("login");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");
        writer.println("<form action=\"login\" method=\"post\">\n" +
                "\n" +
                "    Username: <input type=\"text\" name=\"username\">\n" +
                "    <br>\n" +
                "    Password: <input type=\"password\" name=\"pwd\">\n" +
                "    <br><br>\n" +
                "    <input type=\"submit\" value=\"Login\">\n" +
                "</form>");
        writer.flush();
        writer.close();
    }
}
