package org.eustrosoft.login;

import org.eustrosoft.core.Service;
import org.eustrosoft.handlers.login.dto.LoginDTO;
import org.eustrosoft.handlers.login.dto.LoginRequestBlock;
import org.eustrosoft.handlers.login.dto.LoginResponseBlock;
import org.eustrosoft.json.exception.JsonException;
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

    public LoginResponseBlock login() throws SQLException, JsonException {
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(requestBlock.getR());
        LoginDTO data = ((LoginRequestBlock<LoginDTO>) requestBlock).getData();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(getRequest()),
                DBPoolContext.getUrl(getRequest()),
                DBPoolContext.getDriverClass(getRequest())
        );
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        if (dbps != null) {
            dbps.logout();
        }
        dbps = dbPool.logon(data.getLogin(), data.getPassword());
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

    public LoginResponseBlock logout() throws SQLException {
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(requestBlock.getR());
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(getRequest(), getResponse());
        qTisCookie.deleteCookie();
        loginResponseBlock.setData(new StringResponseData("Logout Success!"));
        return loginResponseBlock;
    }
}
