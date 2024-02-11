package org.eustrosoft.login;

import org.eustrosoft.core.Service;
import org.eustrosoft.handlers.login.dto.LoginDTO;
import org.eustrosoft.handlers.login.dto.LoginResponseBlock;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.tools.WebParams;

import java.sql.SQLException;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.MSG_OK;

public class LoginService implements Service {
    public LoginService() {
    }

    public LoginResponseBlock login(BasicRequestBlock<LoginDTO> dto)
            throws SQLException, JsonException {
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(getRequest()),
                DBPoolContext.getUrl(getRequest()),
                DBPoolContext.getDriverClass(getRequest())
        );
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(dto.getR());
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        if (dbps != null) {
            dbps.logout();
        }
        dbps = dbPool.logon(dto.getData().getLogin(), dto.getData().getPassword());
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
        loginResponseBlock.setE(ERR_OK);
        loginResponseBlock.setM(MSG_OK);
        loginResponseBlock.setL(ResponseLang.EN_US.getLang());
        return loginResponseBlock;
    }

    public LoginResponseBlock logout(BasicRequestBlock<?> dto) throws SQLException {
        LoginResponseBlock loginResponseBlock = new LoginResponseBlock(dto.getR());
        QDBPSession dbps = new QDBPSession(DBPoolContext.getDbPoolName(getRequest()), null);
        dbps.logout();
        QTISSessionCookie qTisCookie = new QTISSessionCookie(getRequest(), getResponse());
        qTisCookie.deleteCookie();
        loginResponseBlock.setE(ERR_OK);
        loginResponseBlock.setM(MSG_OK);
        loginResponseBlock.setL(ResponseLang.EN_US.getLang());
        return loginResponseBlock;
    }
}
