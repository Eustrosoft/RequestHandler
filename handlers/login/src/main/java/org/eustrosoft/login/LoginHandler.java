/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.tools.WebParams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

import static org.eustrosoft.spec.Constants.*;

@Handler(SUBSYSTEM_LOGIN)
public final class LoginHandler implements BasicHandler {
    private HttpServletRequest request;
    private HttpServletResponse response;

    public LoginHandler(HttpServletResponse response, HttpServletRequest request) {
        this.request = request;
        this.response = response;
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginRequestBlock loginRequestBlock = (LoginRequestBlock) requestBlock;
        LoginResponseBlock responseBlock = new LoginResponseBlock();
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
                responseBlock.setE(404L);
                responseBlock.setM("Not yet implemented.");
                break;
        }
        return responseBlock;
    }

    @Deprecated
    public void doLogin(LoginRequestBlock requestBlock) throws ServletException {
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
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.logout();
    }

    private void login(LoginRequestBlock requestBlock) throws SQLException {
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
    }

    private void logout(LoginRequestBlock requestBlock)
            throws SQLException {
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(request), null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        qTisCookie.deleteCookie();
    }
}
