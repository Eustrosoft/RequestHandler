package com.eustrosoft.core.handlers.login;

import com.eustrosoft.core.Constants;
import com.eustrosoft.core.context.DBPoolContext;
import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

import static com.eustrosoft.core.Constants.REQUEST_LOGIN;
import static com.eustrosoft.core.Constants.REQUEST_LOGOUT;

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
                login(loginRequestBlock);
                responseBlock.setErrMsg("Login success!");
                break;
            case REQUEST_LOGOUT:
                logout(loginRequestBlock);
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

    private void login(LoginRequestBlock requestBlock) throws SQLException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse response = requestBlock.getHttpResponse();
        String login = requestBlock.getLogin();
        String password = requestBlock.getPassword();
        QDBPool dbPool = DBPoolContext.getInstance(
                "DBPool1",
                "jdbc:postgresql://test:55432/test",
                "org.postgresql.Driver"
        );
        QDBPSession dbps = new QDBPSession("DBPool1", null);
        if (dbps != null) {
            dbps.logout();
        }
        dbps = dbPool.logon(login, password);
        if (dbps == null)
            dbps = dbPool.createSession();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        qTisCookie.set(
                dbps.getSessionSecretCookie(),
                dbps.getSessionCookieMaxAge(),
                true, false, "/"
        );
        dbPool.addSession(dbps);
    }

    private void logout(LoginRequestBlock requestBlock)
            throws SQLException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse response = requestBlock.getHttpResponse();
        String poolName = "DBPool1";
        QDBPSession dbps = new QDBPSession(poolName, null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        qTisCookie.deleteCookie();
    }
}
