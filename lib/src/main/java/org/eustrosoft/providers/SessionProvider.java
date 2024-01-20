/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.providers;

import org.eustrosoft.core.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class SessionProvider {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private QDBPSession session;

    public SessionProvider(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public QDBPSession getSession() {
        renewSession();
        return this.session;
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
        //this.session.renewSession();
    }
}
