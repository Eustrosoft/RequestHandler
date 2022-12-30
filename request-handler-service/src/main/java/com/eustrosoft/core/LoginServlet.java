package com.eustrosoft.core;

import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.eustrosoft.core.Constants.SESSION_TIMEOUT;

@WebServlet(
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
                    new UsersContext.SQLUser(username, password)
            );
        } catch (Exception ex) {
            PrintWriter writer = response.getWriter();
            writer.write("Error while logging");
            writer.flush();
            writer.close();
        }
    }
}
