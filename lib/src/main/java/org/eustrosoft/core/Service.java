package org.eustrosoft.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

public interface Service {
    default HttpServletRequest getRequest() {
        return RequestContextHolder.getAttributes().getRequest();
    }

    default HttpServletResponse getResponse() {
        return RequestContextHolder.getAttributes().getResponse();
    }

    default QDBPSession getSession() {
        QTISSessionCookie qtisSessionCookie = new QTISSessionCookie(
                getRequest(),
                getResponse()
        );
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(getRequest()),
                DBPoolContext.getUrl(getRequest()),
                DBPoolContext.getDriverClass(getRequest())
        );
        return dbPool.logon(qtisSessionCookie.getCookieValue());
    }
}
