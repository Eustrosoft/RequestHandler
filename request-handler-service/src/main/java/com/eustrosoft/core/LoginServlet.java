package com.eustrosoft.core;

import com.eustrosoft.core.tools.QJson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.eustrosoft.core.Constants.SESSION_TIMEOUT;

@WebServlet(
        urlPatterns = {"/login"}
)
public class LoginServlet extends HttpServlet {

    private final String username = "admin";
    private final String password = "password";

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        QJson qJson = new QJson();
        qJson.parseJSONReader(request.getReader());
        String username = qJson.getItemString("login");
        String password = qJson.getItemString("password");

        if (this.username.equals(username) && this.password.equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(SESSION_TIMEOUT);
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}
