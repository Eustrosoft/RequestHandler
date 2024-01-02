/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers.sam;

import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.db.dao.SamDAO;
import org.eustrosoft.core.handlers.Handler;
import org.eustrosoft.core.handlers.requests.RequestBlock;
import org.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import java.sql.SQLException;
import java.util.Arrays;

public final class SAMHandler implements Handler {
    private QDBPConnection poolConnection;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws SQLException {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        SAMRequestBlock samRequestBlock = (SAMRequestBlock) requestBlock;
        String requestType = samRequestBlock.getR();
        SAMResponseBlock respBlock = new SAMResponseBlock();
        respBlock.setResponseType(requestType);

        SamDAO dao = new SamDAO(poolConnection);
        switch (requestType) {
            case Constants.REQUEST_USER_ID:
                respBlock.setData(dao.getUserId().toString());
                break;
            case Constants.REQUEST_USER_LOGIN:
                respBlock.setData(dao.getUserLogin());
                break;
            case Constants.REQUEST_USER_SLVL:
                respBlock.setData(dao.getUserSLvl().toString());
                break;
            case Constants.REQUEST_USER_AVAILABLE_SLVL:
                respBlock.setData(Arrays.toString(dao.getUserAvailableSlvl()));
                break;
            case Constants.REQUEST_USER_LANG:
                respBlock.setData(dao.getUserLang());
                break;
            case Constants.REQUEST_ZSID:
                respBlock.setZsid(dao.getZsids(samRequestBlock.getType()));
                break;
            default:
                respBlock.setE(Constants.ERR_UNEXPECTED);
                respBlock.setErrMsg(Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED);
                break;
        }
        respBlock.setE(Constants.ERR_OK);
        respBlock.setErrMsg(Constants.MSG_OK);
        return respBlock;
    }
}
