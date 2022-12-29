package com.eustrosoft.core;

import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.responses.Response;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
            ResponseBlock responseBlock = new ResponseBlock() {
                @Override
                public String getSubsystem() {
                    return "login";
                }

                @Override
                public String getRequest() {
                    return "login";
                }

                @Override
                public Long getStatus() {
                    return 501L;
                }

                @Override
                public Long getQId() {
                    return 0L;
                }

                @Override
                public Short getErrCode() {
                    return 1;
                }

                @Override
                public String getErrMsg() {
                    return "Error while logging.";
                }
            };
            Response resp = new Response() {
                @Override
                public long getQTisVer() {
                    return 0;
                }

                @Override
                public List<ResponseBlock> getResponses() {
                    return List.of(responseBlock);
                }

                @Override
                public boolean getQTisEnd() {
                    return true;
                }

                @Override
                public QJson getJson() {
                    QJson json = new QJson();
                    json.addItem("qtisver", getQTisVer());
                    json.addItem("qtisend", getQTisEnd());
                    json.addItem("responses", getResponses());
                    return json;
                }
            };
            PrintWriter writer = response.getWriter();
            writer.write(resp.getJson().toJSONString());
            writer.flush();
            writer.close();
        }
    }
}
