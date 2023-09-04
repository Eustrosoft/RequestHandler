/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.tis;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.dbdatasource.core.DBFunctions;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.SQLException;
import java.util.Arrays;

import static com.eustrosoft.core.Constants.ERR_OK;
import static com.eustrosoft.core.Constants.MSG_OK;
import static com.eustrosoft.core.Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED;
import static com.eustrosoft.core.Constants.REQUEST_USER_AVAILABLE_SLVL;
import static com.eustrosoft.core.Constants.REQUEST_USER_ID;
import static com.eustrosoft.core.Constants.REQUEST_USER_LANG;
import static com.eustrosoft.core.Constants.REQUEST_USER_LOGIN;
import static com.eustrosoft.core.Constants.REQUEST_USER_SLVL;

public final class TISHandler implements Handler {
    private QDBPConnection poolConnection;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws SQLException {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        TISRequestBlock tisRequestBlock = (TISRequestBlock) requestBlock;
        String requestType = tisRequestBlock.getRequestType();
        TISResponseBlock tisResponseBLock = new TISResponseBlock();
        tisResponseBLock.setE(ERR_OK);
        tisResponseBLock.setErrMsg(MSG_OK);
        tisResponseBLock.setResponseType(requestType);

        DBFunctions functions = new DBFunctions(poolConnection);
        switch (requestType) {
            case REQUEST_USER_ID:
                tisResponseBLock.setData(functions.getUserId());
                break;
            case REQUEST_USER_LOGIN:
                tisResponseBLock.setData(functions.getUserLogin());
                break;
            case REQUEST_USER_SLVL:
                tisResponseBLock.setData(functions.getUserSLvl().toString());
                break;
            case REQUEST_USER_AVAILABLE_SLVL:
                tisResponseBLock.setData(Arrays.toString(functions.getUserAvailableSlvl()));
                break;
            case REQUEST_USER_LANG:
                tisResponseBLock.setData(functions.getUserLang());
                break;
            default:
                tisResponseBLock.setE(ERR_OK);
                tisResponseBLock.setErrMsg(MSG_REQUEST_TYPE_NOT_SUPPORTED);
                break;
        }
        return tisResponseBLock;
    }
}
