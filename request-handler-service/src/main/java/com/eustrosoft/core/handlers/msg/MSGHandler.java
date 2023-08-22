/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;

public final class MSGHandler implements Handler {
    private String requestType;
    private UsersContext usersContext;

    public MSGHandler(String requestType) throws Exception {
        this.requestType = requestType;
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        MSGRequestBlock msgRequestBlock = (MSGRequestBlock) requestBlock;
        MSGResponseBlock msgResponseBlock = new MSGResponseBlock();
        msgResponseBlock.setE(0);
        msgResponseBlock.setErrMsg("Ok.");
        // TODO
        return msgResponseBlock;
    }
}
