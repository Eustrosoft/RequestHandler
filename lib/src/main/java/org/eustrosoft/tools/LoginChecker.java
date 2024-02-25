package org.eustrosoft.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.util.HttpTools;

import static org.eustrosoft.constants.Constants.ERR_UNAUTHORIZED;
import static org.eustrosoft.constants.Constants.MSG_UNAUTHORIZED;
import static org.eustrosoft.constants.Constants.REQUEST_LOGIN;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_LOGIN;

public final class LoginChecker {

    public static void checkLogin(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String subsystem) throws Exception {
        if (getSession(request, response) == null && !isLoginSubsystem(subsystem)) {
            HttpTools.printError(response, getUnauthorizedResponse());
            throw new Exception(MSG_UNAUTHORIZED);
        }
    }

    public static QDBPSession getSession(HttpServletRequest request,
                                         HttpServletResponse response) {
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        String cookieValue = qTisCookie.getCookieValue();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        return dbPool.logon(cookieValue);
    }

    public static boolean isLoginSubsystem(String subsystem) {
        return SUBSYSTEM_LOGIN.equalsIgnoreCase(subsystem);
    }

    public static String getUnauthorizedResponse() {
        return HttpTools.getExceptionResponse(MSG_UNAUTHORIZED, SUBSYSTEM_LOGIN, REQUEST_LOGIN, ERR_UNAUTHORIZED);
    }
}
