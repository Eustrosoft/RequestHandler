package org.eustrosoft.login;

import org.eustrosoft.core.Service;
import org.eustrosoft.handlers.login.dto.LoginRequestBlock;
import org.eustrosoft.handlers.login.dto.LoginResponseBlock;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.response.StringResponseData;
import org.eustrosoft.tools.WebParams;

import java.sql.SQLException;

public class LoginService implements Service {
    private final RequestBlock requestBlock;

    public LoginService(RequestBlock requestBlock) {
        this.requestBlock = requestBlock;
    }

    private LoginResponseBlock login() throws SQLException {
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(requestBlock.getR());
        (LoginRequestBlock)
                String login = requestBlock.getLogin();
        String password = requestBlock.getPassword();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(getRequest()),
                DBPoolContext.getUrl(getRequest()),
                DBPoolContext.getDriverClass(getRequest())
        );
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        if (dbps != null) {
            dbps.logout();
        }
        dbps = dbPool.logon(login, password);
        if (dbps == null) {
            dbps = dbPool.createSession();
        }
        QTISSessionCookie qTisCookie = new QTISSessionCookie(getRequest(), getResponse());
        Boolean debug = WebParams.getBoolean(getRequest(), WebParams.DEBUG);
        if (debug == null) {
            debug = false;
        }
        qTisCookie.set(
                dbps.getSessionSecretCookie(),
                dbps.getSessionCookieMaxAge(),
                true, debug, "/"
        );
        loginResponseBlock.setData(new StringResponseData("Logout Success!"));
        return loginResponseBlock;
    }

    private LoginResponseBlock logout() throws SQLException {
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(requestBlock.getR());
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(getRequest(), getResponse());
        qTisCookie.deleteCookie();
        loginResponseBlock.setData(new StringResponseData("Logout Success!"));
        return loginResponseBlock;
    }
}
