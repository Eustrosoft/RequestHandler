package com.eustrosoft.core.providers;

import com.eustrosoft.core.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.LOGIN_POOL;
import static com.eustrosoft.core.Constants.POSTGRES_DRIVER;

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
                LOGIN_POOL, // TODO
                DBPoolContext.getUrl(request),
                POSTGRES_DRIVER
        );
        this.session = dbPool.logon(cookieValue);
        session.renewLastUsageTS();
    }
}
