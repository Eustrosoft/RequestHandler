package org.eustrosoft.tools;

import org.eustrosoft.providers.context.DBPoolContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.eustrosoft.spec.Constants.ERR_UNAUTHORIZED;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public final class LoginChecker {

    public static void checkLogin(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String subsystem) throws Exception {
        if (getSession(request, response) == null && !isLoginSubsystem(subsystem)) {
            HttpTools.printError(response, getUnauthorizedResponse());
            throw new Exception("Unauthorized access");
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

    public static JsonObject getUnauthorizedResponse() {
        return HttpTools.getExceptionResponse("Unauthorized", "login", "login", ERR_UNAUTHORIZED);
    }
}
