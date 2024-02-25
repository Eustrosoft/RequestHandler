/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.providers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.exceptions.SessionNotExistsException;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

public final class SessionProvider {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private QDBPSession session;

    public SessionProvider(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public QDBPSession getSession() throws SessionNotExistsException {
        try {
            renewSession();
            return this.session;
        } catch (NullPointerException ex) {
            throw new SessionNotExistsException();
        }
    }

    private void renewSession() {
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        String cookieValue = qTisCookie.getCookieValue();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        this.session = dbPool.logon(cookieValue);
        if (this.session.isSessionRenewReady()) {
            this.session.renewSession();
            qTisCookie.set(this.session.getSessionSecretCookie(), this.session.getSessionCookieMaxAge());
        }
    }
}
