package com.eustrosoft.core;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
        name = "Alive Servlet",
        description = "Servlet for checking status of server",
        urlPatterns = {"/alive"}
)
public class AliveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        PrintWriter writer = resp.getWriter();
        writer.println("Alive!");
        writer.flush();
    }
}
