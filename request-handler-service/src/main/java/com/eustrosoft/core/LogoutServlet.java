package com.eustrosoft.core;

import com.eustrosoft.core.context.UsersContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(
        name = "Logout Servlet",
        description = "Servlet for logout from system",
        urlPatterns = {"/api/logout"}
)
public class LogoutServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UsersContext usersContext = UsersContext.getInstance();
            usersContext.removeConnection(session.getId());
            session.invalidate();
        }
        request.logout();
    }
}
