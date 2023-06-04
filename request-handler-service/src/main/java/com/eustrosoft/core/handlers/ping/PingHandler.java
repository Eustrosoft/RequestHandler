package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.context.DBPoolContext;
import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.*;

public class PingHandler implements Handler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        PingResponseBlock pingResponseBlock = new PingResponseBlock();
        HttpServletRequest httpRequest = requestBlock.getHttpRequest();
        QTISSessionCookie qtisSessionCookie = new QTISSessionCookie(
                requestBlock.getHttpRequest(),
                requestBlock.getHttpResponse()
        );
        HttpServletRequest request = requestBlock.getHttpRequest();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(qtisSessionCookie.getCookieValue());
        if (dbps.checkSecretCookie(dbps.getSecretCookie())) {
            UsersContext usersContext = UsersContext.getInstance();
            User user = usersContext.getSQLUser(dbps.getSessionSecretCookie());
            pingResponseBlock.setErrCode(ERR_OK);
            pingResponseBlock.setMessage(MSG_OK);
            if (httpRequest.getUserPrincipal() != null) {
                pingResponseBlock.setFullName(httpRequest.getUserPrincipal().getName());
            } else {
                pingResponseBlock.setFullName(user.getUserName());
            }
            pingResponseBlock.setUsername(user.getUserName());
            pingResponseBlock.setUserId(httpRequest.getRequestedSessionId());
        } else {
            pingResponseBlock.setErrCode(ERR_UNAUTHORIZED);
            pingResponseBlock.setMessage(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
