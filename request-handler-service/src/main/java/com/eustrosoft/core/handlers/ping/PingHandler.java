/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.context.DBPoolContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.ERR_OK;
import static com.eustrosoft.core.Constants.ERR_UNAUTHORIZED;
import static com.eustrosoft.core.Constants.MSG_OK;
import static com.eustrosoft.core.Constants.MSG_UNAUTHORIZED;

public class PingHandler implements Handler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        PingResponseBlock pingResponseBlock = new PingResponseBlock();
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
            pingResponseBlock.setErrCode(ERR_OK);
            pingResponseBlock.setMessage(MSG_OK);
            if (request.getUserPrincipal() != null) {
                pingResponseBlock.setFullName(request.getUserPrincipal().getName());
            }
            pingResponseBlock.setUsername(dbps.getLogin());
            pingResponseBlock.setFullName(dbps.getLogin());
        } else {
            pingResponseBlock.setErrCode(ERR_UNAUTHORIZED);
            pingResponseBlock.setMessage(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
