package com.eustrosoft.core.tools;

import com.eustrosoft.core.providers.context.DBPoolContext;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.ERR_UNAUTHORIZED;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_LOGIN;
import static com.eustrosoft.core.tools.HttpTools.getExceptionResponse;
import static com.eustrosoft.core.tools.HttpTools.printError;

public final class LoginChecker {

    @SneakyThrows
    public static void checkLogin(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String subsystem) {
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        String cookieValue = qTisCookie.getCookieValue();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession session = dbPool.logon(cookieValue);

        if (session == null && !isLoginSubsystem(subsystem)) {
            printError(response, getUnauthorizedResponse());
            throw new Exception("Unauthorized access");
        }
    }

    public static boolean isLoginSubsystem(String subsystem) {
        return SUBSYSTEM_LOGIN.equalsIgnoreCase(subsystem);
    }

    public static JsonObject getUnauthorizedResponse() {
        return getExceptionResponse("Unauthorized", "login", "login", ERR_UNAUTHORIZED);
    }
}
