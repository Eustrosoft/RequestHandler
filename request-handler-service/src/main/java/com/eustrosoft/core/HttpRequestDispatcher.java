package com.eustrosoft.core;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "EustrosoftRequestDispatcher",
        description = "Dispatches request depending on it's body dispatch value",
        urlPatterns = {"/api/dispatch"}
)
public class HttpRequestDispatcher extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("login");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");
        writer.println(
                "<form action=\"dispatch\" method=\"post\">\n" +
                        "<input type=\"submit\" value=\"Logout\" >\n" +
                        "</form>");
        writer.flush();
        writer.close();
    }
}
