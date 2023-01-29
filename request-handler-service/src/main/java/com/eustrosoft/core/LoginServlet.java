package com.eustrosoft.core;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.exception.CredentialException;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.eustrosoft.core.Constants.SESSION_TIMEOUT;

@WebServlet(
        name = "Login Servlet",
        description = "Servlet for log into system",
        urlPatterns = {"/api/login"}
)
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        QJson qJson = new QJson();
        qJson.parseJSONReader(request.getReader());
        String username = qJson.getItemString("login");
        String password = qJson.getItemString("password");

        try {
            request.login(username, password);
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(SESSION_TIMEOUT);
            String cookie = response.getHeader("Set-Cookie");
            String[] cookies = cookie.split(";");
            cookie = String.format("%s; Path=/; %s", cookies[0], cookies[2]);
            response.setHeader("Set-Cookie", cookie);
            UsersContext usersContext = UsersContext.getInstance();
            usersContext.setUserDetails(
                    newSession.getId(),
                    new User(username, password, request.getRequestedSessionId())
            );
        } catch (ServletException ex) {
            PrintWriter writer = response.getWriter();
            writer.write(new CredentialException().getMessage());
            response.setStatus(401);
            writer.flush();
            writer.close();
        }
    }
}
