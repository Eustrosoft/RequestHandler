package com.eustrosoft.core.handlers.login;

import com.eustrosoft.core.Constants;
import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.eustrosoft.core.Constants.*;

public final class LoginHandler implements Handler {
    private final String requestType;

    public LoginHandler(String requestType) {
        this.requestType = requestType;
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginRequestBlock loginRequestBlock = (LoginRequestBlock) requestBlock;
        LoginResponseBlock responseBlock = new LoginResponseBlock();
        responseBlock.setE(0);
        switch (requestType) {
            case REQUEST_LOGIN:
                doLogin(loginRequestBlock);
                responseBlock.setErrMsg("Login success!");
                break;
            case REQUEST_LOGOUT:
                doLogout(loginRequestBlock);
                responseBlock.setErrMsg("Logout success!");
                break;
            default:
                responseBlock.setE(404);
                responseBlock.setErrMsg("Not yet implemented.");
                break;
        }
        return responseBlock;
    }

    public void doLogin(LoginRequestBlock requestBlock) throws ServletException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse response = requestBlock.getHttpResponse();
        String login = requestBlock.getLogin();
        String password = requestBlock.getPassword();
        request.login(login, password);
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(Constants.SESSION_TIMEOUT);
        String cookie = response.getHeader("Set-Cookie");
        String[] cookies = cookie.split(";");
        cookie = String.format("%s; Path=/; %s", cookies[0], cookies[2]);
        response.setHeader("Set-Cookie", cookie);
        UsersContext usersContext = UsersContext.getInstance();
        usersContext.setUserDetails(
                newSession.getId(),
                new User(login, password, request.getRequestedSessionId())
        );
    }

    public void doLogout(LoginRequestBlock requestBlock) throws ServletException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            UsersContext usersContext = UsersContext.getInstance();
            usersContext.removeConnection(session.getId());
            session.invalidate();
        }
        request.logout();
    }
}
