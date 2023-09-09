/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sam;

import com.eustrosoft.core.db.core.DBFunctions;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.SQLException;
import java.util.Arrays;

import static com.eustrosoft.core.constants.Constants.*;

public final class SAMHandler implements Handler {
    private QDBPConnection poolConnection;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws SQLException {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        SAMRequestBlock SAMRequestBlock = (SAMRequestBlock) requestBlock;
        String requestType = SAMRequestBlock.getRequestType();
        SAMResponseBlock SAMResponseBLock = new SAMResponseBlock();
        SAMResponseBLock.setE(ERR_OK);
        SAMResponseBLock.setErrMsg(MSG_OK);
        SAMResponseBLock.setResponseType(requestType);

        DBFunctions functions = new DBFunctions(poolConnection);
        switch (requestType) {
            case REQUEST_USER_ID:
                SAMResponseBLock.setData(functions.getUserId().toString());
                break;
            case REQUEST_USER_LOGIN:
                SAMResponseBLock.setData(functions.getUserLogin());
                break;
            case REQUEST_USER_SLVL:
                SAMResponseBLock.setData(functions.getUserSLvl().toString());
                break;
            case REQUEST_USER_AVAILABLE_SLVL:
                SAMResponseBLock.setData(Arrays.toString(functions.getUserAvailableSlvl()));
                break;
            case REQUEST_USER_LANG:
                SAMResponseBLock.setData(functions.getUserLang());
                break;
            default:
                SAMResponseBLock.setE(ERR_OK);
                SAMResponseBLock.setErrMsg(MSG_REQUEST_TYPE_NOT_SUPPORTED);
                break;
        }
        return SAMResponseBLock;
    }
}
