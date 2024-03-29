/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.login;

import com.eustrosoft.core.constants.Constants;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.model.user.User;
import com.eustrosoft.core.providers.context.DBPoolContext;
import com.eustrosoft.core.providers.context.UsersContext;
import com.eustrosoft.core.tools.WebParams;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.Constants.REQUEST_LOGIN;
import static com.eustrosoft.core.constants.Constants.REQUEST_LOGOUT;

public final class LoginHandler implements Handler {

    public LoginHandler() {
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginRequestBlock loginRequestBlock = (LoginRequestBlock) requestBlock;
        LoginResponseBlock responseBlock = new LoginResponseBlock(loginRequestBlock.getR());
        responseBlock.setE(0);
        switch (loginRequestBlock.getR()) {
            case REQUEST_LOGIN:
                login(loginRequestBlock);
                responseBlock.setM("Login success!");
                break;
            case REQUEST_LOGOUT:
                logout(loginRequestBlock);
                responseBlock.setM("Logout success!");
                break;
            default:
                responseBlock.setE(404);
                responseBlock.setM("Not yet implemented.");
                break;
        }
        return responseBlock;
    }

    @Deprecated
    public void doLogin(LoginRequestBlock requestBlock) throws ServletException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        String login = requestBlock.getLogin();
        String password = requestBlock.getPassword();
        request.login(login, password);
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(Constants.SESSION_TIMEOUT);
    }

    @Deprecated
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
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(request), null);
        if (dbps != null) {
            dbps.logout();
        }
        dbps = dbPool.logon(login, password);
        if (dbps == null) {
            dbps = dbPool.createSession();
        }
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        Boolean debug = WebParams.getBoolean(request, WebParams.DEBUG);
        if (debug == null) {
            debug = false;
        }
        qTisCookie.set(
                dbps.getSessionSecretCookie(),
                dbps.getSessionCookieMaxAge(),
                true, debug, "/"
        );
        UsersContext usersContext = UsersContext.getInstance();
        usersContext.setUserDetails(
                dbps.getSessionSecretCookie(),
                new User("", login)
        );
    }

    private void logout(LoginRequestBlock requestBlock)
            throws SQLException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse response = requestBlock.getHttpResponse();
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(request), null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        qTisCookie.deleteCookie();
    }
}
