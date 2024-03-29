/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.db.dao.SamDAO;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.model.user.User;
import com.eustrosoft.core.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.constants.Constants.*;

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
            pingResponseBlock.setE(ERR_OK);
            pingResponseBlock.setM(MSG_OK);
            SamDAO samDAO = new SamDAO(dbps.getConnection());
            User user = samDAO.getUserById(samDAO.getUserId());
            pingResponseBlock.setUsername(user.getUsername());
            pingResponseBlock.setFullName(user.getFullName());
            pingResponseBlock.setDbUser(user.getDbUser());
            pingResponseBlock.setUserId(user.getId().toString());
        } else {
            pingResponseBlock.setE(ERR_UNAUTHORIZED);
            pingResponseBlock.setM(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
